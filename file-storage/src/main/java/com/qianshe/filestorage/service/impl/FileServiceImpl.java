package com.qianshe.filestorage.service.impl;

import com.qianshe.filestorage.config.FileStorageConfig;
import com.qianshe.filestorage.dto.FileInfoResponse;
import com.qianshe.filestorage.dto.FileUploadRequest;
import com.qianshe.filestorage.entity.FileInfo;
import com.qianshe.filestorage.enums.FileAccessType;
import com.qianshe.filestorage.enums.FileStatus;
import com.qianshe.filestorage.mapper.FileInfoMapper;
import com.qianshe.filestorage.service.FileService;
import com.qianshe.filestorage.storage.StorageException;
import com.qianshe.filestorage.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文件服务实现
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileInfoMapper fileInfoMapper;
    private final StorageService storageService;
    private final FileStorageConfig fileStorageConfig;
    private final Tika tika = new Tika();

    @Override
    public FileInfoResponse uploadFile(FileUploadRequest request, Long userId) {
        try {
            MultipartFile file = request.getFile();
            
            // 验证文件
            validateFile(file);
            
            // 计算文件哈希
            String fileHash = calculateFileHash(file);
            
            // 检查是否已存在相同文件
            FileInfo existingFile = fileInfoMapper.findByFileHashAndStatus(fileHash, FileStatus.AVAILABLE);
            if (existingFile != null && !request.getOverwrite()) {
                // 返回已存在的文件信息
                return convertToResponse(existingFile);
            }
            
            // 生成存储路径
            String storagePath = generateStoragePath(file.getOriginalFilename());
            
            // 存储文件
            String actualPath = storageService.store(file, storagePath);
            
            // 创建文件信息
            FileInfo fileInfo = createFileInfo(file, actualPath, fileHash, userId, request);
            fileInfo.setStatus(FileStatus.AVAILABLE);
            
            // 保存到数据库
            fileInfoMapper.insert(fileInfo);
            
            log.info("文件上传成功: fileId={}, originalName={}, userId={}", 
                    fileInfo.getId(), file.getOriginalFilename(), userId);
            
            return convertToResponse(fileInfo);
            
        } catch (Exception e) {
            log.error("文件上传失败: originalName={}, userId={}", 
                    request.getFile().getOriginalFilename(), userId, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FileInfoResponse> uploadFiles(List<FileUploadRequest> requests, Long userId) {
        List<FileInfoResponse> responses = new ArrayList<>();
        
        for (FileUploadRequest request : requests) {
            try {
                FileInfoResponse response = uploadFile(request, userId);
                responses.add(response);
            } catch (Exception e) {
                log.error("批量上传中单个文件失败: {}", request.getFile().getOriginalFilename(), e);
                // 继续处理其他文件
            }
        }
        
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(Long fileId, Long userId) {
        FileInfo fileInfo = getFileInfoWithPermissionCheck(fileId, userId);
        
        try {
            InputStream inputStream = storageService.load(fileInfo.getStoragePath());
            
            // 更新下载次数和最后访问时间
            fileInfoMapper.incrementDownloadCount(fileId, LocalDateTime.now());
            
            log.info("文件下载: fileId={}, userId={}", fileId, userId);
            
            return new InputStreamResource(inputStream);
            
        } catch (StorageException e) {
            log.error("文件下载失败: fileId={}, userId={}", fileId, userId, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileInfoResponse getFileInfo(Long fileId, Long userId) {
        FileInfo fileInfo = getFileInfoWithPermissionCheck(fileId, userId);
        return convertToResponse(fileInfo);
    }

    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        FileInfo fileInfo = getFileInfoWithPermissionCheck(fileId, userId);
        
        try {
            // 软删除：更新状态为已删除
            fileInfo.setStatus(FileStatus.DELETED);
            fileInfo.setUpdatedAt(LocalDateTime.now());
            fileInfoMapper.updateById(fileInfo);
            
            log.info("文件删除成功: fileId={}, userId={}", fileId, userId);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: fileId={}, userId={}", fileId, userId, e);
            return false;
        }
    }

    @Override
    public List<Long> deleteFiles(List<Long> fileIds, Long userId) {
        List<Long> deletedIds = new ArrayList<>();
        
        for (Long fileId : fileIds) {
            try {
                if (deleteFile(fileId, userId)) {
                    deletedIds.add(fileId);
                }
            } catch (Exception e) {
                log.error("批量删除中单个文件失败: fileId={}", fileId, e);
            }
        }
        
        return deletedIds;
    }

    @Override
    @Transactional(readOnly = true)
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileInfoResponse> getUserFiles(Long userId, com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileInfo> page) {
        Page<FileInfo> fileInfoPage = fileInfoMapper.findByUserIdAndStatusOrderByCreatedAtDesc(
                page, userId, FileStatus.AVAILABLE);

        Page<FileInfoResponse> responsePage = new Page<>(page.getCurrent(), page.getSize());
        responsePage.setTotal(fileInfoPage.getTotal());
        responsePage.setRecords(fileInfoPage.getRecords().stream()
                .map(this::convertToResponse)
                .toList());

        return responsePage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileInfoResponse> getBusinessFiles(String businessType, String businessId, Long userId) {
        List<FileInfo> fileInfos = fileInfoMapper.findByBusinessTypeAndBusinessIdAndStatus(
                businessType, businessId, FileStatus.AVAILABLE);

        // 过滤有权限访问的文件
        return fileInfos.stream()
                .filter(fileInfo -> checkFileAccess(fileInfo, userId))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public boolean checkFileAccess(FileInfo fileInfo, Long userId) {
        if (fileInfo == null || userId == null) {
            return false;
        }
        
        // 根据访问权限类型检查
        return switch (fileInfo.getAccessType()) {
            case PUBLIC -> true; // 公开文件任何人都可以访问
            case PRIVATE -> fileInfo.getUserId().equals(userId); // 私有文件只有所有者可以访问
            case BUSINESS -> checkBusinessAccess(fileInfo, userId); // 业务文件需要业务权限
            case ADMIN -> checkAdminAccess(userId); // 管理员文件需要管理员权限
        };
    }

    @Override
    @Transactional(readOnly = true)
    public String generateAccessUrl(Long fileId, Long userId) {
        FileInfo fileInfo = getFileInfoWithPermissionCheck(fileId, userId);
        
        try {
            return storageService.getAccessUrl(fileInfo.getStoragePath());
        } catch (StorageException e) {
            log.error("生成访问URL失败: fileId={}", fileId, e);
            throw new RuntimeException("生成访问URL失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generatePresignedUrl(Long fileId, int expireSeconds, Long userId) {
        FileInfo fileInfo = getFileInfoWithPermissionCheck(fileId, userId);
        
        try {
            return storageService.getPresignedUrl(fileInfo.getStoragePath(), expireSeconds);
        } catch (StorageException e) {
            log.error("生成预签名URL失败: fileId={}", fileId, e);
            throw new RuntimeException("生成预签名URL失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileUsageStats getUserFileUsage(Long userId) {
        // 实现用户文件使用统计
        return new FileUsageStatsImpl(userId);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > fileStorageConfig.getUpload().getMaxFileSize()) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!fileStorageConfig.getUpload().getAllowedTypes().contains(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型: " + contentType);
        }
        
        // 检查文件扩展名
        String filename = file.getOriginalFilename();
        if (StringUtils.hasText(filename)) {
            String extension = getFileExtension(filename).toLowerCase();
            if (fileStorageConfig.getUpload().getForbiddenExtensions().contains(extension)) {
                throw new IllegalArgumentException("禁止的文件扩展名: " + extension);
            }
        }
    }

    /**
     * 计算文件哈希
     */
    private String calculateFileHash(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }

    /**
     * 生成存储路径
     */
    private String generateStoragePath(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String filename = System.currentTimeMillis() + "_" + System.nanoTime();
        
        if (fileStorageConfig.getLocal().getDateFolder()) {
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            return datePath + "/" + filename + "." + extension;
        } else {
            return filename + "." + extension;
        }
    }

    /**
     * 创建文件信息
     */
    private FileInfo createFileInfo(MultipartFile file, String storagePath, String fileHash, 
                                   Long userId, FileUploadRequest request) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(file.getOriginalFilename());
        fileInfo.setStoredName(getFilenameFromPath(storagePath));
        fileInfo.setFileSize(file.getSize());
        fileInfo.setMimeType(file.getContentType());
        fileInfo.setFileExtension(getFileExtension(file.getOriginalFilename()));
        fileInfo.setFileHash(fileHash);
        fileInfo.setStoragePath(storagePath);
        fileInfo.setStorageType(storageService.getStorageType());
        fileInfo.setUserId(userId);
        fileInfo.setAccessType(request.getAccessType());
        fileInfo.setBusinessType(request.getBusinessType());
        fileInfo.setBusinessId(request.getBusinessId());
        fileInfo.setExpireTime(request.getExpireTime());
        fileInfo.setExtraData(request.getExtraData());
        fileInfo.setStatus(FileStatus.UPLOADED);
        
        return fileInfo;
    }

    /**
     * 转换为响应DTO
     */
    private FileInfoResponse convertToResponse(FileInfo fileInfo) {
        FileInfoResponse response = new FileInfoResponse();
        response.setId(fileInfo.getId());
        response.setOriginalName(fileInfo.getOriginalName());
        response.setFileSize(fileInfo.getFileSize());
        response.setFileSizeFormatted(formatFileSize(fileInfo.getFileSize()));
        response.setMimeType(fileInfo.getMimeType());
        response.setFileExtension(fileInfo.getFileExtension());
        response.setStatus(fileInfo.getStatus());
        response.setAccessType(fileInfo.getAccessType());
        response.setBusinessType(fileInfo.getBusinessType());
        response.setBusinessId(fileInfo.getBusinessId());
        response.setDownloadCount(fileInfo.getDownloadCount());
        response.setLastAccessTime(fileInfo.getLastAccessTime());
        response.setExpireTime(fileInfo.getExpireTime());
        response.setCreatedAt(fileInfo.getCreatedAt());
        response.setUpdatedAt(fileInfo.getUpdatedAt());
        
        // 生成访问URL
        try {
            response.setDownloadUrl("/api/v1/files/" + fileInfo.getId());
            response.setPreviewUrl("/api/v1/files/" + fileInfo.getId() + "/preview");
        } catch (Exception e) {
            log.warn("生成访问URL失败: fileId={}", fileInfo.getId(), e);
        }
        
        return response;
    }

    /**
     * 获取文件信息并检查权限
     */
    private FileInfo getFileInfoWithPermissionCheck(Long fileId, Long userId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new IllegalArgumentException("文件不存在: " + fileId);
        }

        if (!checkFileAccess(fileInfo, userId)) {
            throw new SecurityException("无权限访问文件: " + fileId);
        }

        return fileInfo;
    }

    /**
     * 检查业务访问权限
     */
    private boolean checkBusinessAccess(FileInfo fileInfo, Long userId) {
        // TODO: 实现业务权限检查逻辑
        // 这里需要根据具体业务场景实现权限检查
        return true;
    }

    /**
     * 检查管理员权限
     */
    private boolean checkAdminAccess(Long userId) {
        // TODO: 实现管理员权限检查逻辑
        // 这里需要调用用户服务检查管理员权限
        return false;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }

    /**
     * 从路径获取文件名
     */
    private String getFilenameFromPath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        int lastSlashIndex = path.lastIndexOf('/');
        return lastSlashIndex >= 0 ? path.substring(lastSlashIndex + 1) : path;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 文件使用统计实现
     */
    private class FileUsageStatsImpl implements FileUsageStats {
        private final Long userId;

        public FileUsageStatsImpl(Long userId) {
            this.userId = userId;
        }

        @Override
        public Long getTotalFiles() {
            return fileInfoMapper.countByUserIdAndStatus(userId, FileStatus.AVAILABLE);
        }

        @Override
        public Long getTotalSize() {
            return fileInfoMapper.sumFileSizeByUserIdAndStatus(userId, FileStatus.AVAILABLE);
        }

        @Override
        public String getTotalSizeFormatted() {
            return formatFileSize(getTotalSize());
        }

        @Override
        public Long getTodayUploads() {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            return fileInfoMapper.countByUserIdAndStatusAndCreatedAtBetween(
                    userId, FileStatus.AVAILABLE, startOfDay, endOfDay);
        }

        @Override
        public Long getMonthUploads() {
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            return fileInfoMapper.countByUserIdAndStatusAndCreatedAtBetween(
                    userId, FileStatus.AVAILABLE, startOfMonth, endOfMonth);
        }
    }
}

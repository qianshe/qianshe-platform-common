package com.qianshe.filestorage.storage;

import com.qianshe.filestorage.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

/**
 * 本地存储服务实现
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage.type", havingValue = "LOCAL", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final FileStorageConfig fileStorageConfig;

    @Override
    public String store(MultipartFile file, String storagePath) throws StorageException {
        try {
            Path targetPath = getAbsolutePath(storagePath);
            
            // 创建目录
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            log.info("文件存储成功: {}", targetPath);
            return storagePath;
            
        } catch (IOException e) {
            log.error("文件存储失败: {}", storagePath, e);
            throw new StorageException("文件存储失败", e);
        }
    }

    @Override
    public String store(InputStream inputStream, String storagePath, long contentLength, String contentType) throws StorageException {
        try {
            Path targetPath = getAbsolutePath(storagePath);
            
            // 创建目录
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件流存储成功: {}", targetPath);
            return storagePath;
            
        } catch (IOException e) {
            log.error("文件流存储失败: {}", storagePath, e);
            throw new StorageException("文件流存储失败", e);
        }
    }

    @Override
    public InputStream load(String storagePath) throws StorageException {
        try {
            Path path = getAbsolutePath(storagePath);
            if (!Files.exists(path)) {
                throw new StorageException("文件不存在: " + storagePath);
            }
            return Files.newInputStream(path);
            
        } catch (IOException e) {
            log.error("文件读取失败: {}", storagePath, e);
            throw new StorageException("文件读取失败", e);
        }
    }

    @Override
    public boolean delete(String storagePath) throws StorageException {
        try {
            Path path = getAbsolutePath(storagePath);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                log.info("文件删除成功: {}", path);
            } else {
                log.warn("文件不存在，无需删除: {}", path);
            }
            
            return deleted;
            
        } catch (IOException e) {
            log.error("文件删除失败: {}", storagePath, e);
            throw new StorageException("文件删除失败", e);
        }
    }

    @Override
    public boolean exists(String storagePath) throws StorageException {
        Path path = getAbsolutePath(storagePath);
        return Files.exists(path);
    }

    @Override
    public long getFileSize(String storagePath) throws StorageException {
        try {
            Path path = getAbsolutePath(storagePath);
            if (!Files.exists(path)) {
                throw new StorageException("文件不存在: " + storagePath);
            }
            return Files.size(path);
            
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", storagePath, e);
            throw new StorageException("获取文件大小失败", e);
        }
    }

    @Override
    public String getAccessUrl(String storagePath) throws StorageException {
        return fileStorageConfig.getLocal().getUrlPrefix() + "/" + storagePath;
    }

    @Override
    public String getPresignedUrl(String storagePath, int expireSeconds) throws StorageException {
        // 本地存储不支持预签名URL，返回普通访问URL
        return getAccessUrl(storagePath);
    }

    @Override
    public boolean copy(String sourcePath, String targetPath) throws StorageException {
        try {
            Path source = getAbsolutePath(sourcePath);
            Path target = getAbsolutePath(targetPath);
            
            if (!Files.exists(source)) {
                throw new StorageException("源文件不存在: " + sourcePath);
            }
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            // 复制文件
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件复制成功: {} -> {}", source, target);
            return true;
            
        } catch (IOException e) {
            log.error("文件复制失败: {} -> {}", sourcePath, targetPath, e);
            throw new StorageException("文件复制失败", e);
        }
    }

    @Override
    public boolean move(String sourcePath, String targetPath) throws StorageException {
        try {
            Path source = getAbsolutePath(sourcePath);
            Path target = getAbsolutePath(targetPath);
            
            if (!Files.exists(source)) {
                throw new StorageException("源文件不存在: " + sourcePath);
            }
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            // 移动文件
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件移动成功: {} -> {}", source, target);
            return true;
            
        } catch (IOException e) {
            log.error("文件移动失败: {} -> {}", sourcePath, targetPath, e);
            throw new StorageException("文件移动失败", e);
        }
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }

    @Override
    public void init() throws StorageException {
        try {
            Path basePath = Paths.get(fileStorageConfig.getLocal().getBasePath());
            Files.createDirectories(basePath);
            log.info("本地存储初始化成功，存储路径: {}", basePath.toAbsolutePath());
            
        } catch (IOException e) {
            log.error("本地存储初始化失败", e);
            throw new StorageException("本地存储初始化失败", e);
        }
    }

    @Override
    public void destroy() throws StorageException {
        log.info("本地存储服务销毁");
    }

    /**
     * 获取绝对路径
     * 
     * @param storagePath 存储路径
     * @return 绝对路径
     */
    private Path getAbsolutePath(String storagePath) {
        return Paths.get(fileStorageConfig.getLocal().getBasePath(), storagePath);
    }
}

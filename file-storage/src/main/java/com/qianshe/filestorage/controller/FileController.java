package com.qianshe.filestorage.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.common.result.Result;
import com.qianshe.filestorage.dto.FileInfoResponse;
import com.qianshe.filestorage.dto.FileUploadRequest;
import com.qianshe.filestorage.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理控制器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传、下载、管理相关接口")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @SaCheckLogin
    @Operation(summary = "上传文件", description = "上传单个文件")
    public Result<FileInfoResponse> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "访问权限类型") @RequestParam(value = "accessType", required = false) String accessType,
            @Parameter(description = "业务类型") @RequestParam(value = "businessType", required = false) String businessType,
            @Parameter(description = "业务ID") @RequestParam(value = "businessId", required = false) String businessId,
            @Parameter(description = "是否覆盖同名文件") @RequestParam(value = "overwrite", defaultValue = "false") Boolean overwrite,
            @Parameter(description = "是否生成缩略图") @RequestParam(value = "generateThumbnail", defaultValue = "true") Boolean generateThumbnail) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(file);
        if (accessType != null) {
            request.setAccessType(com.qianshe.filestorage.enums.FileAccessType.valueOf(accessType));
        }
        request.setBusinessType(businessType);
        request.setBusinessId(businessId);
        request.setOverwrite(overwrite);
        request.setGenerateThumbnail(generateThumbnail);
        
        FileInfoResponse response = fileService.uploadFile(request, userId);
        return Result.ok(response);
    }

    @PostMapping("/upload/batch")
    @SaCheckLogin
    @Operation(summary = "批量上传文件", description = "批量上传多个文件")
    public Result<List<FileInfoResponse>> uploadFiles(
            @Parameter(description = "上传的文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "访问权限类型") @RequestParam(value = "accessType", required = false) String accessType,
            @Parameter(description = "业务类型") @RequestParam(value = "businessType", required = false) String businessType,
            @Parameter(description = "业务ID") @RequestParam(value = "businessId", required = false) String businessId) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        List<FileUploadRequest> requests = files.stream().map(file -> {
            FileUploadRequest request = new FileUploadRequest();
            request.setFile(file);
            if (accessType != null) {
                request.setAccessType(com.qianshe.filestorage.enums.FileAccessType.valueOf(accessType));
            }
            request.setBusinessType(businessType);
            request.setBusinessId(businessId);
            return request;
        }).toList();
        
        List<FileInfoResponse> responses = fileService.uploadFiles(requests, userId);
        return Result.ok(responses);
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "下载文件", description = "根据文件ID下载文件")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Long userId = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录用户，userId为null，由服务层判断是否允许访问
        }
        
        Resource resource = fileService.downloadFile(fileId, userId);
        FileInfoResponse fileInfo = fileService.getFileInfo(fileId, userId);
        
        // 设置响应头
        String filename = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{fileId}/preview")
    @Operation(summary = "预览文件", description = "在线预览文件（主要用于图片）")
    public ResponseEntity<Resource> previewFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Long userId = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录用户，userId为null，由服务层判断是否允许访问
        }
        
        Resource resource = fileService.downloadFile(fileId, userId);
        FileInfoResponse fileInfo = fileService.getFileInfo(fileId, userId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @GetMapping("/{fileId}/info")
    @Operation(summary = "获取文件信息", description = "获取文件的详细信息")
    public Result<FileInfoResponse> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Long userId = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录用户，userId为null，由服务层判断是否允许访问
        }
        
        FileInfoResponse response = fileService.getFileInfo(fileId, userId);
        return Result.ok(response);
    }

    @DeleteMapping("/{fileId}")
    @SaCheckLogin
    @Operation(summary = "删除文件", description = "删除指定的文件")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        boolean deleted = fileService.deleteFile(fileId, userId);
        return Result.ok(deleted);
    }

    @DeleteMapping("/batch")
    @SaCheckLogin
    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    public Result<List<Long>> deleteFiles(
            @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        List<Long> deletedIds = fileService.deleteFiles(fileIds, userId);
        return Result.ok(deletedIds);
    }

    @GetMapping("/my")
    @SaCheckLogin
    @Operation(summary = "获取我的文件列表", description = "分页获取当前用户的文件列表")
    public Result<Page<FileInfoResponse>> getMyFiles(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "20") Long size) {

        Long userId = StpUtil.getLoginIdAsLong();
        Page<com.qianshe.filestorage.entity.FileInfo> page = new Page<>(current, size);
        Page<FileInfoResponse> files = fileService.getUserFiles(userId, page);
        return Result.ok(files);
    }

    @GetMapping("/business")
    @Operation(summary = "获取业务文件列表", description = "获取指定业务的文件列表")
    public Result<List<FileInfoResponse>> getBusinessFiles(
            @Parameter(description = "业务类型") @RequestParam String businessType,
            @Parameter(description = "业务ID") @RequestParam String businessId) {
        
        Long userId = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录用户，userId为null，由服务层判断是否允许访问
        }
        
        List<FileInfoResponse> files = fileService.getBusinessFiles(businessType, businessId, userId);
        return Result.ok(files);
    }

    @GetMapping("/{fileId}/url")
    @Operation(summary = "获取文件访问URL", description = "获取文件的访问URL")
    public Result<String> getFileUrl(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Long userId = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 未登录用户，userId为null，由服务层判断是否允许访问
        }
        
        String url = fileService.generateAccessUrl(fileId, userId);
        return Result.ok(url);
    }

    @GetMapping("/{fileId}/presigned-url")
    @SaCheckLogin
    @Operation(summary = "获取预签名URL", description = "获取文件的预签名URL（用于直接访问）")
    public Result<String> getPresignedUrl(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "过期时间（秒）") @RequestParam(defaultValue = "3600") int expireSeconds) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.generatePresignedUrl(fileId, expireSeconds, userId);
        return Result.ok(url);
    }

    @GetMapping("/usage")
    @SaCheckLogin
    @Operation(summary = "获取文件使用统计", description = "获取当前用户的文件使用统计信息")
    public Result<FileService.FileUsageStats> getFileUsage() {
        Long userId = StpUtil.getLoginIdAsLong();
        FileService.FileUsageStats stats = fileService.getUserFileUsage(userId);
        return Result.ok(stats);
    }
}

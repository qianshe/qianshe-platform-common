package com.qianshe.filestorage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.filestorage.dto.FileInfoResponse;
import com.qianshe.filestorage.dto.FileUploadRequest;
import com.qianshe.filestorage.entity.FileInfo;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 文件服务接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface FileService {

    /**
     * 上传文件
     * 
     * @param request 上传请求
     * @param userId 用户ID
     * @return 文件信息
     */
    FileInfoResponse uploadFile(FileUploadRequest request, Long userId);

    /**
     * 批量上传文件
     * 
     * @param requests 上传请求列表
     * @param userId 用户ID
     * @return 文件信息列表
     */
    List<FileInfoResponse> uploadFiles(List<FileUploadRequest> requests, Long userId);

    /**
     * 下载文件
     * 
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限检查）
     * @return 文件资源
     */
    Resource downloadFile(Long fileId, Long userId);

    /**
     * 获取文件信息
     * 
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限检查）
     * @return 文件信息
     */
    FileInfoResponse getFileInfo(Long fileId, Long userId);

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限检查）
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 批量删除文件
     * 
     * @param fileIds 文件ID列表
     * @param userId 用户ID（用于权限检查）
     * @return 删除成功的文件ID列表
     */
    List<Long> deleteFiles(List<Long> fileIds, Long userId);

    /**
     * 获取用户文件列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 文件列表
     */
    Page<FileInfoResponse> getUserFiles(Long userId, Page<com.qianshe.filestorage.entity.FileInfo> page);

    /**
     * 根据业务类型获取文件列表
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param userId 用户ID（用于权限检查）
     * @return 文件列表
     */
    List<FileInfoResponse> getBusinessFiles(String businessType, String businessId, Long userId);

    /**
     * 检查文件访问权限
     * 
     * @param fileInfo 文件信息
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean checkFileAccess(FileInfo fileInfo, Long userId);

    /**
     * 生成文件访问URL
     * 
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限检查）
     * @return 访问URL
     */
    String generateAccessUrl(Long fileId, Long userId);

    /**
     * 生成文件预签名URL
     * 
     * @param fileId 文件ID
     * @param expireSeconds 过期时间（秒）
     * @param userId 用户ID（用于权限检查）
     * @return 预签名URL
     */
    String generatePresignedUrl(Long fileId, int expireSeconds, Long userId);

    /**
     * 统计用户文件使用情况
     * 
     * @param userId 用户ID
     * @return 使用情况统计
     */
    FileUsageStats getUserFileUsage(Long userId);

    /**
     * 文件使用情况统计
     */
    interface FileUsageStats {
        /**
         * 文件总数
         */
        Long getTotalFiles();

        /**
         * 文件总大小（字节）
         */
        Long getTotalSize();

        /**
         * 文件总大小（格式化）
         */
        String getTotalSizeFormatted();

        /**
         * 今日上传文件数
         */
        Long getTodayUploads();

        /**
         * 本月上传文件数
         */
        Long getMonthUploads();
    }
}

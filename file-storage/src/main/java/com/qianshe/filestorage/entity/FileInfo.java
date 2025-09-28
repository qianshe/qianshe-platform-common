package com.qianshe.filestorage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qianshe.filestorage.enums.FileAccessType;
import com.qianshe.filestorage.enums.FileStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info")
public class FileInfo {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储文件名
     */
    @TableField("stored_name")
    private String storedName;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 文件扩展名
     */
    @TableField("file_extension")
    private String fileExtension;

    /**
     * 文件哈希值（用于去重和完整性校验）
     */
    @TableField("file_hash")
    private String fileHash;

    /**
     * 文件状态
     */
    @TableField("status")
    private FileStatus status = FileStatus.UPLOADING;

    /**
     * 访问权限类型
     */
    @TableField("access_type")
    private FileAccessType accessType = FileAccessType.PRIVATE;

    /**
     * 文件所有者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 业务ID
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 存储路径
     */
    @TableField("storage_path")
    private String storagePath;

    /**
     * 存储类型（LOCAL/OSS/COS等）
     */
    @TableField("storage_type")
    private String storageType = "LOCAL";

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Long downloadCount = 0L;

    /**
     * 最后访问时间
     */
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 扩展数据（JSON格式）
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

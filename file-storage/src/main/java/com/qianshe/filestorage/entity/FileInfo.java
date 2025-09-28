package com.qianshe.filestorage.entity;

import com.qianshe.filestorage.enums.FileAccessType;
import com.qianshe.filestorage.enums.FileStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "file_info", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_business", columnList = "business_type,business_id"),
    @Index(name = "idx_hash", columnList = "file_hash"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class FileInfo {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 原始文件名
     */
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    /**
     * 存储文件名
     */
    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * MIME类型
     */
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    /**
     * 文件扩展名
     */
    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    /**
     * 文件哈希值（用于去重和完整性校验）
     */
    @Column(name = "file_hash", nullable = false, length = 64)
    private String fileHash;

    /**
     * 文件状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FileStatus status = FileStatus.UPLOADING;

    /**
     * 访问权限类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false, length = 20)
    private FileAccessType accessType = FileAccessType.PRIVATE;

    /**
     * 文件所有者ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 业务类型
     */
    @Column(name = "business_type", length = 50)
    private String businessType;

    /**
     * 业务ID
     */
    @Column(name = "business_id", length = 100)
    private String businessId;

    /**
     * 存储路径
     */
    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    /**
     * 存储类型（LOCAL/OSS/COS等）
     */
    @Column(name = "storage_type", nullable = false, length = 20)
    private String storageType = "LOCAL";

    /**
     * 下载次数
     */
    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;

    /**
     * 最后访问时间
     */
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 扩展数据（JSON格式）
     */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

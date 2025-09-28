package com.qianshe.filestorage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 文件访问日志实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "file_access_log", indexes = {
    @Index(name = "idx_file_id", columnList = "file_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class FileAccessLog {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件ID
     */
    @Column(name = "file_id", nullable = false)
    private Long fileId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 操作类型（UPLOAD/DOWNLOAD/DELETE/VIEW等）
     */
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    /**
     * 客户端IP地址
     */
    @Column(name = "client_ip", length = 45)
    private String clientIp;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 请求来源
     */
    @Column(name = "referer", length = 500)
    private String referer;

    /**
     * 响应状态码
     */
    @Column(name = "response_status")
    private Integer responseStatus;

    /**
     * 响应时间（毫秒）
     */
    @Column(name = "response_time")
    private Long responseTime;

    /**
     * 传输字节数
     */
    @Column(name = "bytes_transferred")
    private Long bytesTransferred;

    /**
     * 错误信息
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

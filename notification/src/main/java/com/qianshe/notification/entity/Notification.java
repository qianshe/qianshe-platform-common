package com.qianshe.notification.entity;

import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 通知实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 通知类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    /**
     * 通知渠道
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    /**
     * 接收用户ID
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 发送用户ID（可为空，系统通知时为空）
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 通知标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 通知内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private Long templateId;

    /**
     * 模板参数（JSON格式）
     */
    @Column(name = "template_params", columnDefinition = "TEXT")
    private String templateParams;

    /**
     * 通知状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    /**
     * 业务ID（关联的业务对象ID）
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 业务类型（关联的业务类型）
     */
    @Column(name = "business_type", length = 50)
    private String businessType;

    /**
     * 扩展数据（JSON格式）
     */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    /**
     * 读取时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * 失败原因
     */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    /**
     * 重试次数
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /**
     * 最大重试次数
     */
    @Column(name = "max_retry_count", nullable = false)
    private Integer maxRetryCount = 3;

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

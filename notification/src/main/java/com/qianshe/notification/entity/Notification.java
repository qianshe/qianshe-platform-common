package com.qianshe.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知实体
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notification")
public class Notification {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知类型
     */
    @TableField("type")
    private NotificationType type;

    /**
     * 通知渠道
     */
    @TableField("channel")
    private NotificationChannel channel;

    /**
     * 接收用户ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 发送用户ID（可为空，系统通知时为空）
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 通知标题
     */
    @TableField("title")
    private String title;

    /**
     * 通知内容
     */
    @TableField("content")
    private String content;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 模板参数（JSON格式）
     */
    @TableField("template_params")
    private String templateParams;

    /**
     * 通知状态
     */
    @TableField("status")
    private NotificationStatus status = NotificationStatus.PENDING;

    /**
     * 业务ID（关联的业务对象ID）
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 业务类型（关联的业务类型）
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 扩展数据（JSON格式）
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 读取时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 失败原因
     */
    @TableField("failure_reason")
    private String failureReason;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount = 0;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_count")
    private Integer maxRetryCount = 3;

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

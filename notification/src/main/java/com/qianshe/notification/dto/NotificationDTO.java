package com.qianshe.notification.dto;

import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知数据传输对象
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "通知信息")
public class NotificationDTO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知类型")
    private NotificationType type;

    @Schema(description = "通知渠道")
    private NotificationChannel channel;

    @Schema(description = "接收用户ID")
    private Long receiverId;

    @Schema(description = "发送用户ID")
    private Long senderId;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "通知状态")
    private NotificationStatus status;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "扩展数据")
    private String extraData;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "读取时间")
    private LocalDateTime readTime;

    @Schema(description = "失败原因")
    private String failureReason;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}

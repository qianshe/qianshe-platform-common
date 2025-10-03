package com.qianshe.notification.dto;

import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 通知消息DTO
 * 
 * 用于RabbitMQ消息传输的数据对象
 * 包含发送通知所需的所有信息
 *
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知类型
     */
    private NotificationType type;

    /**
     * 通知渠道
     */
    private NotificationChannel channel;

    /**
     * 接收用户ID
     */
    private Long receiverId;

    /**
     * 发送用户ID（可为空，系统通知时为空）
     */
    private Long senderId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 模板ID（可选）
     */
    private Long templateId;

    /**
     * 模板参数（可选）
     */
    private Map<String, Object> templateParams;

    /**
     * 业务ID（关联的业务对象ID）
     */
    private String businessId;

    /**
     * 业务类型（关联的业务类型）
     */
    private String businessType;

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    /**
     * 接收者邮箱（邮件通知时必填）
     */
    private String email;

    /**
     * 接收者手机号（短信通知时必填）
     */
    private String phone;

    /**
     * 微信OpenID（微信通知时必填）
     */
    private String wechatOpenId;

    /**
     * 推送设备Token（推送通知时必填）
     */
    private String pushToken;

    /**
     * 优先级（1-10，数字越大优先级越高）
     */
    private Integer priority = 5;

    /**
     * 是否需要持久化到数据库
     */
    private Boolean needPersist = true;

    /**
     * 重试次数（内部使用）
     */
    private Integer retryCount = 0;
}


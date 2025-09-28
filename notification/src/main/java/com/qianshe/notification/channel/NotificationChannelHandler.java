package com.qianshe.notification.channel;

import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;

/**
 * 通知渠道处理器接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface NotificationChannelHandler {

    /**
     * 获取支持的渠道类型
     */
    NotificationChannel getSupportedChannel();

    /**
     * 发送通知
     */
    boolean sendNotification(Notification notification);

    /**
     * 检查渠道是否可用
     */
    boolean isAvailable();

    /**
     * 获取渠道名称
     */
    String getChannelName();
}

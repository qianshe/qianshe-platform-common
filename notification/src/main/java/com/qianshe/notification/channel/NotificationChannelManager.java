package com.qianshe.notification.channel;

import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;

/**
 * 通知渠道管理器接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface NotificationChannelManager {

    /**
     * 发送通知
     */
    boolean sendNotification(Notification notification);

    /**
     * 注册通知渠道
     */
    void registerChannel(NotificationChannel channel, NotificationChannelHandler handler);

    /**
     * 获取渠道处理器
     */
    NotificationChannelHandler getChannelHandler(NotificationChannel channel);

    /**
     * 检查渠道是否可用
     */
    boolean isChannelAvailable(NotificationChannel channel);
}

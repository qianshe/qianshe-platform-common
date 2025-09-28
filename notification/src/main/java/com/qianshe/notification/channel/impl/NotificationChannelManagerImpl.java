package com.qianshe.notification.channel.impl;

import com.qianshe.notification.channel.NotificationChannelHandler;
import com.qianshe.notification.channel.NotificationChannelManager;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知渠道管理器实现类
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
public class NotificationChannelManagerImpl implements NotificationChannelManager {

    private final Map<NotificationChannel, NotificationChannelHandler> channelHandlers = new HashMap<>();

    /**
     * 自动注册所有渠道处理器
     */
    @Autowired
    public void registerChannelHandlers(List<NotificationChannelHandler> handlers) {
        for (NotificationChannelHandler handler : handlers) {
            registerChannel(handler.getSupportedChannel(), handler);
            log.info("注册通知渠道处理器: {} - {}", handler.getSupportedChannel(), handler.getChannelName());
        }
    }

    @Override
    public boolean sendNotification(Notification notification) {
        NotificationChannel channel = notification.getChannel();
        NotificationChannelHandler handler = getChannelHandler(channel);
        
        if (handler == null) {
            log.error("未找到渠道处理器: {}", channel);
            return false;
        }
        
        if (!handler.isAvailable()) {
            log.error("渠道不可用: {}", channel);
            return false;
        }
        
        try {
            return handler.sendNotification(notification);
        } catch (Exception e) {
            log.error("发送通知失败: channel={}, notificationId={}", channel, notification.getId(), e);
            return false;
        }
    }

    @Override
    public void registerChannel(NotificationChannel channel, NotificationChannelHandler handler) {
        channelHandlers.put(channel, handler);
    }

    @Override
    public NotificationChannelHandler getChannelHandler(NotificationChannel channel) {
        return channelHandlers.get(channel);
    }

    @Override
    public boolean isChannelAvailable(NotificationChannel channel) {
        NotificationChannelHandler handler = getChannelHandler(channel);
        return handler != null && handler.isAvailable();
    }
}

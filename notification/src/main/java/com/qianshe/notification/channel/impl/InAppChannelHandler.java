package com.qianshe.notification.channel.impl;

import com.qianshe.notification.channel.NotificationChannelHandler;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 站内信渠道处理器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
public class InAppChannelHandler implements NotificationChannelHandler {

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        log.info("发送站内信通知: receiverId={}, title={}", 
                notification.getReceiverId(), notification.getTitle());
        
        try {
            // 站内信通知实际上就是保存到数据库，这里可以添加额外的处理逻辑
            // 比如推送到WebSocket、更新缓存等
            
            // 模拟发送成功
            log.info("站内信发送成功: notificationId={}", notification.getId());
            return true;
            
        } catch (Exception e) {
            log.error("站内信发送失败: notificationId={}", notification.getId(), e);
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        // 站内信渠道总是可用的
        return true;
    }

    @Override
    public String getChannelName() {
        return "站内信";
    }
}

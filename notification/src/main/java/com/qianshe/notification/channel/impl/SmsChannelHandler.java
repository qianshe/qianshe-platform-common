package com.qianshe.notification.channel.impl;

import com.qianshe.notification.channel.NotificationChannelHandler;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 短信渠道处理器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "notification.sms.enabled", havingValue = "true")
public class SmsChannelHandler implements NotificationChannelHandler {

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        log.info("发送短信通知: receiverId={}, title={}", 
                notification.getReceiverId(), notification.getTitle());
        
        try {
            // 这里需要根据receiverId获取用户手机号
            String phoneNumber = getUserPhoneNumber(notification.getReceiverId());
            
            if (phoneNumber == null) {
                log.warn("用户手机号为空: receiverId={}", notification.getReceiverId());
                return false;
            }
            
            // 调用短信服务发送短信
            boolean success = sendSms(phoneNumber, notification.getContent());
            
            if (success) {
                log.info("短信发送成功: notificationId={}, phone={}", notification.getId(), phoneNumber);
            } else {
                log.warn("短信发送失败: notificationId={}, phone={}", notification.getId(), phoneNumber);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("短信发送失败: notificationId={}", notification.getId(), e);
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        // 检查短信服务是否可用
        // 这里简化处理，实际应该检查短信服务配置和连接状态
        return true;
    }

    @Override
    public String getChannelName() {
        return "短信";
    }

    /**
     * 获取用户手机号
     * 实际应该调用用户服务获取
     */
    private String getUserPhoneNumber(Long userId) {
        // TODO: 调用用户服务获取手机号
        // 这里返回测试手机号
        return "13800138000";
    }

    /**
     * 发送短信
     * 实际应该调用短信服务提供商的API
     */
    private boolean sendSms(String phoneNumber, String content) {
        // TODO: 集成短信服务提供商（如阿里云、腾讯云等）
        // 这里模拟发送成功
        log.info("模拟发送短信: phone={}, content={}", phoneNumber, content);
        return true;
    }
}

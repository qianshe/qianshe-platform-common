package com.qianshe.notification.channel.impl;

import com.qianshe.notification.channel.NotificationChannelHandler;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件渠道处理器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.mail.host")
public class EmailChannelHandler implements NotificationChannelHandler {

    private final JavaMailSender mailSender;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        log.info("发送邮件通知: receiverId={}, title={}", 
                notification.getReceiverId(), notification.getTitle());
        
        try {
            // 这里需要根据receiverId获取用户邮箱地址
            // 简化处理，实际应该调用用户服务获取邮箱
            String toEmail = getUserEmail(notification.getReceiverId());
            
            if (toEmail == null) {
                log.warn("用户邮箱地址为空: receiverId={}", notification.getReceiverId());
                return false;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            log.info("邮件发送成功: notificationId={}, email={}", notification.getId(), toEmail);
            return true;
            
        } catch (Exception e) {
            log.error("邮件发送失败: notificationId={}", notification.getId(), e);
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            // 检查邮件服务是否可用
            return mailSender != null;
        } catch (Exception e) {
            log.error("邮件服务不可用", e);
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "邮件";
    }

    /**
     * 获取用户邮箱地址
     * 实际应该调用用户服务获取
     */
    private String getUserEmail(Long userId) {
        // TODO: 调用用户服务获取邮箱地址
        // 这里返回测试邮箱
        return "test@example.com";
    }
}

package com.qianshe.notification.config;

import com.qianshe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务配置
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.scheduled.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledTaskConfig {

    private final NotificationService notificationService;

    /**
     * 重试失败的通知
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void retryFailedNotifications() {
        try {
            log.debug("开始执行失败通知重试任务");
            notificationService.retryFailedNotifications();
        } catch (Exception e) {
            log.error("执行失败通知重试任务异常", e);
        }
    }

    /**
     * 清理已读的历史通知
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupReadNotifications() {
        try {
            log.info("开始执行历史通知清理任务");
            // 清理30天前的已读通知
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(30);
            int cleanedCount = notificationService.cleanupReadNotifications(beforeTime);
            log.info("历史通知清理完成，清理数量: {}", cleanedCount);
        } catch (Exception e) {
            log.error("执行历史通知清理任务异常", e);
        }
    }
}

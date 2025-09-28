package com.qianshe.notification.service;

import com.qianshe.notification.dto.*;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface NotificationService {

    /**
     * 发送单个通知
     */
    Notification sendNotification(SendNotificationRequest request);

    /**
     * 批量发送通知
     */
    List<Notification> batchSendNotifications(BatchSendRequest request);

    /**
     * 异步发送通知
     */
    void sendNotificationAsync(SendNotificationRequest request);

    /**
     * 异步批量发送通知
     */
    void batchSendNotificationsAsync(BatchSendRequest request);

    /**
     * 根据模板发送通知
     */
    Notification sendByTemplate(String templateCode, List<Long> receiverIds, 
                               List<NotificationChannel> channels, 
                               java.util.Map<String, Object> templateParams,
                               String businessId, String businessType);

    /**
     * 分页查询用户通知
     */
    Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable);

    /**
     * 分页查询用户指定状态的通知
     */
    Page<NotificationDTO> getUserNotificationsByStatus(Long userId, NotificationStatus status, Pageable pageable);

    /**
     * 分页查询用户指定类型的通知
     */
    Page<NotificationDTO> getUserNotificationsByType(Long userId, NotificationType type, Pageable pageable);

    /**
     * 分页查询用户指定渠道的通知
     */
    Page<NotificationDTO> getUserNotificationsByChannel(Long userId, NotificationChannel channel, Pageable pageable);

    /**
     * 获取用户未读通知数量
     */
    long getUnreadCount(Long userId);

    /**
     * 获取用户指定类型的未读通知数量
     */
    long getUnreadCountByType(Long userId, NotificationType type);

    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long userId, List<Long> notificationIds);

    /**
     * 标记用户所有通知为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 根据ID获取通知详情
     */
    NotificationDTO getNotificationById(Long id);

    /**
     * 根据业务ID和类型查询通知
     */
    List<NotificationDTO> getNotificationsByBusiness(String businessId, String businessType);

    /**
     * 重试失败的通知
     */
    void retryFailedNotifications();

    /**
     * 清理已读的历史通知
     */
    int cleanupReadNotifications(LocalDateTime beforeTime);

    /**
     * 获取通知统计信息
     */
    NotificationStatisticsDTO getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户通知统计信息
     */
    NotificationStatisticsDTO getUserStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 取消待发送的通知
     */
    boolean cancelNotification(Long notificationId);

    /**
     * 批量取消通知
     */
    int cancelNotifications(List<Long> notificationIds);
}

package com.qianshe.notification.service.impl;

import com.qianshe.notification.dto.*;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import com.qianshe.notification.repository.NotificationRepository;
import com.qianshe.notification.repository.NotificationTemplateRepository;
import com.qianshe.notification.service.NotificationService;
import com.qianshe.notification.service.NotificationTemplateService;
import com.qianshe.notification.channel.NotificationChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationTemplateService templateService;
    private final NotificationChannelManager channelManager;

    @Override
    @Transactional
    public Notification sendNotification(SendNotificationRequest request) {
        log.info("发送通知请求: {}", request);
        request.validate();

        // 如果只有一个接收者和一个渠道，直接发送
        if (request.getReceiverIds().size() == 1 && request.getChannels().size() == 1) {
            return createAndSendNotification(request, request.getReceiverIds().get(0), request.getChannels().get(0));
        }

        // 多接收者或多渠道，选择第一个组合发送
        Long receiverId = request.getReceiverIds().get(0);
        NotificationChannel channel = request.getChannels().get(0);
        return createAndSendNotification(request, receiverId, channel);
    }

    @Override
    @Transactional
    public List<Notification> batchSendNotifications(BatchSendRequest request) {
        log.info("批量发送通知请求: {}", request);
        
        List<Notification> notifications = new ArrayList<>();
        for (SendNotificationRequest notificationRequest : request.getNotifications()) {
            try {
                notificationRequest.validate();
                // 为每个接收者和渠道创建通知
                for (Long receiverId : notificationRequest.getReceiverIds()) {
                    for (NotificationChannel channel : notificationRequest.getChannels()) {
                        Notification notification = createAndSendNotification(notificationRequest, receiverId, channel);
                        notifications.add(notification);
                    }
                }
            } catch (Exception e) {
                log.error("批量发送通知失败: {}", notificationRequest, e);
            }
        }
        
        return notifications;
    }

    @Override
    @Async
    public void sendNotificationAsync(SendNotificationRequest request) {
        try {
            sendNotification(request);
        } catch (Exception e) {
            log.error("异步发送通知失败: {}", request, e);
        }
    }

    @Override
    @Async
    public void batchSendNotificationsAsync(BatchSendRequest request) {
        try {
            batchSendNotifications(request);
        } catch (Exception e) {
            log.error("异步批量发送通知失败: {}", request, e);
        }
    }

    @Override
    @Transactional
    public Notification sendByTemplate(String templateCode, List<Long> receiverIds, 
                                     List<NotificationChannel> channels, 
                                     Map<String, Object> templateParams,
                                     String businessId, String businessType) {
        log.info("根据模板发送通知: templateCode={}, receiverIds={}, channels={}", 
                templateCode, receiverIds, channels);

        // 获取模板
        NotificationTemplate template = templateRepository.findByTemplateCodeAndEnabled(templateCode, true)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在或已禁用: " + templateCode));

        // 构建发送请求
        SendNotificationRequest request = new SendNotificationRequest();
        request.setType(template.getType());
        request.setChannels(channels);
        request.setReceiverIds(receiverIds);
        request.setTemplateCode(templateCode);
        request.setTemplateParams(templateParams);
        request.setBusinessId(businessId);
        request.setBusinessType(businessType);

        return sendNotification(request);
    }

    @Override
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToDTO);
    }

    @Override
    public Page<NotificationDTO> getUserNotificationsByStatus(Long userId, NotificationStatus status, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return notifications.map(this::convertToDTO);
    }

    @Override
    public Page<NotificationDTO> getUserNotificationsByType(Long userId, NotificationType type, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        return notifications.map(this::convertToDTO);
    }

    @Override
    public Page<NotificationDTO> getUserNotificationsByChannel(Long userId, NotificationChannel channel, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverIdAndChannelOrderByCreatedAtDesc(userId, channel, pageable);
        return notifications.map(this::convertToDTO);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndStatus(userId, NotificationStatus.SUCCESS);
    }

    @Override
    public long getUnreadCountByType(Long userId, NotificationType type) {
        return notificationRepository.countByReceiverIdAndTypeAndStatus(userId, type, NotificationStatus.SUCCESS);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long userId, List<Long> notificationIds) {
        if (CollectionUtils.isEmpty(notificationIds)) {
            return false;
        }
        
        int updatedCount = notificationRepository.markAsRead(userId, notificationIds, 
                NotificationStatus.READ, LocalDateTime.now());
        return updatedCount > 0;
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long userId) {
        int updatedCount = notificationRepository.markAllAsRead(userId, 
                NotificationStatus.READ, NotificationStatus.SUCCESS, LocalDateTime.now());
        return updatedCount > 0;
    }

    @Override
    public NotificationDTO getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<NotificationDTO> getNotificationsByBusiness(String businessId, String businessType) {
        List<Notification> notifications = notificationRepository.findByBusinessIdAndBusinessType(businessId, businessType);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建并发送通知
     */
    private Notification createAndSendNotification(SendNotificationRequest request, Long receiverId, NotificationChannel channel) {
        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setChannel(channel);
        notification.setReceiverId(receiverId);
        notification.setSenderId(request.getSenderId());
        notification.setBusinessId(request.getBusinessId());
        notification.setBusinessType(request.getBusinessType());
        notification.setMaxRetryCount(request.getMaxRetryCount());

        // 处理模板或直接内容
        if (StringUtils.hasText(request.getTemplateCode())) {
            processTemplate(notification, request);
        } else {
            notification.setTitle(request.getTitle());
            notification.setContent(request.getContent());
        }

        // 设置扩展数据
        if (request.getExtraData() != null) {
            // 这里应该将Map转换为JSON字符串，简化处理
            notification.setExtraData(request.getExtraData().toString());
        }

        // 保存通知
        notification = notificationRepository.save(notification);

        // 发送通知
        sendNotificationToChannel(notification);

        return notification;
    }

    /**
     * 处理模板
     */
    private void processTemplate(Notification notification, SendNotificationRequest request) {
        try {
            NotificationTemplate template = templateRepository.findByTemplateCodeAndEnabled(request.getTemplateCode(), true)
                    .orElseThrow(() -> new IllegalArgumentException("模板不存在或已禁用: " + request.getTemplateCode()));

            notification.setTemplateId(template.getId());
            
            // 渲染模板
            String title = templateService.renderTemplate(template.getTitleTemplate(), request.getTemplateParams());
            String content = templateService.renderTemplate(template.getContentTemplate(), request.getTemplateParams());
            
            notification.setTitle(title);
            notification.setContent(content);
            
            // 保存模板参数
            if (request.getTemplateParams() != null) {
                notification.setTemplateParams(request.getTemplateParams().toString());
            }
        } catch (Exception e) {
            log.error("处理模板失败: templateCode={}", request.getTemplateCode(), e);
            throw new RuntimeException("模板处理失败", e);
        }
    }

    /**
     * 发送通知到指定渠道
     */
    private void sendNotificationToChannel(Notification notification) {
        try {
            notification.setStatus(NotificationStatus.SENDING);
            notification.setSendTime(LocalDateTime.now());
            notificationRepository.save(notification);

            // 通过渠道管理器发送
            boolean success = channelManager.sendNotification(notification);
            
            if (success) {
                notification.setStatus(NotificationStatus.SUCCESS);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setFailureReason("渠道发送失败");
            }
        } catch (Exception e) {
            log.error("发送通知失败: notificationId={}", notification.getId(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void retryFailedNotifications() {
        log.info("开始重试失败的通知");

        // 查询需要重试的通知（5分钟前失败的）
        LocalDateTime retryTime = LocalDateTime.now().minusMinutes(5);
        Pageable pageable = PageRequest.of(0, 100);
        List<Notification> failedNotifications = notificationRepository.findRetryableNotifications(
                NotificationStatus.FAILED, retryTime, pageable);

        for (Notification notification : failedNotifications) {
            try {
                notification.setRetryCount(notification.getRetryCount() + 1);
                sendNotificationToChannel(notification);
                log.info("重试通知成功: notificationId={}", notification.getId());
            } catch (Exception e) {
                log.error("重试通知失败: notificationId={}", notification.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public int cleanupReadNotifications(LocalDateTime beforeTime) {
        log.info("清理已读通知，时间早于: {}", beforeTime);
        return notificationRepository.deleteReadNotificationsBefore(NotificationStatus.READ, beforeTime);
    }

    @Override
    public NotificationStatisticsDTO getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取通知统计信息: {} - {}", startTime, endTime);

        NotificationStatisticsDTO statistics = new NotificationStatisticsDTO();

        // 获取状态统计
        List<Object[]> statusStats = notificationRepository.countNotificationsByStatusBetween(startTime, endTime);
        Map<String, Long> statusMap = new HashMap<>();
        long totalCount = 0;

        for (Object[] stat : statusStats) {
            NotificationStatus status = (NotificationStatus) stat[0];
            Long count = (Long) stat[1];
            statusMap.put(status.getCode(), count);
            totalCount += count;

            // 设置各状态数量
            switch (status) {
                case PENDING -> statistics.setPendingCount(count);
                case SENDING -> statistics.setSendingCount(count);
                case SUCCESS -> statistics.setSuccessCount(count);
                case FAILED -> statistics.setFailedCount(count);
                case READ -> statistics.setReadCount(count);
                case CANCELLED -> statistics.setCancelledCount(count);
            }
        }

        statistics.setTotalCount(totalCount);
        statistics.setStatusStatistics(statusMap);

        // 获取渠道统计
        List<Object[]> channelStats = notificationRepository.countNotificationsByChannelAndStatusBetween(startTime, endTime);
        Map<String, Long> channelMap = new HashMap<>();
        Map<String, Long> typeMap = new HashMap<>();

        for (Object[] stat : channelStats) {
            NotificationChannel channel = (NotificationChannel) stat[0];
            Long count = (Long) stat[2];
            channelMap.merge(channel.getCode(), count, Long::sum);
        }

        statistics.setChannelStatistics(channelMap);
        statistics.calculateSuccessRate();

        return statistics;
    }

    @Override
    public NotificationStatisticsDTO getUserStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 简化实现，实际应该有专门的用户统计查询
        NotificationStatisticsDTO statistics = new NotificationStatisticsDTO();

        // 获取用户未读数量
        long unreadCount = getUnreadCount(userId);
        statistics.setSuccessCount(unreadCount);

        return statistics;
    }

    @Override
    @Transactional
    public boolean cancelNotification(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            if (notification.getStatus() == NotificationStatus.PENDING) {
                notification.setStatus(NotificationStatus.CANCELLED);
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public int cancelNotifications(List<Long> notificationIds) {
        int cancelledCount = 0;
        for (Long id : notificationIds) {
            if (cancelNotification(id)) {
                cancelledCount++;
            }
        }
        return cancelledCount;
    }

    /**
     * 转换为DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        BeanUtils.copyProperties(notification, dto);
        return dto;
    }
}

package com.qianshe.notification.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.qianshe.common.result.Result;
import com.qianshe.notification.dto.*;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import com.qianshe.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知控制器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "通知发送和管理相关接口")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "发送通知", description = "发送单个通知")
    public Result<Notification> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        try {
            Notification notification = notificationService.sendNotification(request);
            return Result.success(notification);
        } catch (Exception e) {
            log.error("发送通知失败", e);
            return Result.error("发送通知失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-send")
    @Operation(summary = "批量发送通知", description = "批量发送多个通知")
    public Result<List<Notification>> batchSendNotifications(@Valid @RequestBody BatchSendRequest request) {
        try {
            List<Notification> notifications = notificationService.batchSendNotifications(request);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
            return Result.error("批量发送通知失败: " + e.getMessage());
        }
    }

    @PostMapping("/send-async")
    @Operation(summary = "异步发送通知", description = "异步发送单个通知")
    public Result<String> sendNotificationAsync(@Valid @RequestBody SendNotificationRequest request) {
        try {
            notificationService.sendNotificationAsync(request);
            return Result.success("通知已提交异步发送");
        } catch (Exception e) {
            log.error("异步发送通知失败", e);
            return Result.error("异步发送通知失败: " + e.getMessage());
        }
    }

    @PostMapping("/template/{templateCode}")
    @Operation(summary = "根据模板发送通知", description = "使用指定模板发送通知")
    public Result<Notification> sendByTemplate(
            @PathVariable String templateCode,
            @RequestBody SendNotificationRequest request) {
        try {
            Notification notification = notificationService.sendByTemplate(
                    templateCode, 
                    request.getReceiverIds(), 
                    request.getChannels(),
                    request.getTemplateParams(),
                    request.getBusinessId(),
                    request.getBusinessType()
            );
            return Result.success(notification);
        } catch (Exception e) {
            log.error("根据模板发送通知失败", e);
            return Result.error("根据模板发送通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    @SaCheckLogin
    @Operation(summary = "获取用户通知", description = "分页获取当前用户的通知列表")
    public Result<Page<NotificationDTO>> getUserNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("获取用户通知失败", e);
            return Result.error("获取用户通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/status/{status}")
    @SaCheckLogin
    @Operation(summary = "按状态获取用户通知", description = "分页获取当前用户指定状态的通知")
    public Result<Page<NotificationDTO>> getUserNotificationsByStatus(
            @PathVariable NotificationStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notifications = notificationService.getUserNotificationsByStatus(userId, status, pageable);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("按状态获取用户通知失败", e);
            return Result.error("按状态获取用户通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/type/{type}")
    @SaCheckLogin
    @Operation(summary = "按类型获取用户通知", description = "分页获取当前用户指定类型的通知")
    public Result<Page<NotificationDTO>> getUserNotificationsByType(
            @PathVariable NotificationType type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notifications = notificationService.getUserNotificationsByType(userId, type, pageable);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("按类型获取用户通知失败", e);
            return Result.error("按类型获取用户通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/channel/{channel}")
    @SaCheckLogin
    @Operation(summary = "按渠道获取用户通知", description = "分页获取当前用户指定渠道的通知")
    public Result<Page<NotificationDTO>> getUserNotificationsByChannel(
            @PathVariable NotificationChannel channel,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notifications = notificationService.getUserNotificationsByChannel(userId, channel, pageable);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("按渠道获取用户通知失败", e);
            return Result.error("按渠道获取用户通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/unread-count")
    @SaCheckLogin
    @Operation(summary = "获取未读通知数量", description = "获取当前用户的未读通知数量")
    public Result<Long> getUnreadCount() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            long count = notificationService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取未读通知数量失败", e);
            return Result.error("获取未读通知数量失败: " + e.getMessage());
        }
    }

    @PostMapping("/mark-read")
    @SaCheckLogin
    @Operation(summary = "标记通知为已读", description = "批量标记指定通知为已读")
    public Result<Boolean> markAsRead(@RequestBody List<Long> notificationIds) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean success = notificationService.markAsRead(userId, notificationIds);
            return Result.success(success);
        } catch (Exception e) {
            log.error("标记通知为已读失败", e);
            return Result.error("标记通知为已读失败: " + e.getMessage());
        }
    }

    @PostMapping("/mark-all-read")
    @SaCheckLogin
    @Operation(summary = "标记所有通知为已读", description = "标记当前用户的所有通知为已读")
    public Result<Boolean> markAllAsRead() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean success = notificationService.markAllAsRead(userId);
            return Result.success(success);
        } catch (Exception e) {
            log.error("标记所有通知为已读失败", e);
            return Result.error("标记所有通知为已读失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知详情", description = "根据ID获取通知详情")
    public Result<NotificationDTO> getNotificationById(@PathVariable Long id) {
        try {
            NotificationDTO notification = notificationService.getNotificationById(id);
            if (notification != null) {
                return Result.success(notification);
            } else {
                return Result.error("通知不存在");
            }
        } catch (Exception e) {
            log.error("获取通知详情失败", e);
            return Result.error("获取通知详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/business/{businessId}/{businessType}")
    @Operation(summary = "根据业务获取通知", description = "根据业务ID和类型获取相关通知")
    public Result<List<NotificationDTO>> getNotificationsByBusiness(
            @PathVariable String businessId,
            @PathVariable String businessType) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByBusiness(businessId, businessType);
            return Result.success(notifications);
        } catch (Exception e) {
            log.error("根据业务获取通知失败", e);
            return Result.error("根据业务获取通知失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取通知统计", description = "获取指定时间范围内的通知统计信息")
    public Result<NotificationStatisticsDTO> getStatistics(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            NotificationStatisticsDTO statistics = notificationService.getStatistics(startTime, endTime);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取通知统计失败", e);
            return Result.error("获取通知统计失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "取消通知", description = "取消待发送的通知")
    public Result<Boolean> cancelNotification(@PathVariable Long id) {
        try {
            boolean success = notificationService.cancelNotification(id);
            return Result.success(success);
        } catch (Exception e) {
            log.error("取消通知失败", e);
            return Result.error("取消通知失败: " + e.getMessage());
        }
    }
}

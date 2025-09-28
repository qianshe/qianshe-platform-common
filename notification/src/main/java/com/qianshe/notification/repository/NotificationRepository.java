package com.qianshe.notification.repository;

import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知数据访问接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 根据接收用户ID分页查询通知
     */
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    /**
     * 根据接收用户ID和状态查询通知
     */
    Page<Notification> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, NotificationStatus status, Pageable pageable);

    /**
     * 根据接收用户ID和类型查询通知
     */
    Page<Notification> findByReceiverIdAndTypeOrderByCreatedAtDesc(Long receiverId, NotificationType type, Pageable pageable);

    /**
     * 根据接收用户ID和渠道查询通知
     */
    Page<Notification> findByReceiverIdAndChannelOrderByCreatedAtDesc(Long receiverId, NotificationChannel channel, Pageable pageable);

    /**
     * 查询待发送的通知
     */
    List<Notification> findByStatusAndRetryCountLessThanMaxRetryCountOrderByCreatedAtAsc(NotificationStatus status, Pageable pageable);

    /**
     * 查询需要重试的失败通知
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.retryCount < n.maxRetryCount AND n.updatedAt < :beforeTime ORDER BY n.updatedAt ASC")
    List<Notification> findRetryableNotifications(@Param("status") NotificationStatus status, @Param("beforeTime") LocalDateTime beforeTime, Pageable pageable);

    /**
     * 统计用户未读通知数量
     */
    long countByReceiverIdAndStatus(Long receiverId, NotificationStatus status);

    /**
     * 统计用户指定类型的未读通知数量
     */
    long countByReceiverIdAndTypeAndStatus(Long receiverId, NotificationType type, NotificationStatus status);

    /**
     * 批量标记通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.readTime = :readTime WHERE n.receiverId = :receiverId AND n.id IN :ids")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("ids") List<Long> ids, @Param("status") NotificationStatus status, @Param("readTime") LocalDateTime readTime);

    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.readTime = :readTime WHERE n.receiverId = :receiverId AND n.status = :currentStatus")
    int markAllAsRead(@Param("receiverId") Long receiverId, @Param("status") NotificationStatus status, @Param("currentStatus") NotificationStatus currentStatus, @Param("readTime") LocalDateTime readTime);

    /**
     * 根据业务ID和类型查询通知
     */
    List<Notification> findByBusinessIdAndBusinessType(String businessId, String businessType);

    /**
     * 删除指定时间之前的已读通知
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.status = :status AND n.readTime < :beforeTime")
    int deleteReadNotificationsBefore(@Param("status") NotificationStatus status, @Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计指定时间范围内的通知发送情况
     */
    @Query("SELECT n.status, COUNT(n) FROM Notification n WHERE n.createdAt BETWEEN :startTime AND :endTime GROUP BY n.status")
    List<Object[]> countNotificationsByStatusBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内各渠道的通知发送情况
     */
    @Query("SELECT n.channel, n.status, COUNT(n) FROM Notification n WHERE n.createdAt BETWEEN :startTime AND :endTime GROUP BY n.channel, n.status")
    List<Object[]> countNotificationsByChannelAndStatusBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}

package com.qianshe.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationStatus;
import com.qianshe.notification.enums.NotificationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知Mapper接口
 *
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 根据接收用户ID分页查询通知
     */
    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} ORDER BY created_at DESC")
    IPage<Notification> selectPageByReceiverId(Page<Notification> page, @Param("receiverId") Long receiverId);

    /**
     * 根据接收用户ID和状态查询通知
     */
    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} AND status = #{status} ORDER BY created_at DESC")
    IPage<Notification> selectPageByReceiverIdAndStatus(Page<Notification> page, @Param("receiverId") Long receiverId, @Param("status") String status);

    /**
     * 根据接收用户ID和类型查询通知
     */
    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} AND type = #{type} ORDER BY created_at DESC")
    IPage<Notification> selectPageByReceiverIdAndType(Page<Notification> page, @Param("receiverId") Long receiverId, @Param("type") String type);

    /**
     * 根据接收用户ID和渠道查询通知
     */
    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} AND channel = #{channel} ORDER BY created_at DESC")
    IPage<Notification> selectPageByReceiverIdAndChannel(Page<Notification> page, @Param("receiverId") Long receiverId, @Param("channel") String channel);

    /**
     * 查询待发送的通知（重试次数未超过最大限制）
     */
    @Select("SELECT * FROM notification WHERE status = #{status} AND retry_count < max_retry_count ORDER BY created_at ASC LIMIT #{page.size}")
    List<Notification> selectPendingNotifications(@Param("status") String status, @Param("page") Page<Notification> page);

    /**
     * 查询需要重试的失败通知
     */
    @Select("SELECT * FROM notification WHERE status = #{status} AND retry_count < max_retry_count AND updated_at < #{beforeTime} ORDER BY updated_at ASC LIMIT #{page.size}")
    List<Notification> selectRetryableNotifications(@Param("status") String status, @Param("beforeTime") LocalDateTime beforeTime, @Param("page") Page<Notification> page);

    /**
     * 统计用户未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id = #{receiverId} AND status = #{status}")
    long countByReceiverIdAndStatus(@Param("receiverId") Long receiverId, @Param("status") String status);

    /**
     * 统计用户指定类型的未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id = #{receiverId} AND type = #{type} AND status = #{status}")
    long countByReceiverIdAndTypeAndStatus(@Param("receiverId") Long receiverId, @Param("type") String type, @Param("status") String status);

    /**
     * 批量标记通知为已读
     */
    @Update("<script>" +
            "UPDATE notification SET status = #{status}, read_time = #{readTime} " +
            "WHERE receiver_id = #{receiverId} AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int updateMarkAsRead(@Param("receiverId") Long receiverId, @Param("ids") List<Long> ids, @Param("status") String status, @Param("readTime") LocalDateTime readTime);

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE notification SET status = #{status}, read_time = #{readTime} WHERE receiver_id = #{receiverId} AND status = #{currentStatus}")
    int updateMarkAllAsRead(@Param("receiverId") Long receiverId, @Param("status") String status, @Param("currentStatus") String currentStatus, @Param("readTime") LocalDateTime readTime);

    /**
     * 根据业务ID和类型查询通知
     */
    @Select("SELECT * FROM notification WHERE business_id = #{businessId} AND business_type = #{businessType}")
    List<Notification> selectByBusinessIdAndType(@Param("businessId") String businessId, @Param("businessType") String businessType);

    /**
     * 删除指定时间之前的已读通知
     */
    @Update("DELETE FROM notification WHERE status = #{status} AND read_time < #{beforeTime}")
    int deleteReadNotificationsBefore(@Param("status") String status, @Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计指定时间范围内的通知发送情况
     */
    @Select("SELECT status, COUNT(*) as count FROM notification WHERE created_at BETWEEN #{startTime} AND #{endTime} GROUP BY status")
    List<Object[]> countNotificationsByStatusBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内各渠道的通知发送情况
     */
    @Select("SELECT channel, status, COUNT(*) as count FROM notification WHERE created_at BETWEEN #{startTime} AND #{endTime} GROUP BY channel, status")
    List<Object[]> countNotificationsByChannelAndStatusBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
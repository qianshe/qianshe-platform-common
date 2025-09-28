package com.qianshe.notification.repository;

import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板数据访问接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    /**
     * 根据模板编码查询模板
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);

    /**
     * 根据模板编码和启用状态查询模板
     */
    Optional<NotificationTemplate> findByTemplateCodeAndEnabled(String templateCode, Boolean enabled);

    /**
     * 根据通知类型查询启用的模板
     */
    List<NotificationTemplate> findByTypeAndEnabledOrderByCreatedAtDesc(NotificationType type, Boolean enabled);

    /**
     * 根据通知类型分页查询模板
     */
    Page<NotificationTemplate> findByTypeOrderByCreatedAtDesc(NotificationType type, Pageable pageable);

    /**
     * 根据启用状态分页查询模板
     */
    Page<NotificationTemplate> findByEnabledOrderByCreatedAtDesc(Boolean enabled, Pageable pageable);

    /**
     * 根据创建者查询模板
     */
    Page<NotificationTemplate> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    /**
     * 模糊查询模板名称
     */
    Page<NotificationTemplate> findByTemplateNameContainingOrderByCreatedAtDesc(String templateName, Pageable pageable);

    /**
     * 检查模板编码是否存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 检查模板编码是否存在（排除指定ID）
     */
    boolean existsByTemplateCodeAndIdNot(String templateCode, Long id);

    /**
     * 查询支持指定渠道的模板
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.enabled = true AND t.supportedChannels LIKE %:channel%")
    List<NotificationTemplate> findBySupportedChannelsContaining(@Param("channel") String channel);

    /**
     * 根据类型和支持的渠道查询模板
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.type = :type AND t.enabled = true AND t.supportedChannels LIKE %:channel%")
    List<NotificationTemplate> findByTypeAndSupportedChannelsContaining(@Param("type") NotificationType type, @Param("channel") String channel);

    /**
     * 统计各类型的模板数量
     */
    @Query("SELECT t.type, COUNT(t) FROM NotificationTemplate t WHERE t.enabled = true GROUP BY t.type")
    List<Object[]> countTemplatesByType();

    /**
     * 统计启用和禁用的模板数量
     */
    @Query("SELECT t.enabled, COUNT(t) FROM NotificationTemplate t GROUP BY t.enabled")
    List<Object[]> countTemplatesByEnabled();
}

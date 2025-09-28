package com.qianshe.notification.service;

import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 通知模板服务接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface NotificationTemplateService {

    /**
     * 创建通知模板
     */
    NotificationTemplate createTemplate(NotificationTemplate template);

    /**
     * 更新通知模板
     */
    NotificationTemplate updateTemplate(Long id, NotificationTemplate template);

    /**
     * 删除通知模板
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据ID获取模板
     */
    NotificationTemplate getTemplateById(Long id);

    /**
     * 根据编码获取模板
     */
    NotificationTemplate getTemplateByCode(String templateCode);

    /**
     * 分页查询模板
     */
    Page<NotificationTemplate> getTemplates(Pageable pageable);

    /**
     * 根据类型分页查询模板
     */
    Page<NotificationTemplate> getTemplatesByType(NotificationType type, Pageable pageable);

    /**
     * 根据启用状态分页查询模板
     */
    Page<NotificationTemplate> getTemplatesByEnabled(Boolean enabled, Pageable pageable);

    /**
     * 根据创建者分页查询模板
     */
    Page<NotificationTemplate> getTemplatesByCreator(Long creatorId, Pageable pageable);

    /**
     * 模糊查询模板名称
     */
    Page<NotificationTemplate> searchTemplatesByName(String templateName, Pageable pageable);

    /**
     * 获取指定类型的启用模板
     */
    List<NotificationTemplate> getEnabledTemplatesByType(NotificationType type);

    /**
     * 启用/禁用模板
     */
    boolean toggleTemplateStatus(Long id, Boolean enabled);

    /**
     * 渲染模板
     */
    String renderTemplate(String template, Map<String, Object> params);

    /**
     * 验证模板语法
     */
    boolean validateTemplate(String template);

    /**
     * 检查模板编码是否存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 检查模板编码是否存在（排除指定ID）
     */
    boolean existsByTemplateCodeExcludeId(String templateCode, Long excludeId);

    /**
     * 获取模板统计信息
     */
    Map<String, Long> getTemplateStatistics();
}

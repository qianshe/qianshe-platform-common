package com.qianshe.notification.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationType;
import com.qianshe.notification.mapper.NotificationTemplateMapper;
import com.qianshe.notification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通知模板服务实现类
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateMapper templateMapper;

    // 模板变量匹配模式：${variableName}
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    @Override
    @Transactional
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        log.info("创建通知模板: {}", template.getTemplateCode());

        // 检查模板编码是否已存在
        if (templateMapper.existsByTemplateCode(template.getTemplateCode())) {
            throw new IllegalArgumentException("模板编码已存在: " + template.getTemplateCode());
        }

        // 验证模板语法
        if (!validateTemplate(template.getTitleTemplate())) {
            throw new IllegalArgumentException("标题模板语法错误");
        }
        if (!validateTemplate(template.getContentTemplate())) {
            throw new IllegalArgumentException("内容模板语法错误");
        }

        templateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional
    public NotificationTemplate updateTemplate(Long id, NotificationTemplate template) {
        log.info("更新通知模板: id={}", id);

        NotificationTemplate existingTemplate = templateMapper.selectById(id);
        if (existingTemplate == null) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }

        // 检查模板编码是否与其他模板冲突
        if (!existingTemplate.getTemplateCode().equals(template.getTemplateCode()) &&
            templateMapper.existsByTemplateCodeAndIdNot(template.getTemplateCode(), id)) {
            throw new IllegalArgumentException("模板编码已存在: " + template.getTemplateCode());
        }

        // 验证模板语法
        if (!validateTemplate(template.getTitleTemplate())) {
            throw new IllegalArgumentException("标题模板语法错误");
        }
        if (!validateTemplate(template.getContentTemplate())) {
            throw new IllegalArgumentException("内容模板语法错误");
        }

        // 更新字段
        existingTemplate.setTemplateCode(template.getTemplateCode());
        existingTemplate.setTemplateName(template.getTemplateName());
        existingTemplate.setType(template.getType());
        existingTemplate.setSupportedChannels(template.getSupportedChannels());
        existingTemplate.setTitleTemplate(template.getTitleTemplate());
        existingTemplate.setContentTemplate(template.getContentTemplate());
        existingTemplate.setParamDescription(template.getParamDescription());
        existingTemplate.setEnabled(template.getEnabled());
        existingTemplate.setRemark(template.getRemark());

        templateMapper.updateById(existingTemplate);
        return existingTemplate;
    }

    @Override
    @Transactional
    public boolean deleteTemplate(Long id) {
        log.info("删除通知模板: id={}", id);

        if (templateMapper.selectById(id) != null) {
            templateMapper.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public NotificationTemplate getTemplateByCode(String templateCode) {
        return templateMapper.selectByTemplateCode(templateCode);
    }

    @Override
    public org.springframework.data.domain.Page<NotificationTemplate> getTemplates(Pageable pageable) {
        Page<NotificationTemplate> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<NotificationTemplate> result = templateMapper.selectPage(page, null);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<NotificationTemplate> getTemplatesByType(NotificationType type, Pageable pageable) {
        Page<NotificationTemplate> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<NotificationTemplate> result = templateMapper.selectPageByType(page, type.name());
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<NotificationTemplate> getTemplatesByEnabled(Boolean enabled, Pageable pageable) {
        Page<NotificationTemplate> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<NotificationTemplate> result = templateMapper.selectPageByEnabled(page, enabled);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<NotificationTemplate> getTemplatesByCreator(Long creatorId, Pageable pageable) {
        Page<NotificationTemplate> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<NotificationTemplate> result = templateMapper.selectPageByCreatorId(page, creatorId);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<NotificationTemplate> searchTemplatesByName(String templateName, Pageable pageable) {
        Page<NotificationTemplate> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<NotificationTemplate> result = templateMapper.selectPageByTemplateNameLike(page, templateName);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<NotificationTemplate> getEnabledTemplatesByType(NotificationType type) {
        return templateMapper.selectByTypeAndEnabled(type.name(), true);
    }

    @Override
    @Transactional
    public boolean toggleTemplateStatus(Long id, Boolean enabled) {
        NotificationTemplate template = templateMapper.selectById(id);
        if (template != null) {
            template.setEnabled(enabled);
            templateMapper.updateById(template);
            return true;
        }
        return false;
    }

    @Override
    public String renderTemplate(String template, Map<String, Object> params) {
        if (!StringUtils.hasText(template)) {
            return template;
        }

        if (params == null || params.isEmpty()) {
            return template;
        }

        String result = template;
        Matcher matcher = TEMPLATE_PATTERN.matcher(template);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = params.get(variableName);
            if (value != null) {
                result = result.replace("${" + variableName + "}", value.toString());
            }
        }

        return result;
    }

    @Override
    public boolean validateTemplate(String template) {
        if (!StringUtils.hasText(template)) {
            return false;
        }

        try {
            // 简单的语法验证：检查模板变量格式是否正确
            Matcher matcher = TEMPLATE_PATTERN.matcher(template);
            while (matcher.find()) {
                String variableName = matcher.group(1);
                if (!StringUtils.hasText(variableName)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("模板语法验证失败: {}", template, e);
            return false;
        }
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        return templateMapper.existsByTemplateCode(templateCode);
    }

    @Override
    public boolean existsByTemplateCodeExcludeId(String templateCode, Long excludeId) {
        return templateMapper.existsByTemplateCodeAndIdNot(templateCode, excludeId);
    }

    @Override
    public Map<String, Long> getTemplateStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        // 获取各类型统计
        List<Object[]> typeStats = templateMapper.countTemplatesByType();
        for (Object[] stat : typeStats) {
            NotificationType type = (NotificationType) stat[0];
            Long count = (Long) stat[1];
            statistics.put("type_" + type.getCode(), count);
        }

        // 获取启用状态统计
        List<Object[]> enabledStats = templateMapper.countTemplatesByEnabled();
        for (Object[] stat : enabledStats) {
            Boolean enabled = (Boolean) stat[0];
            Long count = (Long) stat[1];
            statistics.put(enabled ? "enabled" : "disabled", count);
        }

        // 总数
        Long totalCount = templateMapper.selectCount(null);
        statistics.put("total", totalCount);

        return statistics;
    }
}
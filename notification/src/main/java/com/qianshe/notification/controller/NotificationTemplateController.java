package com.qianshe.notification.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.qianshe.common.result.Result;
import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationType;
import com.qianshe.notification.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知模板控制器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notification-templates")
@RequiredArgsConstructor
@Tag(name = "通知模板管理", description = "通知模板相关接口")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    @PostMapping
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "创建通知模板", description = "创建新的通知模板")
    public Result<NotificationTemplate> createTemplate(@Valid @RequestBody NotificationTemplate template) {
        try {
            template.setCreatorId(StpUtil.getLoginIdAsLong());
            NotificationTemplate createdTemplate = templateService.createTemplate(template);
            return Result.success(createdTemplate);
        } catch (Exception e) {
            log.error("创建通知模板失败", e);
            return Result.error("创建通知模板失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "更新通知模板", description = "更新指定的通知模板")
    public Result<NotificationTemplate> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody NotificationTemplate template) {
        try {
            NotificationTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return Result.success(updatedTemplate);
        } catch (Exception e) {
            log.error("更新通知模板失败", e);
            return Result.error("更新通知模板失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "删除通知模板", description = "删除指定的通知模板")
    public Result<Boolean> deleteTemplate(@PathVariable Long id) {
        try {
            boolean success = templateService.deleteTemplate(id);
            return Result.success(success);
        } catch (Exception e) {
            log.error("删除通知模板失败", e);
            return Result.error("删除通知模板失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知模板详情", description = "根据ID获取通知模板详情")
    public Result<NotificationTemplate> getTemplateById(@PathVariable Long id) {
        try {
            NotificationTemplate template = templateService.getTemplateById(id);
            if (template != null) {
                return Result.success(template);
            } else {
                return Result.error("模板不存在");
            }
        } catch (Exception e) {
            log.error("获取通知模板详情失败", e);
            return Result.error("获取通知模板详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据编码获取模板", description = "根据模板编码获取模板详情")
    public Result<NotificationTemplate> getTemplateByCode(@PathVariable String templateCode) {
        try {
            NotificationTemplate template = templateService.getTemplateByCode(templateCode);
            if (template != null) {
                return Result.success(template);
            } else {
                return Result.error("模板不存在");
            }
        } catch (Exception e) {
            log.error("根据编码获取模板失败", e);
            return Result.error("根据编码获取模板失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "分页获取通知模板", description = "分页获取通知模板列表")
    public Result<Page<NotificationTemplate>> getTemplates(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationTemplate> templates = templateService.getTemplates(pageable);
            return Result.success(templates);
        } catch (Exception e) {
            log.error("分页获取通知模板失败", e);
            return Result.error("分页获取通知模板失败: " + e.getMessage());
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取模板", description = "根据通知类型分页获取模板")
    public Result<Page<NotificationTemplate>> getTemplatesByType(
            @PathVariable NotificationType type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationTemplate> templates = templateService.getTemplatesByType(type, pageable);
            return Result.success(templates);
        } catch (Exception e) {
            log.error("按类型获取模板失败", e);
            return Result.error("按类型获取模板失败: " + e.getMessage());
        }
    }

    @GetMapping("/enabled/{enabled}")
    @Operation(summary = "按启用状态获取模板", description = "根据启用状态分页获取模板")
    public Result<Page<NotificationTemplate>> getTemplatesByEnabled(
            @PathVariable Boolean enabled,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationTemplate> templates = templateService.getTemplatesByEnabled(enabled, pageable);
            return Result.success(templates);
        } catch (Exception e) {
            log.error("按启用状态获取模板失败", e);
            return Result.error("按启用状态获取模板失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索模板", description = "根据模板名称模糊搜索")
    public Result<Page<NotificationTemplate>> searchTemplatesByName(
            @Parameter(description = "模板名称") @RequestParam String templateName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationTemplate> templates = templateService.searchTemplatesByName(templateName, pageable);
            return Result.success(templates);
        } catch (Exception e) {
            log.error("搜索模板失败", e);
            return Result.error("搜索模板失败: " + e.getMessage());
        }
    }

    @GetMapping("/enabled-by-type/{type}")
    @Operation(summary = "获取启用的模板", description = "获取指定类型的所有启用模板")
    public Result<List<NotificationTemplate>> getEnabledTemplatesByType(@PathVariable NotificationType type) {
        try {
            List<NotificationTemplate> templates = templateService.getEnabledTemplatesByType(type);
            return Result.success(templates);
        } catch (Exception e) {
            log.error("获取启用的模板失败", e);
            return Result.error("获取启用的模板失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "切换模板状态", description = "启用或禁用指定模板")
    public Result<Boolean> toggleTemplateStatus(
            @PathVariable Long id,
            @Parameter(description = "启用状态") @RequestParam Boolean enabled) {
        try {
            boolean success = templateService.toggleTemplateStatus(id, enabled);
            return Result.success(success);
        } catch (Exception e) {
            log.error("切换模板状态失败", e);
            return Result.error("切换模板状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "验证模板语法", description = "验证模板语法是否正确")
    public Result<Boolean> validateTemplate(@RequestBody String template) {
        try {
            boolean valid = templateService.validateTemplate(template);
            return Result.success(valid);
        } catch (Exception e) {
            log.error("验证模板语法失败", e);
            return Result.error("验证模板语法失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取模板统计", description = "获取模板统计信息")
    public Result<Map<String, Long>> getTemplateStatistics() {
        try {
            Map<String, Long> statistics = templateService.getTemplateStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取模板统计失败", e);
            return Result.error("获取模板统计失败: " + e.getMessage());
        }
    }
}

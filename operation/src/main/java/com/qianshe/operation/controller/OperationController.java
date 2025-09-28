package com.qianshe.operation.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.qianshe.common.dto.Result;
import com.qianshe.operation.entity.AuditTask;
import com.qianshe.operation.enums.AuditStatus;
import com.qianshe.operation.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 运营管理控制器
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/operation")
@RequiredArgsConstructor
@Tag(name = "运营管理", description = "运营管理相关接口")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/audit-tasks")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "创建审核任务", description = "创建新的审核任务")
    public Result<AuditTask> createAuditTask(@RequestBody AuditTask auditTask) {
        try {
            AuditTask created = operationService.createAuditTask(auditTask);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建审核任务失败", e);
            return Result.error("创建审核任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/audit-tasks")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "分页查询审核任务", description = "分页查询审核任务列表")
    public Result<Page<AuditTask>> getAuditTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditTask> tasks = operationService.getAuditTasks(pageable);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("查询审核任务失败", e);
            return Result.error("查询审核任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/audit-tasks/status/{status}")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "按状态查询审核任务", description = "根据状态分页查询审核任务")
    public Result<Page<AuditTask>> getAuditTasksByStatus(
            @PathVariable AuditStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditTask> tasks = operationService.getAuditTasksByStatus(status, pageable);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("按状态查询审核任务失败", e);
            return Result.error("按状态查询审核任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/audit-tasks/{taskId}/audit")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "审核任务", description = "对指定任务进行审核")
    public Result<Boolean> auditTask(
            @PathVariable Long taskId,
            @RequestParam Long auditorId,
            @RequestParam AuditStatus status,
            @RequestParam(required = false) String comment) {
        try {
            boolean success = operationService.auditTask(taskId, auditorId, status, comment);
            return Result.success(success);
        } catch (Exception e) {
            log.error("审核任务失败", e);
            return Result.error("审核任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/platform")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "获取平台统计", description = "获取平台统计数据")
    public Result<Map<String, Object>> getPlatformStatistics(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = operationService.getPlatformStatistics(startTime, endTime);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取平台统计失败", e);
            return Result.error("获取平台统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/audit")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "获取审核统计", description = "获取审核统计数据")
    public Result<Map<String, Object>> getAuditStatistics(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = operationService.getAuditStatistics(startTime, endTime);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取审核统计失败", e);
            return Result.error("获取审核统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/config")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "获取系统配置", description = "获取系统配置信息")
    public Result<Map<String, Object>> getSystemConfig() {
        try {
            Map<String, Object> config = operationService.getSystemConfig();
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取系统配置失败", e);
            return Result.error("获取系统配置失败: " + e.getMessage());
        }
    }

    @PutMapping("/config")
    @SaCheckLogin
    @SaCheckRole("admin")
    @Operation(summary = "更新系统配置", description = "更新系统配置信息")
    public Result<Boolean> updateSystemConfig(@RequestBody Map<String, Object> config) {
        try {
            boolean success = operationService.updateSystemConfig(config);
            return Result.success(success);
        } catch (Exception e) {
            log.error("更新系统配置失败", e);
            return Result.error("更新系统配置失败: " + e.getMessage());
        }
    }
}

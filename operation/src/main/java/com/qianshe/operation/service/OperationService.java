package com.qianshe.operation.service;

import com.qianshe.operation.entity.AuditTask;
import com.qianshe.operation.enums.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 运营管理服务接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
public interface OperationService {

    /**
     * 创建审核任务
     */
    AuditTask createAuditTask(AuditTask auditTask);

    /**
     * 分页查询审核任务
     */
    Page<AuditTask> getAuditTasks(Pageable pageable);

    /**
     * 根据状态查询审核任务
     */
    Page<AuditTask> getAuditTasksByStatus(AuditStatus status, Pageable pageable);

    /**
     * 审核任务
     */
    boolean auditTask(Long taskId, Long auditorId, AuditStatus status, String comment);

    /**
     * 获取平台统计数据
     */
    Map<String, Object> getPlatformStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取审核统计数据
     */
    Map<String, Object> getAuditStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取系统配置
     */
    Map<String, Object> getSystemConfig();

    /**
     * 更新系统配置
     */
    boolean updateSystemConfig(Map<String, Object> config);
}

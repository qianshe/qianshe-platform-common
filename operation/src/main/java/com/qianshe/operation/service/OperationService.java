package com.qianshe.operation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qianshe.operation.entity.AuditTask;
import com.qianshe.operation.enums.AuditStatus;

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
     *
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<AuditTask> getAuditTasks(Integer page, Integer size);

    /**
     * 根据状态查询审核任务
     *
     * @param status 审核状态
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<AuditTask> getAuditTasksByStatus(AuditStatus status, Integer page, Integer size);

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

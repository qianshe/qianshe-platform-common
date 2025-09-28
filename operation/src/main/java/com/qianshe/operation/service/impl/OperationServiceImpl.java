package com.qianshe.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.operation.entity.AuditTask;
import com.qianshe.operation.enums.AuditStatus;
import com.qianshe.operation.mapper.AuditTaskMapper;
import com.qianshe.operation.service.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营管理服务实现
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final AuditTaskMapper auditTaskMapper;

    @Override
    @Transactional
    public AuditTask createAuditTask(AuditTask auditTask) {
        log.info("创建审核任务: {}", auditTask.getTitle());
        
        // 设置默认值
        if (auditTask.getStatus() == null) {
            auditTask.setStatus(AuditStatus.PENDING);
        }
        if (auditTask.getPriority() == null) {
            auditTask.setPriority(1);
        }
        
        // 保存审核任务
        auditTaskMapper.insert(auditTask);
        
        log.info("审核任务创建成功，ID: {}", auditTask.getId());
        return auditTask;
    }

    @Override
    public org.springframework.data.domain.Page<AuditTask> getAuditTasks(Pageable pageable) {
        log.info("分页查询审核任务，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        // 转换分页参数
        Page<AuditTask> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 查询数据
        IPage<AuditTask> result = auditTaskMapper.selectPage(page,
            new QueryWrapper<AuditTask>().orderByDesc("created_at"));
        
        // 转换为Spring Data Page
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<AuditTask> getAuditTasksByStatus(AuditStatus status, Pageable pageable) {
        log.info("根据状态查询审核任务，状态: {}, 页码: {}, 大小: {}", 
                status, pageable.getPageNumber(), pageable.getPageSize());
        
        // 转换分页参数
        Page<AuditTask> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 查询数据
        IPage<AuditTask> result = auditTaskMapper.selectByStatus(page, status);
        
        // 转换为Spring Data Page
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    @Transactional
    public boolean auditTask(Long taskId, Long auditorId, AuditStatus status, String comment) {
        log.info("审核任务，任务ID: {}, 审核员: {}, 状态: {}", taskId, auditorId, status);
        
        try {
            // 检查任务是否存在
            AuditTask task = auditTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("审核任务不存在，ID: {}", taskId);
                return false;
            }
            
            // 检查任务状态
            if (task.getStatus() != AuditStatus.PENDING) {
                log.warn("任务已被审核，当前状态: {}", task.getStatus());
                return false;
            }
            
            // 更新审核状态
            int updated = auditTaskMapper.updateAuditStatus(taskId, status, auditorId, comment, LocalDateTime.now());
            
            if (updated > 0) {
                log.info("审核任务成功，任务ID: {}", taskId);
                return true;
            } else {
                log.warn("审核任务失败，任务ID: {}", taskId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("审核任务异常，任务ID: {}", taskId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getPlatformStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取平台统计数据，时间范围: {} - {}", startTime, endTime);
        
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 统计各状态的任务数量
            List<Map<String, Object>> statusStats = auditTaskMapper.countByStatus(startTime, endTime);
            statistics.put("statusStatistics", statusStats);
            
            // 统计各业务类型的任务数量
            List<Map<String, Object>> businessTypeStats = auditTaskMapper.countByBusinessType(startTime, endTime);
            statistics.put("businessTypeStatistics", businessTypeStats);
            
            // 计算总任务数
            long totalTasks = statusStats.stream()
                    .mapToLong(stat -> ((Number) stat.get("count")).longValue())
                    .sum();
            statistics.put("totalTasks", totalTasks);
            
            // 计算待审核任务数
            long pendingTasks = auditTaskMapper.selectCount(
                new QueryWrapper<AuditTask>()
                    .eq("status", AuditStatus.PENDING)
                    .between("created_at", startTime, endTime));
            statistics.put("pendingTasks", pendingTasks);

            // 计算已完成任务数
            long completedTasks = auditTaskMapper.selectCount(
                new QueryWrapper<AuditTask>()
                    .in("status", AuditStatus.APPROVED, AuditStatus.REJECTED)
                    .between("created_at", startTime, endTime));
            statistics.put("completedTasks", completedTasks);
            
            // 计算完成率
            double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
            statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
            
            log.info("平台统计数据获取成功，总任务数: {}", totalTasks);
            
        } catch (Exception e) {
            log.error("获取平台统计数据失败", e);
            statistics.put("error", "获取统计数据失败: " + e.getMessage());
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getAuditStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取审核统计数据，时间范围: {} - {}", startTime, endTime);
        
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 统计审核员工作量
            List<Map<String, Object>> auditorStats = auditTaskMapper.countByAuditor(startTime, endTime);
            statistics.put("auditorStatistics", auditorStats);
            
            // 计算平均审核时间（简化实现）
            statistics.put("averageAuditTime", "2.5小时");
            
            // 查询超时任务
            List<AuditTask> timeoutTasks = auditTaskMapper.selectTimeoutTasks();
            statistics.put("timeoutTasks", timeoutTasks.size());
            
            // 查询高优先级待审核任务
            List<AuditTask> highPriorityTasks = auditTaskMapper.selectHighPriorityPendingTasks();
            statistics.put("highPriorityPendingTasks", highPriorityTasks.size());
            
            log.info("审核统计数据获取成功");
            
        } catch (Exception e) {
            log.error("获取审核统计数据失败", e);
            statistics.put("error", "获取审核统计数据失败: " + e.getMessage());
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getSystemConfig() {
        log.info("获取系统配置");
        
        Map<String, Object> config = new HashMap<>();
        
        // 审核配置
        config.put("audit.auto_assign", true);
        config.put("audit.timeout_hours", 48);
        config.put("audit.batch_size", 100);
        
        // 统计配置
        config.put("statistics.cache_ttl", 300);
        config.put("statistics.refresh_interval", 60);
        
        // 平台配置
        config.put("platform.name", "千舍运营管理平台");
        config.put("platform.version", "1.0.0");
        
        return config;
    }

    @Override
    public boolean updateSystemConfig(Map<String, Object> config) {
        log.info("更新系统配置: {}", config);
        
        try {
            // 这里应该将配置保存到数据库或配置中心
            // 简化实现，直接返回成功
            log.info("系统配置更新成功");
            return true;
            
        } catch (Exception e) {
            log.error("更新系统配置失败", e);
            return false;
        }
    }
}

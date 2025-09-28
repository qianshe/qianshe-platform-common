package com.qianshe.operation.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.operation.entity.AuditTask;
import com.qianshe.operation.enums.AuditStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审核任务Mapper接口
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface AuditTaskMapper extends BaseMapper<AuditTask> {

    /**
     * 根据状态分页查询审核任务
     */
    default IPage<AuditTask> selectByStatus(Page<AuditTask> page, AuditStatus status) {
        return selectPage(page,
            new QueryWrapper<AuditTask>().eq("status", status)
                                        .orderByDesc("created_at"));
    }

    /**
     * 根据提交用户ID分页查询审核任务
     */
    default IPage<AuditTask> selectBySubmitterId(Page<AuditTask> page, Long submitterId) {
        return selectPage(page,
            new QueryWrapper<AuditTask>().eq("submitter_id", submitterId)
                                        .orderByDesc("created_at"));
    }

    /**
     * 根据审核员ID分页查询审核任务
     */
    default IPage<AuditTask> selectByAuditorId(Page<AuditTask> page, Long auditorId) {
        return selectPage(page,
            new QueryWrapper<AuditTask>().eq("auditor_id", auditorId)
                                        .orderByDesc("created_at"));
    }

    /**
     * 根据业务类型和状态查询审核任务
     */
    default List<AuditTask> selectByBusinessTypeAndStatus(String businessType, AuditStatus status) {
        return selectList(
            new QueryWrapper<AuditTask>().eq("business_type", businessType)
                                        .eq("status", status)
                                        .orderByDesc("created_at"));
    }

    /**
     * 更新审核任务状态
     */
    @Update("UPDATE audit_task SET status = #{status}, auditor_id = #{auditorId}, " +
            "audit_comment = #{auditComment}, audit_time = #{auditTime}, " +
            "updated_at = NOW() WHERE id = #{taskId}")
    int updateAuditStatus(@Param("taskId") Long taskId, 
                         @Param("status") AuditStatus status,
                         @Param("auditorId") Long auditorId,
                         @Param("auditComment") String auditComment,
                         @Param("auditTime") LocalDateTime auditTime);

    /**
     * 统计各状态的审核任务数量
     */
    @Select("SELECT status, COUNT(*) as count FROM audit_task " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY status")
    List<Map<String, Object>> countByStatus(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各业务类型的审核任务数量
     */
    @Select("SELECT business_type, COUNT(*) as count FROM audit_task " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY business_type")
    List<Map<String, Object>> countByBusinessType(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 统计审核员的工作量
     */
    @Select("SELECT auditor_id, COUNT(*) as count FROM audit_task " +
            "WHERE status != 'PENDING' AND audit_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY auditor_id")
    List<Map<String, Object>> countByAuditor(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询超时的审核任务
     */
    default List<AuditTask> selectTimeoutTasks() {
        return selectList(
            new QueryWrapper<AuditTask>().eq("status", AuditStatus.PENDING)
                                        .lt("deadline", LocalDateTime.now())
                                        .orderByAsc("created_at"));
    }

    /**
     * 查询高优先级待审核任务
     */
    default List<AuditTask> selectHighPriorityPendingTasks() {
        return selectList(
            new QueryWrapper<AuditTask>().eq("status", AuditStatus.PENDING)
                                        .ge("priority", 3)
                                        .orderByDesc("priority")
                                        .orderByAsc("created_at"));
    }

    /**
     * 根据关键词搜索审核任务
     */
    default IPage<AuditTask> searchTasks(Page<AuditTask> page, String keyword) {
        return selectPage(page,
            new QueryWrapper<AuditTask>().and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("content", keyword)
                .or()
                .like("business_id", keyword))
            .orderByDesc("created_at"));
    }
}

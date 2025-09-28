package com.qianshe.operation.entity;

import com.qianshe.operation.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 审核任务实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "audit_task")
@EntityListeners(AuditingEntityListener.class)
public class AuditTask {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 业务ID
     */
    @Column(name = "business_id", nullable = false, length = 100)
    private String businessId;

    /**
     * 业务类型
     */
    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    /**
     * 审核标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 审核内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditStatus status = AuditStatus.PENDING;

    /**
     * 提交用户ID
     */
    @Column(name = "submitter_id", nullable = false)
    private Long submitterId;

    /**
     * 审核员ID
     */
    @Column(name = "auditor_id")
    private Long auditorId;

    /**
     * 审核意见
     */
    @Column(name = "audit_comment", length = 500)
    private String auditComment;

    /**
     * 优先级
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 1;

    /**
     * 截止时间
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 扩展数据
     */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

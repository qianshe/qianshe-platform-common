package com.qianshe.notification.entity;

import com.qianshe.notification.enums.NotificationChannel;
import com.qianshe.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 通知模板实体
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification_template")
@EntityListeners(AuditingEntityListener.class)
public class NotificationTemplate {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 模板编码（唯一标识）
     */
    @Column(name = "template_code", nullable = false, unique = true, length = 100)
    private String templateCode;

    /**
     * 模板名称
     */
    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    /**
     * 通知类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    /**
     * 支持的通知渠道（逗号分隔）
     */
    @Column(name = "supported_channels", nullable = false, length = 200)
    private String supportedChannels;

    /**
     * 标题模板
     */
    @Column(name = "title_template", nullable = false, length = 500)
    private String titleTemplate;

    /**
     * 内容模板
     */
    @Column(name = "content_template", nullable = false, columnDefinition = "TEXT")
    private String contentTemplate;

    /**
     * 模板参数说明（JSON格式）
     */
    @Column(name = "param_description", columnDefinition = "TEXT")
    private String paramDescription;

    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * 创建者ID
     */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

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

    /**
     * 检查是否支持指定渠道
     */
    public boolean supportsChannel(NotificationChannel channel) {
        return supportedChannels != null && supportedChannels.contains(channel.getCode());
    }
}

package com.qianshe.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 通知统计数据传输对象
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "通知统计信息")
public class NotificationStatisticsDTO {

    @Schema(description = "总通知数")
    private Long totalCount;

    @Schema(description = "待发送数量")
    private Long pendingCount;

    @Schema(description = "发送中数量")
    private Long sendingCount;

    @Schema(description = "发送成功数量")
    private Long successCount;

    @Schema(description = "发送失败数量")
    private Long failedCount;

    @Schema(description = "已读数量")
    private Long readCount;

    @Schema(description = "已取消数量")
    private Long cancelledCount;

    @Schema(description = "成功率")
    private Double successRate;

    @Schema(description = "各渠道统计")
    private Map<String, Long> channelStatistics;

    @Schema(description = "各类型统计")
    private Map<String, Long> typeStatistics;

    @Schema(description = "各状态统计")
    private Map<String, Long> statusStatistics;

    /**
     * 计算成功率
     */
    public void calculateSuccessRate() {
        if (totalCount != null && totalCount > 0) {
            long sentCount = (successCount != null ? successCount : 0) + (failedCount != null ? failedCount : 0);
            if (sentCount > 0) {
                this.successRate = (double) (successCount != null ? successCount : 0) / sentCount * 100;
            } else {
                this.successRate = 0.0;
            }
        } else {
            this.successRate = 0.0;
        }
    }
}

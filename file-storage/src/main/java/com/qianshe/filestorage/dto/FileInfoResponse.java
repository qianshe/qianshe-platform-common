package com.qianshe.filestorage.dto;

import com.qianshe.filestorage.enums.FileAccessType;
import com.qianshe.filestorage.enums.FileStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息响应DTO
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Schema(description = "文件信息响应")
public class FileInfoResponse {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "123")
    private Long id;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "avatar.jpg")
    private String originalName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long fileSize;

    /**
     * 文件大小（格式化）
     */
    @Schema(description = "文件大小（格式化）", example = "1.0 MB")
    private String fileSizeFormatted;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExtension;

    /**
     * 文件状态
     */
    @Schema(description = "文件状态")
    private FileStatus status;

    /**
     * 访问权限类型
     */
    @Schema(description = "访问权限类型")
    private FileAccessType accessType;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "avatar")
    private String businessType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "user_123")
    private String businessId;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数", example = "10")
    private Long downloadCount;

    /**
     * 下载URL
     */
    @Schema(description = "下载URL", example = "/api/v1/files/123")
    private String downloadUrl;

    /**
     * 预览URL（仅图片文件）
     */
    @Schema(description = "预览URL", example = "/api/v1/files/123/preview")
    private String previewUrl;

    /**
     * 缩略图URL列表
     */
    @Schema(description = "缩略图URL列表")
    private List<ThumbnailInfo> thumbnails;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 缩略图信息
     */
    @Data
    @Schema(description = "缩略图信息")
    public static class ThumbnailInfo {
        
        /**
         * 宽度
         */
        @Schema(description = "宽度", example = "100")
        private Integer width;

        /**
         * 高度
         */
        @Schema(description = "高度", example = "100")
        private Integer height;

        /**
         * 缩略图URL
         */
        @Schema(description = "缩略图URL", example = "/api/v1/files/123/thumbnail/100x100")
        private String url;
    }
}

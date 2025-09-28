package com.qianshe.filestorage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 文件存储配置
 * 
 * @author qianshe
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {

    /**
     * 存储类型（LOCAL/OSS/COS等）
     */
    private String type = "LOCAL";

    /**
     * 本地存储配置
     */
    private Local local = new Local();

    /**
     * 阿里云OSS配置
     */
    private Oss oss = new Oss();

    /**
     * 文件上传限制
     */
    private Upload upload = new Upload();

    /**
     * 图片处理配置
     */
    private Image image = new Image();

    /**
     * 清理策略配置
     */
    private Cleanup cleanup = new Cleanup();

    /**
     * 本地存储配置
     */
    @Data
    public static class Local {
        /**
         * 存储根目录
         */
        private String basePath = "/app/files";

        /**
         * 访问URL前缀
         */
        private String urlPrefix = "/files";

        /**
         * 是否按日期分目录
         */
        private Boolean dateFolder = true;
    }

    /**
     * 阿里云OSS配置
     */
    @Data
    public static class Oss {
        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥Secret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 自定义域名
         */
        private String customDomain;

        /**
         * 存储路径前缀
         */
        private String pathPrefix = "files";
    }

    /**
     * 文件上传限制配置
     */
    @Data
    public static class Upload {
        /**
         * 单个文件最大大小（字节）
         */
        private Long maxFileSize = 100 * 1024 * 1024L; // 100MB

        /**
         * 单次上传最大文件数
         */
        private Integer maxFileCount = 10;

        /**
         * 允许的文件类型
         */
        private List<String> allowedTypes = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf", "text/plain",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        /**
         * 禁止的文件扩展名
         */
        private List<String> forbiddenExtensions = List.of(
            "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js"
        );
    }

    /**
     * 图片处理配置
     */
    @Data
    public static class Image {
        /**
         * 是否启用图片处理
         */
        private Boolean enabled = true;

        /**
         * 默认缩略图尺寸
         */
        private List<String> defaultThumbnailSizes = List.of("100,100", "200,200", "400,400");

        /**
         * 图片质量（0.0-1.0）
         */
        private Float quality = 0.8f;

        /**
         * 水印文字
         */
        private String watermarkText;

        /**
         * 水印透明度（0.0-1.0）
         */
        private Float watermarkOpacity = 0.5f;
    }

    /**
     * 清理策略配置
     */
    @Data
    public static class Cleanup {
        /**
         * 是否启用自动清理
         */
        private Boolean enabled = true;

        /**
         * 临时文件保留时间（小时）
         */
        private Integer tempFileRetentionHours = 24;

        /**
         * 已删除文件保留时间（天）
         */
        private Integer deletedFileRetentionDays = 30;

        /**
         * 长期未访问文件阈值（天）
         */
        private Integer inactiveFileThresholdDays = 365;

        /**
         * 清理任务执行时间（cron表达式）
         */
        private String cleanupCron = "0 0 2 * * ?"; // 每天凌晨2点执行
    }
}

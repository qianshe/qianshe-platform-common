package com.qianshe.common.constants;

/**
 * 通用常量定义
 * 
 * @author qianshe
 * @since 1.0.0
 */
public final class CommonConstants {
    
    private CommonConstants() {
        // 工具类，禁止实例化
    }
    
    /**
     * 分页相关常量
     */
    public static final class Page {
        public static final int DEFAULT_SIZE = 20; // 默认分页大小
        public static final int MAX_SIZE = 100; // 最大分页大小
        public static final int DEFAULT_PAGE = 0; // 默认页码
    }
    
    /**
     * 缓存相关常量
     */
    public static final class Cache {
        public static final int DEFAULT_TTL = 1800; // 默认缓存时间30分钟
        public static final int SHORT_TTL = 300; // 短期缓存5分钟
        public static final int LONG_TTL = 3600; // 长期缓存1小时
    }
    
    /**
     * 审核相关常量
     */
    public static final class Audit {
        public static final int TIMEOUT_HOURS = 24; // 审核超时时间(小时)
        public static final int MAX_AUDIT_BATCH = 100; // 最大批量审核数
    }
    
    /**
     * 文件相关常量
     */
    public static final class File {
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
        public static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"};
    }
    
    /**
     * 通知相关常量
     */
    public static final class Notification {
        public static final int BATCH_SIZE = 50; // 批量通知大小
        public static final int DELAY_SECONDS = 5; // 通知延迟时间
        public static final int MAX_RETRY_TIMES = 3; // 最大重试次数
    }
}

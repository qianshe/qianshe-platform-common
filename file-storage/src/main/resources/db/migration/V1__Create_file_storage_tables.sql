-- 文件存储服务数据库初始化脚本
-- 版本: V1.0.0
-- 创建时间: 2024-12-28

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS qianshe_file_storage 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qianshe_file_storage;

-- 文件信息表
CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    mime_type VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    file_extension VARCHAR(20) COMMENT '文件扩展名',
    file_hash VARCHAR(64) NOT NULL COMMENT '文件哈希值（MD5）',
    storage_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    storage_type VARCHAR(20) NOT NULL DEFAULT 'LOCAL' COMMENT '存储类型',
    status VARCHAR(20) NOT NULL DEFAULT 'UPLOADED' COMMENT '文件状态',
    access_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE' COMMENT '访问权限类型',
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id VARCHAR(100) COMMENT '业务ID',
    download_count BIGINT DEFAULT 0 COMMENT '下载次数',
    last_access_time DATETIME COMMENT '最后访问时间',
    expire_time DATETIME COMMENT '过期时间',
    extra_data JSON COMMENT '扩展数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_file_hash (file_hash),
    INDEX idx_user_id (user_id),
    INDEX idx_business (business_type, business_id),
    INDEX idx_status (status),
    INDEX idx_access_type (access_type),
    INDEX idx_created_at (created_at),
    INDEX idx_expire_time (expire_time),
    INDEX idx_storage_path (storage_path),
    
    -- 复合索引
    INDEX idx_user_status (user_id, status),
    INDEX idx_business_status (business_type, business_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 文件访问日志表
CREATE TABLE IF NOT EXISTS file_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    user_id BIGINT COMMENT '用户ID',
    operation VARCHAR(20) NOT NULL COMMENT '操作类型',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    referer VARCHAR(500) COMMENT '来源页面',
    response_time BIGINT COMMENT '响应时间（毫秒）',
    bytes_transferred BIGINT COMMENT '传输字节数',
    status_code INT COMMENT 'HTTP状态码',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_user_id (user_id),
    INDEX idx_operation (operation),
    INDEX idx_created_at (created_at),
    INDEX idx_ip_address (ip_address),
    
    -- 复合索引
    INDEX idx_file_user (file_id, user_id),
    INDEX idx_file_operation (file_id, operation),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES file_info(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件访问日志表';

-- 插入初始数据（可选）
-- 这里可以插入一些测试数据或默认配置

-- 创建视图：文件统计信息
CREATE OR REPLACE VIEW file_statistics AS
SELECT 
    DATE(created_at) as date,
    COUNT(*) as total_files,
    SUM(file_size) as total_size,
    COUNT(DISTINCT user_id) as unique_users,
    AVG(file_size) as avg_file_size
FROM file_info 
WHERE status = 'AVAILABLE'
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- 创建视图：用户文件统计
CREATE OR REPLACE VIEW user_file_statistics AS
SELECT 
    user_id,
    COUNT(*) as total_files,
    SUM(file_size) as total_size,
    SUM(download_count) as total_downloads,
    MAX(created_at) as last_upload_time
FROM file_info 
WHERE status = 'AVAILABLE'
GROUP BY user_id;

-- 创建存储过程：清理过期文件
DELIMITER //
CREATE PROCEDURE CleanupExpiredFiles()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE file_id BIGINT;
    DECLARE storage_path VARCHAR(500);
    
    -- 声明游标
    DECLARE cur CURSOR FOR 
        SELECT id, storage_path 
        FROM file_info 
        WHERE expire_time IS NOT NULL 
        AND expire_time < NOW() 
        AND status != 'DELETED';
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 打开游标
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO file_id, storage_path;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 更新文件状态为已删除
        UPDATE file_info 
        SET status = 'DELETED', updated_at = NOW() 
        WHERE id = file_id;
        
        -- 记录清理日志
        INSERT INTO file_access_log (file_id, operation, created_at) 
        VALUES (file_id, 'CLEANUP', NOW());
        
    END LOOP;
    
    -- 关闭游标
    CLOSE cur;
    
    -- 提交事务
    COMMIT;
    
    -- 返回清理的文件数量
    SELECT ROW_COUNT() as cleaned_files;
END //
DELIMITER ;

-- 创建事件：定时清理过期文件（每天凌晨2点执行）
-- 注意：需要确保事件调度器已启用 (SET GLOBAL event_scheduler = ON;)
CREATE EVENT IF NOT EXISTS cleanup_expired_files
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE + INTERVAL 1 DAY, '02:00:00')
DO
  CALL CleanupExpiredFiles();

-- 创建触发器：自动记录文件下载日志
DELIMITER //
CREATE TRIGGER after_download_count_update
AFTER UPDATE ON file_info
FOR EACH ROW
BEGIN
    IF NEW.download_count > OLD.download_count THEN
        INSERT INTO file_access_log (
            file_id, 
            operation, 
            created_at
        ) VALUES (
            NEW.id, 
            'DOWNLOAD', 
            NOW()
        );
    END IF;
END //
DELIMITER ;

-- 授权语句（根据实际需要调整）
-- GRANT SELECT, INSERT, UPDATE, DELETE ON qianshe_file_storage.* TO 'file_storage_user'@'%';
-- FLUSH PRIVILEGES;

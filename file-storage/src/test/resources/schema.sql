-- 文件信息表
CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    mime_type VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    file_extension VARCHAR(20) COMMENT '文件扩展名',
    file_hash VARCHAR(64) NOT NULL COMMENT '文件哈希值',
    status VARCHAR(20) NOT NULL DEFAULT 'UPLOADING' COMMENT '文件状态',
    access_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE' COMMENT '访问权限类型',
    user_id BIGINT NOT NULL COMMENT '文件所有者ID',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id VARCHAR(100) COMMENT '业务ID',
    storage_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    storage_type VARCHAR(20) NOT NULL DEFAULT 'LOCAL' COMMENT '存储类型',
    download_count BIGINT NOT NULL DEFAULT 0 COMMENT '下载次数',
    last_access_time DATETIME COMMENT '最后访问时间',
    expire_time DATETIME COMMENT '过期时间',
    extra_data TEXT COMMENT '扩展数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建索引
CREATE INDEX idx_file_info_user_id ON file_info(user_id);
CREATE INDEX idx_file_info_business ON file_info(business_type, business_id);
CREATE INDEX idx_file_info_hash ON file_info(file_hash);
CREATE INDEX idx_file_info_status ON file_info(status);
CREATE INDEX idx_file_info_created_at ON file_info(created_at);

-- 文件访问日志表
CREATE TABLE IF NOT EXISTS file_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_id BIGINT NOT NULL COMMENT '文件ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(20) NOT NULL COMMENT '操作类型',
    client_ip VARCHAR(45) COMMENT '客户端IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    referer VARCHAR(500) COMMENT '请求来源',
    response_status INT COMMENT '响应状态码',
    response_time BIGINT COMMENT '响应时间（毫秒）',
    bytes_transferred BIGINT COMMENT '传输字节数',
    error_message VARCHAR(1000) COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 创建索引
CREATE INDEX idx_file_access_log_file_id ON file_access_log(file_id);
CREATE INDEX idx_file_access_log_user_id ON file_access_log(user_id);
CREATE INDEX idx_file_access_log_action ON file_access_log(action);
CREATE INDEX idx_file_access_log_created_at ON file_access_log(created_at);

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qianshe_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE qianshe_user;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `status` tinyint(4) DEFAULT 0 COMMENT '状态（0：正常；1：禁用）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- OAuth2用户关联表
CREATE TABLE IF NOT EXISTS `sys_user_oauth` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `oauth_type` varchar(20) NOT NULL COMMENT 'OAuth2类型（如：github、google）',
    `oauth_id` varchar(100) NOT NULL COMMENT 'OAuth2唯一标识',
    `username` varchar(100) DEFAULT NULL COMMENT 'OAuth2用户名',
    `avatar` varchar(255) DEFAULT NULL COMMENT 'OAuth2头像',
    `email` varchar(100) DEFAULT NULL COMMENT 'OAuth2邮箱',
    `access_token` varchar(255) DEFAULT NULL COMMENT '访问令牌',
    `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新令牌',
    `expires_in` bigint(20) DEFAULT NULL COMMENT '过期时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_oauth_id_type` (`oauth_id`, `oauth_type`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2用户关联表';

-- 初始化管理员账号
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `status`)
VALUES ('admin', '$2a$10$wrkdDTO4/cUgId5FjeT0P.2rk3eohLjKltulB4loklW364Rr05jla', '管理员', 0);

-- 为用户表添加用户类型字段
ALTER TABLE sys_user ADD COLUMN user_type VARCHAR(20) DEFAULT 'user' COMMENT '用户类型（user：普通用户，admin：管理员，vip：VIP用户）';

-- 更新已有的超级管理员为admin类型
UPDATE sys_user SET user_type = 'admin' WHERE username = 'admin';
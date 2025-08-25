

-- 创建用户类型字典表
CREATE TABLE IF NOT EXISTS `sys_user_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(20) NOT NULL COMMENT '类型编码',
  `name` varchar(50) NOT NULL COMMENT '类型名称',
  `description` varchar(200) DEFAULT NULL COMMENT '类型描述',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0：启用，1：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户类型字典表';

-- 插入默认的用户类型数据
INSERT INTO `sys_user_type` (`code`, `name`, `description`, `status`) VALUES
('user', '普通用户', '普通注册用户', 0),
('admin', '管理员', '系统管理员', 0),
('vip', 'VIP用户', '付费会员用户', 0);

-- 创建用户角色关系表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`,`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0：启用，1：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 插入默认角色
INSERT INTO `sys_role` (`code`, `name`, `description`, `status`) VALUES
('admin', '管理员', '系统管理员，拥有所有权限', 0),
('user', '用户', '普通注册用户', 0),
('vip', 'VIP用户', '付费会员用户', 0); 
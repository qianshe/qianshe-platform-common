# 🔐 认证服务 (Auth Service)

## 📖 服务简介
Auth服务就像一个智能保安，负责：
- 👥 管理用户的注册和登录
- 🎫 发放和验证通行证（Token）
- 🔑 控制用户的权限
- 🛡️ 保护用户的账号安全

## 🌟 核心功能

### 1. 用户管理 👤
- 用户注册：支持多种注册方式
- 用户登录：安全的身份验证
- 信息管理：维护用户资料
- 状态管理：控制账号状态

### 2. 权限控制 🔒
```java
// 登录示例
@PostMapping("/login")
public Result<String> login(@RequestBody LoginDTO dto) {
    // 1. 验证用户名密码
    User user = userService.login(dto);
    // 2. 生成访问令牌
    String token = StpUtil.createLoginSession(user.getId());
    // 3. 返回令牌
    return Result.ok(token);
}

// 权限检查示例
@SaCheckRole("admin")
@GetMapping("/users")
public Result<List<UserVO>> listUsers() {
    // 只有管理员可以查看用户列表
    return Result.ok(userService.listUsers());
}
```

### 3. 安全保护 🛡️
- 密码加密：使用BCrypt加密
- 登录保护：防止暴力破解
- 账号保护：异常行为检测
- 操作日志：记录重要操作

## 💾 数据库设计

### 1. 用户表 (fantasy_user)
```sql
CREATE TABLE `fantasy_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `nickname` varchar(50) COMMENT '昵称',
    `avatar` varchar(255) COMMENT '头像URL',
    `email` varchar(100) COMMENT '邮箱',
    `phone` varchar(20) COMMENT '手机号',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-正常，1-禁用',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 角色表 (fantasy_role)
```sql
CREATE TABLE `fantasy_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` varchar(50) NOT NULL COMMENT '角色名称',
    `code` varchar(50) NOT NULL COMMENT '角色编码',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

### 3. 用户角色关联表 (fantasy_user_role)
```sql
CREATE TABLE `fantasy_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

## 🔌 API接口

### 1. 用户接口
```http
# 用户注册
POST /auth/register
{
    "username": "test",
    "password": "123456",
    "email": "test@example.com",
    "phone": "13800138000"
}

# 用户登录
POST /auth/login
{
    "username": "test",
    "password": "123456"
}

# 获取用户信息
GET /auth/info

# 修改密码
PUT /auth/password
{
    "oldPassword": "123456",
    "newPassword": "newpass123"
}
```

### 2. 角色接口
```http
# 创建角色
POST /auth/roles

# 更新角色
PUT /auth/roles

# 删除角色
DELETE /auth/roles/{roleId}

# 获取角色列表
GET /auth/roles
```

## 🎯 未来规划

### 1. 认证功能
- [ ] OAuth2.0集成：支持第三方登录
- [ ] 手机验证码：短信验证登录
- [ ] 邮箱验证码：邮箱验证登录
- [ ] 扫码登录：支持二维码登录

### 2. 安全功能
- [ ] 密码强度检查
- [ ] IP登录限制
- [ ] 设备数量限制
- [ ] 异地登录提醒

### 3. 权限功能
- [ ] 数据权限控制
- [ ] 字段级别权限
- [ ] 操作权限管理
- [ ] 多租户支持

## 🚀 快速开始

### 1. 环境准备
```bash
# 确保已安装：
- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Nacos 2.2.0+
```

### 2. 数据库配置
1. 创建数据库
```sql
CREATE DATABASE fantasy_auth DEFAULT CHARACTER SET utf8mb4;
```

2. 导入表结构
```bash
mysql -u root -p fantasy_auth < schema.sql
```

### 3. 修改配置
1. 修改数据库连接信息
2. 修改Redis连接信息
3. 修改Nacos配置

### 4. 启动服务
```bash
# 在auth目录下运行
mvn spring-boot:run
```

## 📚 API文档
- 访问地址：http://localhost:9001/doc.html
- 包含所有认证相关接口
- 支持在线调试功能

## 💡 使用建议
1. 定期修改密码
2. 启用双因素认证
3. 监控异常登录
4. 定期检查权限

## 🔍 常见问题
1. 登录失败
   - 检查用户名密码是否正确
   - 确认账号是否被锁定
   - 查看登录日志

2. 权限不足
   - 检查用户角色是否正确
   - 确认权限是否已分配
   - 查看权限配置

3. Token失效
   - 检查Token是否过期
   - 确认是否异地登录
   - 尝试重新登录

## 🤝 需要帮助？
如果遇到问题，可以：
1. 查看错误日志
2. 检查配置文件
3. 联系技术支持
4. 查看在线文档 
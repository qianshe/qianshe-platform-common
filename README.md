# 千社平台基础设施项目

## 概述
本项目包含千社平台的基础设施组件，为业务项目提供通用的功能支持。

## 模块说明

### common - 公共组件模块
- 通用实体类和工具类
- 统一异常处理
- 统一响应格式
- 安全工具类

### gateway - API网关模块
- 请求路由和负载均衡
- 统一鉴权和权限控制
- 请求限流和黑名单过滤
- 日志记录和监控

### user-auth - 用户认证模块
- 用户注册和登录
- 验证码生成和验证
- 密码重置和找回
- 多账号体系支持

## 版本信息
- **当前版本**: 1.0.0
- **依赖BOM**: qianshe-platform-bom:1.0.0

## 构建说明

### 前提条件
- JDK 17+
- Maven 3.8+
- 已安装 qianshe-platform-bom

### 构建步骤
```bash
# 清理并编译
mvn clean compile

# 安装到本地仓库
mvn clean install

# 打包
mvn clean package
```

## 🚀 部署方式

### Docker部署（推荐）

⚠️ **前提条件**：确保 `qianshe-network` 网络已创建
```bash
# 1. 初始化网络（在工作空间根目录）
cd ..
./quick-migrate-network.bat

# 2. 启动基础设施服务
cd qianshe-platform-common
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f gateway
docker-compose logs -f user-auth

# 5. 停止服务
docker-compose down
```

**包含服务**：
- `gateway` - API网关（端口: 9000）
- `user-auth` - 用户认证服务

### 传统Maven运行
```bash
# 启动认证服务
cd user-auth
mvn spring-boot:run

# 启动网关服务
cd gateway
mvn spring-boot:run
```

## 🌐 网络配置

- **统一网络**: `qianshe-network`
- **服务通信**: 通过服务名进行内部通信
- **外部访问**: 只有gateway暴露9000端口
- **容器名称**: 与工作空间统一配置保持一致

⚠️ **注意**: 不能与工作空间统一部署同时运行，会有容器名冲突

## 配置说明
各模块的配置文件位于对应的 `src/main/resources` 目录下：
- `application.yml` - 应用配置
- `bootstrap.yml` - 启动配置
- `nacos-logback.xml` - 日志配置

## 依赖项目
- [qianshe-platform-bom](../qianshe-platform-bom) - 版本管理

## 被依赖项目
- fantasy-center - 业务项目
- 其他业务项目（future）
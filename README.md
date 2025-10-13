# 千社平台基础设施项目

## 📋 项目概述

千社平台（Qianshe Platform）基础设施项目，基于Spring Cloud微服务架构，为业务项目提供统一的基础设施服务支持。项目采用BOM统一版本管理，确保所有组件的技术栈一致性。

## 🏗️ 项目架构

### 📦 基础设施模块

#### common - 公共组件模块
- **通用组件**: 统一实体类、工具类、常量定义
- **异常处理**: 全局异常处理器和错误码管理
- **响应格式**: 统一的API响应格式 `Result<T>`
- **安全工具**: Sa-Token安全认证工具类
- **配置管理**: 统一配置类和属性绑定

#### gateway - API网关模块
- **请求路由**: 基于Nacos的动态路由和负载均衡
- **统一鉴权**: Sa-Token多账号体系权限控制
- **流量控制**: 请求限流、熔断降级、黑名单过滤
- **监控日志**: 请求日志记录和性能监控
- **API聚合**: 统一的API入口和文档聚合

#### user-auth - 用户认证模块
- **用户管理**: 用户注册、登录、信息管理
- **认证授权**: JWT令牌生成和验证
- **验证码服务**: 图形验证码、短信验证码
- **密码安全**: 密码加密、重置、找回功能
- **多账号体系**: 支持用户、VIP、管理员多层级账号

#### file-storage - 文件存储模块
- **文件管理**: 文件上传、下载、删除、预览
- **存储抽象**: 支持本地存储和云存储(OSS)
- **权限控制**: 四级权限模型(公开、私有、业务、管理员)
- **图片处理**: 缩略图生成、格式转换、水印
- **审计日志**: 完整的文件操作记录和访问统计

#### notification - 通知服务模块
- **消息推送**: 邮件、短信、站内消息
- **模板管理**: 消息模板配置和管理
- **推送策略**: 异步推送和批量处理
- **送达统计**: 消息送达率和状态跟踪

#### operation - 运营服务模块
- **数据统计**: 用户行为统计和业务数据分析
- **报表生成**: 自动化报表生成和导出
- **系统监控**: 服务健康状态和性能监控
- **运营工具**: 内容审核、用户管理等运营功能

## 🛠️ 技术栈

### 🎯 核心框架
- **Java**: JDK 17
- **Spring Boot**: 3.0.12
- **Spring Cloud**: 微服务架构
- **Spring Cloud Alibaba**: Nacos集成
- **Maven**: 多模块项目管理
- **依赖管理**: qianshe-platform-bom (v1.0.0)

### 🗄️ 数据存储
- **数据库**: MySQL 8.0+
- **连接池**: Druid (阿里巴巴数据源)
- **ORM框架**: MyBatis Plus + JPA/Hibernate
- **缓存**: Redis
- **消息队列**: RabbitMQ

### 🔐 安全与认证
- **权限认证**: Sa-Token
- **Redis集成**: sa-token-redis-jackson
- **JWT**: 无状态令牌认证
- **负载均衡**: Spring Cloud LoadBalancer

### 📚 API与文档
- **API文档**: SpringDoc OpenAPI 3.0
- **接口测试**: Swagger UI / Knife4j
- **数据校验**: Spring Boot Validation
- **接口版本**: 统一版本管理

### 🛠️ 开发工具
- **代码简化**: Lombok
- **编译优化**: 参数保留编译
- **测试框架**: Spring Boot Test + JUnit 5
- **容器化**: Docker + Docker Compose

### 🌐 基础设施
- **服务发现**: Nacos
- **配置中心**: Nacos Config
- **健康检查**: Spring Boot Actuator
- **监控指标**: Prometheus + Micrometer

## 📊 版本信息
- **当前版本**: 1.0.0
- **依赖BOM**: qianshe-platform-bom:1.0.0
- **Java版本**: JDK 17
- **Spring Boot版本**: 3.0.12

## 🚀 快速开始

### 📋 环境要求

```bash
# 必需环境
- JDK 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.0+
```

### 🏗️ 构建说明

#### 前提条件
- JDK 17+
- Maven 3.8+
- 已安装 qianshe-platform-bom

#### 构建步骤
```bash
# 清理并编译
mvn clean compile

# 安装到本地仓库
mvn clean install

# 打包
mvn clean package
```

### 🏃‍♂️ 部署方式

#### 方式一：Docker统一部署（推荐）

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
docker-compose logs -f file-storage

# 5. 停止服务
docker-compose down
```

**包含服务**：
- `gateway` - API网关（端口: 9000）
- `user-auth` - 用户认证服务（端口: 9001）
- `file-storage` - 文件存储服务（端口: 8092）

#### 方式二：传统Maven运行
```bash
# 启动认证服务
cd user-auth
mvn spring-boot:run

# 启动网关服务
cd gateway
mvn spring-boot:run

# 启动文件存储服务
cd file-storage
mvn spring-boot:run
```

### 🌐 网络配置

- **统一网络**: `qianshe-network`
- **服务通信**: 通过服务名进行内部通信
- **外部访问**: gateway(9000)、file-storage(8092)
- **容器名称**: 与工作空间统一配置保持一致

⚠️ **注意**: 不能与工作空间统一部署同时运行，会有容器名冲突

## 📊 服务端口

| 服务名称 | 端口 | 描述 | 状态 |
|---------|------|------|------|
| gateway | 9000 | API网关，对外暴露 | ✅ 运行中 |
| user-auth | 9001 | 用户认证服务 | ✅ 运行中 |
| file-storage | 8092 | 文件存储服务 | ✅ 运行中 |
| notification | - | 通知服务 | 🚧 开发中 |
| operation | - | 运营服务 | 🚧 开发中 |

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
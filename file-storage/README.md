# 文件存储服务 (File Storage Service)

## 📋 服务概述

文件存储服务是千社平台的核心基础设施组件，基于Spring Cloud微服务架构，提供统一的文件上传、下载、管理功能。支持多种存储后端，具备完善的权限控制、审计日志和图片处理能力，为业务项目提供可靠的文件存储解决方案。

## 🏗️ 架构特点

### 🎯 核心功能
- **文件管理**: 文件上传、下载、删除、预览、批量操作
- **权限控制**: 四级权限模型（公开、私有、业务、管理员）
- **存储抽象**: 支持本地存储和云存储（阿里云OSS、腾讯云COS等）
- **图片处理**: 缩略图生成、格式转换、水印添加、尺寸调整
- **审计日志**: 完整的文件操作记录和访问统计分析
- **文件去重**: 基于哈希值的智能去重机制，节省存储空间
- **安全防护**: 文件类型校验、大小限制、病毒扫描接口

### 🛠️ 技术栈

#### 🎯 核心框架
- **Java**: JDK 17
- **Spring Boot**: 3.0.12
- **Spring Cloud**: 微服务架构
- **Spring Cloud Alibaba**: Nacos服务发现
- **Maven**: 项目构建管理
- **依赖管理**: qianshe-platform-bom (v1.0.0)

#### 🗄️ 数据存储
- **数据库**: MySQL 8.0+
- **连接池**: Druid (阿里巴巴数据源)
- **ORM框架**: MyBatis Plus + JPA/Hibernate
- **缓存**: Redis
- **文件存储**: 本地文件系统 / 云存储

#### 🔐 安全与认证
- **权限认证**: Sa-Token
- **Redis集成**: sa-token-redis-jackson
- **JWT**: 无状态令牌认证
- **文件安全**: 类型校验、大小限制、路径防护

#### 📚 API与文档
- **API文档**: SpringDoc OpenAPI 3.0
- **接口测试**: Swagger UI / Knife4j
- **数据校验**: Spring Boot Validation
- **接口版本**: 统一版本管理 `/api/v1/files`

#### 🛠️ 开发工具
- **代码简化**: Lombok
- **编译优化**: 参数保留编译
- **测试框架**: Spring Boot Test + JUnit 5
- **容器化**: Docker + Docker Compose

#### 🌐 基础设施
- **服务发现**: Nacos
- **配置中心**: Nacos Config
- **健康检查**: Spring Boot Actuator
- **监控指标**: Prometheus + Micrometer

## 📁 项目结构

```
file-storage/
├── src/main/java/com/qianshe/filestorage/
│   ├── FileStorageApplication.java      # 启动类
│   ├── config/
│   │   └── FileStorageConfig.java       # 配置类
│   ├── controller/
│   │   └── FileController.java          # REST控制器
│   ├── service/
│   │   ├── FileService.java             # 服务接口
│   │   └── impl/FileServiceImpl.java    # 服务实现
│   ├── storage/
│   │   ├── StorageService.java          # 存储接口
│   │   ├── StorageException.java        # 存储异常
│   │   └── LocalStorageService.java     # 本地存储实现
│   ├── entity/
│   │   ├── FileInfo.java                # 文件信息实体
│   │   └── FileAccessLog.java           # 访问日志实体
│   ├── repository/
│   │   ├── FileInfoRepository.java      # 文件信息Repository
│   │   └── FileAccessLogRepository.java # 访问日志Repository
│   ├── dto/
│   │   ├── FileUploadRequest.java       # 上传请求DTO
│   │   └── FileInfoResponse.java        # 文件信息响应DTO
│   └── enums/
│       ├── FileStatus.java              # 文件状态枚举
│       └── FileAccessType.java          # 访问权限枚举
├── src/main/resources/
│   └── application.yml                  # 应用配置
├── Dockerfile                           # Docker构建文件
├── pom.xml                             # Maven配置
└── README.md                           # 项目说明
```

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

### 🗄️ 数据库配置

#### 创建数据库
```sql
CREATE DATABASE qianshe_file_storage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 初始化表结构
```sql
-- 表结构文件位于：src/main/resources/db/migration/
-- 或执行：src/main/resources/db/qianshe_file_storage_schema.sql
```

### 🏃‍♂️ 启动方式

#### 方式一：Docker部署（推荐）
```bash
# 1. 确保网络已初始化
cd ../../..
./quick-migrate-network.bat

# 2. 启动文件存储服务
cd qianshe-platform-common
docker-compose up -d file-storage

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f file-storage
```

#### 方式二：传统Maven运行
```bash
# 1. 确保依赖服务已启动（Nacos、MySQL、Redis）
# 2. 启动文件存储服务
mvn spring-boot:run
```

#### 方式三：工作空间统一启动
```bash
# 在工作空间根目录启动所有服务
cd ../../..
docker-compose up -d
```

### 🌐 服务访问

| 服务 | 地址 | 描述 |
|------|------|------|
| **API文档** | http://localhost:8092/swagger-ui.html | Swagger UI接口文档 |
| **API文档** | http://localhost:8092/doc.html | Knife4j增强文档 |
| **健康检查** | http://localhost:8092/actuator/health | 服务健康状态 |
| **监控指标** | http://localhost:8092/actuator/prometheus | Prometheus监控指标 |
| **服务信息** | http://localhost:8092/actuator/info | 服务详细信息 |

### 📊 服务配置
- **服务端口**: 8092
- **服务名称**: file-storage
- **数据库**: qianshe_file_storage
- **网络**: qianshe-network

## 📚 API接口

### 文件上传
```http
POST /api/v1/files/upload
Content-Type: multipart/form-data

file: [文件]
accessType: PUBLIC|PRIVATE|BUSINESS|ADMIN
businessType: avatar|document|image
businessId: user_123
```

### 文件下载
```http
GET /api/v1/files/{fileId}
Authorization: Bearer {token}
```

### 文件信息
```http
GET /api/v1/files/{fileId}/info
Authorization: Bearer {token}
```

### 用户文件列表
```http
GET /api/v1/files/my?page=0&size=20
Authorization: Bearer {token}
```

## ⚙️ 配置说明

### 存储配置
```yaml
file:
  storage:
    type: LOCAL  # LOCAL|OSS
    local:
      base-path: /app/files
      url-prefix: /files
      date-folder: true
    upload:
      max-file-size: 104857600  # 100MB
      max-file-count: 10
      allowed-types:
        - image/jpeg
        - image/png
        - application/pdf
```

### 权限模型
- **PUBLIC**：公开访问，无需认证
- **PRIVATE**：私有文件，仅所有者可访问
- **BUSINESS**：业务文件，需要业务权限
- **ADMIN**：管理员文件，需要管理员权限

## 🔧 开发指南

### 添加新的存储后端
1. 实现 `StorageService` 接口
2. 添加 `@ConditionalOnProperty` 注解
3. 在配置文件中添加相应配置

### 扩展文件处理器
1. 创建处理器类实现相应接口
2. 注册为Spring Bean
3. 在服务层调用处理器

## 📊 监控指标

服务提供以下监控指标：
- 文件上传/下载次数
- 存储空间使用情况
- API响应时间
- 错误率统计
- 系统资源使用情况

## 🐳 Docker部署

### 构建镜像
```bash
docker build -t qianshe/file-storage:latest .
```

### 运行容器
```bash
docker run -d \
  --name file-storage \
  -p 8092:8092 \
  -v file-storage-data:/app/files \
  qianshe/file-storage:latest
```

## 🔍 故障排查

### 常见问题
1. **文件上传失败**：检查文件大小和类型限制
2. **权限错误**：确认用户认证状态和文件权限
3. **存储空间不足**：检查磁盘空间和配置限制
4. **数据库连接失败**：确认数据库服务状态和连接配置

### 日志查看
```bash
# Docker环境
docker logs file-storage

# 本地环境
tail -f logs/file-storage.log
```

## 📝 更新日志

### v1.0.0 (当前版本)
- ✅ 完整的文件管理功能（上传、下载、删除、预览）
- ✅ 四级权限控制模型（公开、私有、业务、管理员）
- ✅ 存储抽象层设计（本地存储 + 云存储支持）
- ✅ 图片处理功能（缩略图、格式转换、水印）
- ✅ 审计日志和访问统计
- ✅ 文件去重机制
- ✅ 安全防护（类型校验、大小限制）
- ✅ Spring Cloud微服务架构
- ✅ Docker容器化部署
- ✅ 统一的技术栈和依赖管理

### 🔮 计划功能
- 🚧 云存储集成（阿里云OSS、腾讯云COS）
- 🚧 文件版本管理
- 🚧 分布式存储支持
- 🚧 文件分享和协作功能
- 🚧 高级图片处理（OCR、智能裁剪）

## 🔗 相关服务

- **qianshe-platform-common**: 基础设施项目
- **gateway**: API网关服务 (端口: 9000)
- **user-auth**: 用户认证服务 (端口: 9001)
- **fantasy-backend**: 业务项目依赖

## 📞 技术支持

如遇到问题，请：
1. 查看日志文件排查问题
2. 检查网络和依赖服务状态
3. 参考API文档和配置说明
4. 联系项目维护团队

---

💡 **提示**: 本服务是千社平台基础设施的核心组件，确保与网关和认证服务协同使用以获得完整功能。

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码变更
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

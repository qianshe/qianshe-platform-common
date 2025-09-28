# 文件存储服务 (File Storage Service)

## 📋 服务概述

文件存储服务是千舍平台的核心基础设施服务，提供统一的文件上传、下载、管理功能。支持多种存储后端，具备完善的权限控制、审计日志和图片处理能力。

## 🏗️ 架构特点

### 核心功能
- **文件上传下载**：支持单文件和批量文件操作
- **权限控制**：四级权限模型（公开、私有、业务、管理员）
- **存储抽象**：支持本地存储和云存储（OSS等）
- **图片处理**：缩略图生成、格式转换、水印添加
- **审计日志**：完整的文件操作记录和访问统计
- **文件去重**：基于哈希值的智能去重机制

### 技术栈
- **框架**：Spring Boot 3.0.12 + Spring Cloud
- **数据库**：MySQL 8.0 + JPA/Hibernate
- **缓存**：Redis
- **认证**：Sa-Token
- **文档**：OpenAPI 3.0 (Swagger)
- **监控**：Actuator + Prometheus
- **容器化**：Docker

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

### 1. 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 2. 配置数据库
```sql
CREATE DATABASE qianshe_file_storage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 启动服务
```bash
# 开发环境
mvn spring-boot:run

# Docker环境
docker-compose up -d file-storage
```

### 4. 访问服务
- **API文档**：http://localhost:8092/swagger-ui.html
- **健康检查**：http://localhost:8092/actuator/health
- **监控指标**：http://localhost:8092/actuator/prometheus

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

## 📝 版本历史

### v1.0.0 (2024-12-28)
- ✅ 基础文件上传下载功能
- ✅ 权限控制系统
- ✅ 本地存储支持
- ✅ 审计日志记录
- ✅ Docker容器化部署
- ✅ API文档和监控

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码变更
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

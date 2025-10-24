# 水行政执法考试系统 - 后端

## 项目概述

基于 Spring Boot 3.5.6 + MyBatis + MySQL + Redis

## 技术栈

- Java 17
- Spring Boot 3.5.6
- Spring Security + JWT
- MyBatis 3.0.5
- MySQL 8.0
- Redis
- Maven

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 数据库准备

创建数据库并导入初始化脚本：

### 2. 配置文件

修改 `src/main/resources/application.yml`：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/water_law_exam
    username: root
    password: 123456

# Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: 
```

### 3. 启动项目

```bash
# 清理编译
mvn clean install

# 运行
mvn spring-boot:run
```

服务启动在：`http://localhost:8080`

## API 接口

### 基础路径

所有接口统一前缀：`/api`

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## JWT 认证

### 请求头格式

```
Authorization: Bearer <token>
```

### Token 有效期

默认 2 小时（7200000 毫秒），可在 `application.yml` 中修改。

## 开发说明

### 添加新接口步骤

1. 创建 Entity（实体类）
2. 创建 Mapper 接口和 XML
3. 创建 Request/Response DTO
4. 创建 Service 实现业务逻辑
5. 创建 Controller 定义接口

### 权限控制

使用 Spring Security 注解：

```java
@PreAuthorize("hasRole('ADMIN')")  // 需要ADMIN角色
@PreAuthorize("hasRole('USER')")   // 需要USER角色
```

### 全局异常处理

业务异常使用 `BusinessException`：

```java
throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
```

### Redis 连接失败

检查：
- Redis 服务是否启动
- `application.yml` 中的 Redis 配置是否正确

### JWT Token 无效

检查：
- Token 是否过期
- 请求头格式是否正确：`Authorization: Bearer <token>`

## 许可证

Private


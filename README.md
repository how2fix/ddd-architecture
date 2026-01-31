# 用户注册系统 - COLA架构示例

基于COLA（Clean Object-Oriented and Layered Architecture）架构的用户注册示例项目。

## 项目结构

```
user-archetype/
├── user-client/          # Client层 - 对外API契约（DTO、Command、Query）
├── user-adapter/         # Adapter层 - 协议适配（Controller）
├── user-app/             # Application层 - 业务编排（Executor、Query）
├── user-domain/          # Domain层 - 核心业务逻辑（Entity、Gateway接口）
├── user-infrastructure/  # Infrastructure层 - 技术实现（Repository、Gateway实现）
└── user-start/           # Start模块 - 启动入口
```

## 快速开始

### 1. 编译项目

```bash
mvn clean install -DskipTests
```

### 2. 启动应用

```bash
cd user-start
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

### 3. 测试API

#### 用户注册

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "phone": "13800138000",
    "sendEmail": true,
    "sendSms": true
  }'
```

响应：
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": "1"
}
```

#### 查询用户

```bash
curl http://localhost:8080/api/users/1
```

响应：
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "status": "INACTIVE",
    "registerTime": "2024-01-01T10:00:00",
    "lastActiveTime": "2024-01-01T10:00:00"
  }
}
```

## 架构说明

### Client层（对外契约）
- `UserServiceI` - 用户服务接口
- `UserDTO` - 用户数据传输对象
- `UserRegisterCmd` - 注册命令（写操作）
- `UserByIdQry` - 查询对象（读操作）
- `UserStatus` - 用户状态枚举
- `ErrorCode` - 错误码定义

### Adapter层（适配层）
- `UserController` - HTTP接口适配
- 只做协议转换和参数校验，不包含业务逻辑

### Application层（应用层）
- `UserApplicationService` - 应用服务门面
- `UserRegisterCmdExe` - 注册命令执行器
- `UserByIdQryExe` - 查询执行器
- `UserAssembler` - DTO与领域对象转换

### Domain层（领域层 - 核心）
- `User` - 用户聚合根（充血模型）
- `Email` - 邮箱值对象
- `Phone` - 手机号值对象
- `UserDomainService` - 用户领域服务
- `IUserRepository` - 用户仓储接口
- `IEmailGateway` - 邮件网关接口（防腐层）
- `ISmsGateway` - 短信网关接口（防腐层）

### Infrastructure层（基础设施层）
- `UserRepositoryImpl` - 用户仓储实现
- `EmailGatewayImpl` - 邮件网关实现（模拟）
- `SmsGatewayImpl` - 短信网关实现（模拟）
- `UserMapper` - MyBatis Plus Mapper
- `UserPO` - 持久化对象
- `UserConverter` - 领域对象与PO转换

## 请求流程

```
HTTP请求 → UserController (Adapter层)
         ↓
         UserApplicationService (Application层)
         ↓
         UserRegisterCmdExe (Application层)
         ↓
         User.register() + UserDomainService (Domain层)
         ↓
         UserRepository + IEmailGateway + ISmsGateway (Domain接口)
         ↓
         UserRepositoryImpl + EmailGatewayImpl + SmsGatewayImpl (Infrastructure实现)
```

## 设计特点

1. **依赖倒置** - Domain层定义接口，Infrastructure层实现
2. **CQRS** - Command和Query分离
3. **充血模型** - 业务逻辑在领域对象内
4. **防腐层** - Gateway接口隔离第三方依赖
5. **事务边界** - 在Application层控制

## H2数据库控制台

访问 `http://localhost:8080/h2-console` 查看数据库
- JDBC URL: `jdbc:h2:mem:user_db`
- 用户名: `sa`
- 密码: (空)

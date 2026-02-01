# COLA架构功能实现指南 v2.0

本文档说明项目中已实现的COLA架构功能及其使用方法。

---

## 功能概览

| 功能 | 状态 | 描述 |
|------|------|------|
| 扩展点机制 | ✅ 已实现 | @Extension + ExtensionExecutor |
| 标准异常体系 | ✅ 已实现 | BizException/SysException |
| 增强Assembler | ✅ 已实现 | 统一DTO转换、数据脱敏 |
| CQRS模式 | ✅ 已实现 | Command/Query分离 |
| 依赖倒置 | ✅ 已实现 | Domain定义接口，Infrastructure实现 |

---

## 1. 扩展点机制 (@Extension)

### 1.1 核心概念

扩展点机制用于支持多租户/多业务线的差异化处理，避免代码中出现大量if-else。

```
业务场景 = bizId + useCase + scenario

例如：
- DEFAULT.register      → 默认注册流程
- VIP.register          → VIP用户注册流程
- VIP.register.email    → VIP用户邮箱注册流程
```

### 1.2 核心组件

#### ExtensionPointI - 扩展点标记接口

**文件**: `user-domain/extension/ExtensionPointI.java`

```java
public interface ExtensionPointI {
    // 标记接口，所有扩展点必须继承此接口
}
```

#### @Extension - 扩展点注解

**文件**: `user-domain/extension/Extension.java`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    String bizId() default "";           // 业务身份
    String useCase() default "";         // 用例
    String scenario() default "";        // 场景
    String description() default "";     // 描述
}
```

#### BizScenario - 业务场景

**文件**: `user-domain/extension/BizScenario.java`

```java
public class BizScenario {
    private String bizId;
    private String useCase;
    private String scenario;

    public static BizScenario valueOf(String bizId) { ... }
    public static BizScenario valueOf(String bizId, String useCase) { ... }
    public static BizScenario valueOf(String bizId, String useCase, String scenario) { ... }
}
```

#### ExtensionExecutor - 扩展点执行器

**文件**: `user-domain/extension/ExtensionExecutor.java`

```java
@Component
public class ExtensionExecutor {

    // 有返回值的执行
    public <T extends ExtensionPointI, R> R execute(
        Class<T> extensionPoint,
        BizScenario bizScenario,
        ExtensionInvoker<T, R> executor
    ) { ... }

    // 无返回值的执行
    public <T extends ExtensionPointI> void executeVoid(
        Class<T> extensionPoint,
        BizScenario bizScenario,
        ExtensionInvokerVoid<T> executor
    ) { ... }
}
```

### 1.3 定义扩展点

```java
// 继承 ExtensionPointI
public interface UserValidatorExtPt extends ExtensionPointI {
    void validate(User user, BizScenario bizScenario);
}
```

### 1.4 实现扩展点

```java
// 默认实现
@Extension(bizId = "DEFAULT")
public class DefaultUserValidatorExt implements UserValidatorExtPt {
    @Override
    public void validate(User user, BizScenario bizScenario) {
        // 默认校验规则
    }
}

// VIP用户实现
@Extension(bizId = "VIP", description = "VIP用户校验规则")
public class VipUserValidatorExt implements UserValidatorExtPt {
    @Override
    public void validate(User user, BizScenario bizScenario) {
        // VIP用户更严格的校验规则
        if (user.getUsername() == null || user.getUsername().length() < 5) {
            throw new BizException("10010", "VIP用户名至少5个字符");
        }
        if (user.getPhone() == null) {
            throw new BizException("10011", "VIP用户必须绑定手机号");
        }
        // VIP用户必须使用企业邮箱
        String email = user.getEmail().getValue();
        if (!email.matches(".*@(company|corp|enterprise)\\.com$")) {
            throw new BizException("10012", "VIP用户必须使用企业邮箱注册");
        }
    }
}
```

### 1.5 使用扩展点

```java
@Service
@RequiredArgsConstructor
public class UserRegisterCmdExeWithExtension {

    private final ExtensionExecutor extensionExecutor;
    private final IUserRepository userRepository;
    private final IEmailGateway emailGateway;

    @Transactional(rollbackFor = Exception.class)
    public Response execute(UserRegisterCmd cmd, BizScenario bizScenario) {

        // 1. 使用扩展点进行校验
        extensionExecutor.executeVoid(
            UserValidatorExtPt.class,
            bizScenario,
            ext -> ext.validate(user, bizScenario)
        );

        // 2. 使用扩展点计算注册奖金
        BigDecimal bonus = extensionExecutor.execute(
            UserRegisterBonusExtPt.class,
            bizScenario,
            ext -> ext.calculateBonus(userId, bizScenario)
        );

        // 继续处理...
        return Response.buildSuccess(userId);
    }
}
```

### 1.6 已实现的扩展点

| 扩展点接口 | 实现数量 | 业务场景 |
|-----------|---------|---------|
| `UserValidatorExtPt` | 2 | DEFAULT, VIP |
| `UserRegisterBonusExtPt` | 2 | DEFAULT, VIP |
| `LevelUpgradeExtPt` | 3 | NORMAL, VIP, ENTERPRISE |
| `RiskCheckExtPt` | 5 | login, register, payment, withdraw(VIP) |
| `PointCalculateExtPt` | 5 | purchase, purchase(VIP), checkin, continuous, comment |
| `NotificationChannelExtPt` | 3 | DEFAULT, VIP, ENTERPRISE |
| `OrderDiscountExtPt` | 4 | NEW_USER, DEFAULT, VIP, ENTERPRISE |
| `RegisterValidatorExtPt` | 4 | email, phone, enterprise, wechat |

---

## 2. 标准异常体系

### 2.1 异常类型

```java
// 业务异常 - 可预期，需友好提示
BizException

// 系统异常 - 不可预期，需告警
SysException
```

### 2.2 错误码定义

**文件**: `user-client/exception/ErrorCode.java`

```java
public enum ErrorCode {
    // 参数校验错误
    PARAM_INVALID("PARAM_INVALID", "参数不合法"),
    EMAIL_FORMAT_ERROR("EMAIL_FORMAT_ERROR", "邮箱格式不正确"),
    PHONE_FORMAT_ERROR("PHONE_FORMAT_ERROR", "手机号格式不正确"),

    // 业务错误
    EMAIL_ALREADY_EXISTS("EMAIL_EXISTS", "邮箱已被注册"),
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),

    // 系统错误
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误"),
    DB_ERROR("DB_ERROR", "数据库异常");
}
```

### 2.3 使用方式

```java
// 方式1：通过ErrorCode创建
throw BizException.of(ErrorCode.EMAIL_ALREADY_EXISTS);

// 方式2：直接创建
throw BizException.of("10003", "邮箱已被注册");

// 方式3：系统异常（带原始异常）
throw SysException.wrap("50002", cause);
```

### 2.4 全局异常处理

**文件**: `user-adapter/exception/GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException e) {
        return Response.buildFailure(e.getErrCode(), e.getErrMsg());
    }

    @ExceptionHandler(SysException.class)
    public Response handleSysException(SysException e) {
        return Response.buildFailure(e.getErrCode(), "系统异常，请稍后重试");
    }
}
```

---

## 3. 增强Assembler模式

### 3.1 核心方法

**文件**: `user-app/assembler/UserAssembler.java`

```java
@Component
public class UserAssembler {

    // 单个对象转换（自动脱敏）
    public UserDTO toDTO(User user) { ... }

    // 单个对象转换（完整信息，不脱敏）
    public UserDTO toFullDTO(User user) { ... }

    // 列表转换
    public List<UserDTO> toDTOList(List<User> users) { ... }

    // 命令转领域对象
    public User toDomain(UserRegisterCmd cmd) { ... }

    // 数据脱敏
    public String getMaskedPhone(String phone) { ... }
    public String getMaskedEmail(String email) { ... }
}
```

### 3.2 数据脱敏规则

- **手机号**: `13800138000` → `138****8000`
- **邮箱**: `test@example.com` → `t***@example.com`

---

## 4. CQRS模式

### 4.1 命令 (Command)

**文件**: `user-client/dto/cmd/UserRegisterCmd.java`

```java
public class UserRegisterCmd extends Command {
    private String username;
    private String email;
    private String password;
    private String phone;
    private Boolean sendEmail;
    private Boolean sendSms;
}
```

### 4.2 查询 (Query)

**文件**: `user-client/dto/query/UserByIdQry.java`

```java
public class UserByIdQry extends Query {
    private Long userId;
}
```

### 4.3 命令执行器

**文件**: `user-app/executor/UserRegisterCmdExe.java`

```java
@Component
public class UserRegisterCmdExe {
    @Transactional(rollbackFor = Exception.class)
    public Response execute(UserRegisterCmd cmd) {
        // 写操作逻辑
    }
}
```

### 4.4 查询执行器

**文件**: `user-app/executor/UserByIdQryExe.java`

```java
@Component
public class UserByIdQryExe {
    public Response execute(UserByIdQry qry) {
        // 读操作逻辑（无事务）
    }
}
```

---

## 5. 依赖倒置原则 (DIP)

### 5.1 Domain层定义接口

**文件**: `user-domain/gateway/IUserRepository.java`

```java
public interface IUserRepository {
    User save(User user);
    User findById(Long id);
    User findByEmail(String email);
    User findByPhone(String phone);
}
```

### 5.2 Infrastructure层实现接口

**文件**: `user-infrastructure/gatewayimpl/UserRepositoryImpl.java`

```java
@Repository
public class UserRepositoryImpl implements IUserRepository {
    // 实现接口方法
}
```

### 5.3 Application层依赖接口

```java
@Component
public class UserRegisterCmdExe {
    private final IUserRepository userRepository;  // 依赖接口，不依赖实现
}
```

---

## 6. 文件清单

### 6.1 扩展点机制

| 文件路径 | 描述 |
|---------|------|
| `user-domain/extension/ExtensionPointI.java` | 扩展点标记接口 |
| `user-domain/extension/Extension.java` | @Extension注解 |
| `user-domain/extension/BizScenario.java` | 业务场景 |
| `user-domain/extension/ExtensionExecutor.java` | 扩展点执行器 |
| `user-domain/extension/ApplicationContextHelper.java` | Spring工具类 |

### 6.2 扩展点接口 (8个)

| 文件 | 描述 |
|------|------|
| `user-domain/extension/UserValidatorExtPt.java` | 用户校验扩展点 |
| `user-domain/extension/UserRegisterBonusExtPt.java` | 注册奖金扩展点 |
| `user-domain/extension/LevelUpgradeExtPt.java` | 等级升级扩展点 |
| `user-domain/extension/RiskCheckExtPt.java` | 风控校验扩展点 |
| `user-domain/extension/PointCalculateExtPt.java` | 积分计算扩展点 |
| `user-domain/extension/NotificationChannelExtPt.java` | 通知渠道扩展点 |
| `user-domain/extension/OrderDiscountExtPt.java` | 订单优惠扩展点 |
| `user-domain/extension/RegisterValidatorExtPt.java` | 注册校验扩展点 |

### 6.3 扩展点实现 (27个)

**目录**: `user-domain/extension/impl/`

```
impl/
├── DefaultUserValidatorExt.java
├── VipUserValidatorExt.java
├── DefaultUserRegisterBonusExt.java
├── VipUserRegisterBonusExt.java
├── NormalUserLevelUpgradeExt.java
├── VipUserLevelUpgradeExt.java
├── EnterpriseUserLevelUpgradeExt.java
├── LoginRiskCheckExt.java
├── RegisterRiskCheckExt.java
├── PaymentRiskCheckExt.java
├── WithdrawRiskCheckExt.java
├── VipWithdrawRiskCheckExt.java
├── PurchasePointExt.java
├── VipPurchasePointExt.java
├── CheckinPointExt.java
├── ContinuousCheckinPointExt.java
├── CommentPointExt.java
├── DefaultNotificationChannelExt.java
├── VipNotificationChannelExt.java
├── EnterpriseNotificationChannelExt.java
├── NewUserOrderDiscountExt.java
├── DefaultOrderDiscountExt.java
├── VipOrderDiscountExt.java
├── EnterpriseOrderDiscountExt.java
├── EmailRegisterValidatorExt.java
├── PhoneRegisterValidatorExt.java
├── EnterpriseRegisterValidatorExt.java
└── WechatRegisterValidatorExt.java
```

### 6.4 异常体系

| 文件路径 | 描述 |
|---------|------|
| `user-client/exception/BaseException.java` | 异常基类 |
| `user-client/exception/BizException.java` | 业务异常 |
| `user-client/exception/SysException.java` | 系统异常 |
| `user-client/exception/ErrorCode.java` | 错误码枚举 |
| `user-adapter/exception/GlobalExceptionHandler.java` | 全局异常处理器 |

---

## 7. 测试API

### 7.1 普通用户注册

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

### 7.2 VIP用户注册（需要企业邮箱）

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vipuser",
    "email": "vipuser@company.com",
    "password": "123456",
    "phone": "13900139000",
    "sendEmail": true,
    "sendSms": true
  }'
```

注意：VIP用户需要使用企业邮箱（@company.com, @corp.com, @enterprise.com）注册。

---

## 8. 扩展点最佳实践

### 8.1 何时使用扩展点

✅ **适合使用扩展点的场景**：
- 多租户系统
- 多业务线共用一套代码
- 不同客户有不同业务规则
- 需要灵活配置的业务逻辑

❌ **不适合使用扩展点的场景**：
- 单一业务场景
- 简单的if-else判断
- 不会变化的固定规则

### 8.2 扩展点命名规范

```
{业务对象}{操作}ExtPt

例如：
- UserValidatorExtPt       // 用户校验扩展点
- UserRegisterBonusExtPt   // 用户注册奖金扩展点
- OrderCalculateExtPt      // 订单计算扩展点
```

### 8.3 业务场景设计

```
粗粒度 → 细粒度

DEFAULT           → 默认实现（兜底）
VIP               → VIP用户
VIP.register      → VIP用户注册场景
VIP.register.email → VIP用户邮箱注册场景
```

### 8.4 扩展点找不到会怎样？

会抛出`SysException`，错误码为`EXTENSION_NOT_FOUND`。建议总是提供DEFAULT实现作为兜底。

### 8.5 如何调试扩展点？

开启DEBUG日志：

```yaml
logging:
  level:
    com.example.user.domain.extension: DEBUG
```

日志输出：

```
DEBUG - 执行扩展点: interface=UserValidatorExtPt, scenario=VIP.register, impl=VipUserValidatorExt
```

---

## 9. 技术栈

| 技术 | 版本 | 用途 |
|-----|------|-----|
| Java | 17 | 开发语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| H2 Database | 2.2.224 | 开发数据库 |
| Lombok | 1.18.30 | 减少样板代码 |
| Jakarta Validation | 3.0.2 | 参数校验 |

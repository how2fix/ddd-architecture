# COLA扩展点业务场景示例

本文档展示如何在实际业务中使用已实现的扩展点机制。

---

## 扩展点清单

| 序号 | 扩展点接口 | 文件路径 | 实现数量 |
|------|----------|---------|---------|
| 1 | `UserValidatorExtPt` | `user-domain/extension/UserValidatorExtPt.java` | 2 (DEFAULT, VIP) |
| 2 | `UserRegisterBonusExtPt` | `user-domain/extension/UserRegisterBonusExtPt.java` | 2 (DEFAULT, VIP) |
| 3 | `LevelUpgradeExtPt` | `user-domain/extension/LevelUpgradeExtPt.java` | 3 (NORMAL, VIP, ENTERPRISE) |
| 4 | `RiskCheckExtPt` | `user-domain/extension/RiskCheckExtPt.java` | 5 (login, register, payment, withdraw×2) |
| 5 | `PointCalculateExtPt` | `user-domain/extension/PointCalculateExtPt.java` | 5 (purchase×2, checkin, continuous, comment) |
| 6 | `NotificationChannelExtPt` | `user-domain/extension/NotificationChannelExtPt.java` | 3 (DEFAULT, VIP, ENTERPRISE) |
| 7 | `OrderDiscountExtPt` | `user-domain/extension/OrderDiscountExtPt.java` | 4 (NEW_USER, DEFAULT, VIP, ENTERPRISE) |
| 8 | `RegisterValidatorExtPt` | `user-domain/extension/RegisterValidatorExtPt.java` | 4 (email, phone, enterprise, wechat) |

---

## 扩展点机制核心组件

### 1. 扩展点执行器 (ExtensionExecutor)

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

### 2. 业务场景 (BizScenario)

**文件**: `user-domain/extension/BizScenario.java`

```java
// 场景格式: bizId.useCase.scenario
BizScenario.valueOf("DEFAULT")                    // 默认
BizScenario.valueOf("VIP", "register")            // VIP注册
BizScenario.valueOf("VIP", "register", "email")   // VIP邮箱注册
```

### 3. 扩展点注解 (@Extension)

**文件**: `user-domain/extension/Extension.java`

```java
@Extension(bizId = "VIP", useCase = "register", description = "VIP用户注册")
public class VipUserValidatorExt implements UserValidatorExtPt { ... }
```

---

## 场景1: 用户校验 (UserValidatorExtPt)

### 扩展点定义

**文件**: `user-domain/extension/UserValidatorExtPt.java`

```java
public interface UserValidatorExtPt extends ExtensionPointI {
    void validate(User user, BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | bizId | 说明 |
|-------|------|-------|------|
| `DefaultUserValidatorExt` | `impl/DefaultUserValidatorExt.java` | DEFAULT | 默认用户校验 |
| `VipUserValidatorExt` | `impl/VipUserValidatorExt.java` | VIP | VIP用户校验（更严格） |

### VIP用户校验规则

**文件**: `user-domain/extension/impl/VipUserValidatorExt.java`

```java
@Extension(bizId = "VIP", description = "VIP用户校验规则")
public class VipUserValidatorExt implements UserValidatorExtPt {
    @Override
    public void validate(User user, BizScenario bizScenario) {
        // VIP用户有更严格的校验规则
        if (user.getUsername() == null || user.getUsername().length() < 5) {
            throw new BizException("10010", "VIP用户名至少5个字符");
        }
        // VIP用户必须有手机号
        if (user.getPhone() == null) {
            throw new BizException("10011", "VIP用户必须绑定手机号");
        }
        // VIP用户额外校验：邮箱必须是企业邮箱
        String email = user.getEmail().getValue();
        if (!email.matches(".*@(company|corp|enterprise)\\.com$")) {
            throw new BizException("10012", "VIP用户必须使用企业邮箱注册");
        }
    }
}
```

### 使用示例

```java
@Autowired
private ExtensionExecutor extensionExecutor;

public void validateUser(User user, String userType) {
    BizScenario scenario = BizScenario.valueOf(userType, "register");

    extensionExecutor.executeVoid(
        UserValidatorExtPt.class,
        scenario,
        ext -> ext.validate(user, scenario)
    );
}
```

---

## 场景2: 用户等级升级 (LevelUpgradeExtPt)

### 扩展点定义

**文件**: `user-domain/extension/LevelUpgradeExtPt.java`

```java
public interface LevelUpgradeExtPt extends ExtensionPointI {
    LevelUpgradeResult checkUpgrade(Long userId, String currentLevel,
                                    BigDecimal totalConsumption, Long totalPoints,
                                    BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | bizId | 升级条件 |
|-------|------|-------|---------|
| `NormalUserLevelUpgradeExt` | `impl/NormalUserLevelUpgradeExt.java` | NORMAL | 消费1000元升级VIP |
| `VipUserLevelUpgradeExt` | `impl/VipUserLevelUpgradeExt.java` | VIP | 消费5000元升级SVIP |
| `EnterpriseUserLevelUpgradeExt` | `impl/EnterpriseUserLevelUpgradeExt.java` | ENTERPRISE | 需要人工审核 |

### 测试用例

```java
// 普通用户 - 消费1000元升级VIP
LevelUpgradeResult result = extensionExecutor.execute(
    LevelUpgradeExtPt.class,
    BizScenario.valueOf("NORMAL"),
    ext -> ext.checkUpgrade(1L, "NORMAL", new BigDecimal("1000"), 0L, scenario)
);
// 结果: canUpgrade=true, targetLevel="VIP"

// VIP用户 - 消费5000元升级SVIP
LevelUpgradeResult result = extensionExecutor.execute(
    LevelUpgradeExtPt.class,
    BizScenario.valueOf("VIP"),
    ext -> ext.checkUpgrade(1L, "VIP", new BigDecimal("5000"), 0L, scenario)
);
// 结果: canUpgrade=true, targetLevel="SVIP"

// 企业用户 - 需要人工审核
LevelUpgradeResult result = extensionExecutor.execute(
    LevelUpgradeExtPt.class,
    BizScenario.valueOf("ENTERPRISE"),
    ext -> ext.checkUpgrade(1L, "ENTERPRISE", new BigDecimal("10000"), 0L, scenario)
);
// 结果: canUpgrade=false, reason="企业用户升级需要人工审核"
```

---

## 场景3: 风控校验 (RiskCheckExtPt)

### 扩展点定义

**文件**: `user-domain/extension/RiskCheckExtPt.java`

```java
public interface RiskCheckExtPt extends ExtensionPointI {
    RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context);
}
```

### 实现列表

| 实现类 | 文件 | 场景 | 风控规则 |
|-------|------|------|---------|
| `LoginRiskCheckExt` | `impl/LoginRiskCheckExt.java` | login | 密码错误次数、异地登录 |
| `RegisterRiskCheckExt` | `impl/RegisterRiskCheckExt.java` | register | IP限制、设备限制 |
| `PaymentRiskCheckExt` | `impl/PaymentRiskCheckExt.java` | payment | 大额支付验证 |
| `WithdrawRiskCheckExt` | `impl/WithdrawRiskCheckExt.java` | withdraw | 单笔限额、日限额 |
| `VipWithdrawRiskCheckExt` | `impl/VipWithdrawRiskCheckExt.java` | withdraw(VIP) | VIP更高限额 |

### 使用示例

```java
// 登录场景 - 密码错误5次
RiskContext context = RiskContext.forLogin("192.168.1.100", "device_001");
RiskCheckResult result = extensionExecutor.execute(
    RiskCheckExtPt.class,
    BizScenario.valueOf("DEFAULT", "login"),
    ext -> ext.check(1L, null, context)
);
// 结果: pass=false, action=BLOCK, message="密码错误次数过多，账户已锁定30分钟"

// 支付场景 - 大额支付
context = RiskContext.forPayment("192.168.1.100", "device_001", new BigDecimal("15000"));
result = extensionExecutor.execute(
    RiskCheckExtPt.class,
    BizScenario.valueOf("DEFAULT", "payment"),
    ext -> ext.check(1L, new BigDecimal("15000"), context)
);
// 结果: pass=false, action=CHALLENGE, message="大额支付需要二次验证"

// VIP提现 - 更高额度
BizScenario vipScenario = BizScenario.valueOf("VIP", "withdraw");
result = extensionExecutor.execute(
    RiskCheckExtPt.class,
    vipScenario,
    ext -> ext.check(1L, new BigDecimal("30000"), context)
);
// VIP用户可提现30000元（普通用户只能5000元）
```

---

## 场景4: 积分计算 (PointCalculateExtPt)

### 扩展点定义

**文件**: `user-domain/extension/PointCalculateExtPt.java`

```java
public interface PointCalculateExtPt extends ExtensionPointI {
    Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | 场景 | 积分规则 |
|-------|------|------|---------|
| `PurchasePointExt` | `impl/PurchasePointExt.java` | purchase | 消费1元=1积分 |
| `VipPurchasePointExt` | `impl/VipPurchasePointExt.java` | purchase(VIP) | 消费1元=2积分（双倍） |
| `CheckinPointExt` | `impl/CheckinPointExt.java` | daily_checkin | 每日签到10积分 |
| `ContinuousCheckinPointExt` | `impl/ContinuousCheckinPointExt.java` | continuous_checkin | 连续签到额外奖励 |
| `CommentPointExt` | `impl/CommentPointExt.java` | comment | 发表评论5积分 |

### 使用示例

```java
// 普通用户购物 - 100元 = 100积分
Long points = extensionExecutor.execute(
    PointCalculateExtPt.class,
    BizScenario.valueOf("DEFAULT", "purchase"),
    ext -> ext.calculate(1L, new BigDecimal("100"), scenario)
);
// 结果: 100

// VIP用户购物 - 100元 = 200积分（双倍）
points = extensionExecutor.execute(
    PointCalculateExtPt.class,
    BizScenario.valueOf("VIP", "purchase"),
    ext -> ext.calculate(1L, new BigDecimal("100"), scenario)
);
// 结果: 200

// 每日签到
points = extensionExecutor.execute(
    PointCalculateExtPt.class,
    BizScenario.valueOf("DEFAULT", "daily_checkin"),
    ext -> ext.calculate(1L, null, scenario)
);
// 结果: 10

// 连续签到7天
points = extensionExecutor.execute(
    PointCalculateExtPt.class,
    BizScenario.valueOf("DEFAULT", "continuous_checkin"),
    ext -> ext.calculate(1L, new BigDecimal("7"), scenario)
);
// 结果: 50（连续7天额外奖励）
```

---

## 场景5: 通知渠道选择 (NotificationChannelExtPt)

### 扩展点定义

**文件**: `user-domain/extension/NotificationChannelExtPt.java`

```java
public interface NotificationChannelExtPt extends ExtensionPointI {
    List<ChannelType> getChannels(NotificationType type, Long userId, BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | 通知类型 |
|-------|------|---------|
| `DefaultNotificationChannelExt` | `impl/DefaultNotificationChannelExt.java` | 站内信 |
| `VipNotificationChannelExt` | `impl/VipNotificationChannelExt.java` | 邮件+站内信 |
| `EnterpriseNotificationChannelExt` | `impl/EnterpriseNotificationChannelExt.java` | 电话+短信+邮件 |

### 使用示例

```java
// 普通用户 - 营销消息 → 站内信
List<ChannelType> channels = extensionExecutor.execute(
    NotificationChannelExtPt.class,
    BizScenario.valueOf("DEFAULT"),
    ext -> ext.getChannels(NotificationType.MARKETING, 1L, scenario)
);
// 结果: [IN_APP]

// VIP用户 - 交易提醒 → 短信+邮件
channels = extensionExecutor.execute(
    NotificationChannelExtPt.class,
    BizScenario.valueOf("VIP"),
    ext -> ext.getChannels(NotificationType.TRANSACTION, 1L, scenario)
);
// 结果: [SMS, EMAIL]

// 企业用户 - 安全告警 → 电话+短信
channels = extensionExecutor.execute(
    NotificationChannelExtPt.class,
    BizScenario.valueOf("ENTERPRISE"),
    ext -> ext.getChannels(NotificationType.SECURITY, 1L, scenario)
);
// 结果: [PHONE, SMS]
```

---

## 场景6: 订单优惠 (OrderDiscountExtPt)

### 扩展点定义

**文件**: `user-domain/extension/OrderDiscountExtPt.java`

```java
public interface OrderDiscountExtPt extends ExtensionPointI {
    DiscountResult calculateDiscount(Long userId, BigDecimal amount,
                                      String productId, BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | 优惠规则 |
|-------|------|---------|
| `NewUserOrderDiscountExt` | `impl/NewUserOrderDiscountExt.java` | 首单9折 |
| `DefaultOrderDiscountExt` | `impl/DefaultOrderDiscountExt.java` | 满100减10 |
| `VipOrderDiscountExt` | `impl/VipOrderDiscountExt.java` | 8折+免运费 |
| `EnterpriseOrderDiscountExt` | `impl/EnterpriseOrderDiscountExt.java` | 7折+月结 |

### 使用示例

```java
// 新用户 - 首单9折
DiscountResult result = extensionExecutor.execute(
    OrderDiscountExtPt.class,
    BizScenario.valueOf("NEW_USER"),
    ext -> ext.calculateDiscount(1L, new BigDecimal("100"), "P001", scenario)
);
// 结果: discountAmount=10, description="新用户首单9折优惠"

// VIP用户 - 8折+免运费
result = extensionExecutor.execute(
    OrderDiscountExtPt.class,
    BizScenario.valueOf("VIP"),
    ext -> ext.calculateDiscount(1L, new BigDecimal("100"), "P001", scenario)
);
// 结果: discountAmount=30, description="VIP会员8折优惠+免运费" (20元折扣+10元运费)

// 企业用户 - 7折
result = extensionExecutor.execute(
    OrderDiscountExtPt.class,
    BizScenario.valueOf("ENTERPRISE"),
    ext -> ext.calculateDiscount(1L, new BigDecimal("1000"), "P001", scenario)
);
// 结果: discountAmount=300, description="企业用户7折优惠，支持月结"
```

---

## 场景7: 注册多场景校验 (RegisterValidatorExtPt)

### 扩展点定义

**文件**: `user-domain/extension/RegisterValidatorExtPt.java`

```java
public interface RegisterValidatorExtPt extends ExtensionPointI {
    void validate(UserRegisterCmd cmd, BizScenario bizScenario);
}
```

### 实现列表

| 实现类 | 文件 | 注册方式 | 校验规则 |
|-------|------|---------|---------|
| `EmailRegisterValidatorExt` | `impl/EmailRegisterValidatorExt.java` | email | 邮箱格式+验证码 |
| `PhoneRegisterValidatorExt` | `impl/PhoneRegisterValidatorExt.java` | phone | 手机号格式+短信验证码+运营商 |
| `EnterpriseRegisterValidatorExt` | `impl/EnterpriseRegisterValidatorExt.java` | email(企业) | 企业邮箱+企业名称+营业执照 |
| `WechatRegisterValidatorExt` | `impl/WechatRegisterValidatorExt.java` | wechat | 微信授权+openid检查 |

### 使用示例

```java
// 邮箱注册
UserRegisterCmd cmd = new UserRegisterCmd();
cmd.setEmail("user@example.com");
cmd.setVerifyCode("123456");

extensionExecutor.executeVoid(
    RegisterValidatorExtPt.class,
    BizScenario.valueOf("DEFAULT", "register", "email"),
    ext -> ext.validate(cmd, scenario)
);

// 企业用户注册
cmd.setEmail("admin@company.com");
cmd.setCompanyName("某某科技有限公司");
cmd.setBusinessLicense("license.jpg");
cmd.setCreditCode("91110000MA001234XY");

extensionExecutor.executeVoid(
    RegisterValidatorExtPt.class,
    BizScenario.valueOf("ENTERPRISE", "register", "email"),
    ext -> ext.validate(cmd, scenario)
);
// 校验: 企业邮箱 + 企业名称 + 营业执照 + 统一社会信用代码
```

---

## 综合业务流程示例

### 用户下单流程（使用多个扩展点）

**文件**: `user-app/executor/UserRegisterCmdExeWithExtension.java`

```java
@Service
@RequiredArgsConstructor
public class OrderProcessService {

    private final ExtensionExecutor extensionExecutor;

    /**
     * 用户下单（综合使用多个扩展点）
     */
    public Response placeOrder(Long userId, BigDecimal amount,
                               String productId, String userType) {

        BizScenario scenario = BizScenario.valueOf(userType);

        // 1. 风控检查
        RiskContext context = RiskContext.forPayment("192.168.1.100", "device_001");
        RiskCheckResult riskResult = extensionExecutor.execute(
            RiskCheckExtPt.class,
            BizScenario.valueOf(userType, "payment"),
            ext -> ext.check(userId, amount, context)
        );

        if (!riskResult.isPass()) {
            return Response.buildFailure("RISK_CHECK", riskResult.getMessage());
        }

        // 2. 计算优惠
        DiscountResult discount = extensionExecutor.execute(
            OrderDiscountExtPt.class,
            scenario,
            ext -> ext.calculateDiscount(userId, amount, productId, scenario)
        );

        BigDecimal finalAmount = amount.subtract(discount.calculateFinalDiscount(amount));

        // 3. 创建订单...
        String orderId = createOrder(userId, productId, finalAmount, discount);

        // 4. 计算积分
        Long points = extensionExecutor.execute(
            PointCalculateExtPt.class,
            BizScenario.valueOf(userType, "purchase"),
            ext -> ext.calculate(userId, amount, scenario)
        );
        grantPoints(userId, points);

        // 5. 发送通知
        extensionExecutor.executeVoid(
            NotificationChannelExtPt.class,
            scenario,
            ext -> {
                List<ChannelType> channels = ext.getChannels(
                    NotificationChannelExtPt.NotificationType.TRANSACTION,
                    userId,
                    scenario
                );
                sendNotification(channels, "订单创建成功");
            }
        );

        return Response.buildSuccess(orderId);
    }
}
```

---

## 扩展点调试

### 开启DEBUG日志

```yaml
logging:
  level:
    com.example.user.domain.extension: DEBUG
```

### 日志输出示例

```
DEBUG - 执行扩展点: interface=UserValidatorExtPt, scenario=VIP.register, impl=VipUserValidatorExt
DEBUG - 执行VIP用户校验: username=vipuser
DEBUG - 计算VIP用户订单优惠: userId=1, amount=1000
DEBUG - 购物积分计算(VIP): userId=1, amount=1000, points=2000
DEBUG - 扩展点选择: interface=NotificationChannelExtPt, scenario=VIP, 渠道=[SMS, EMAIL]
```

---

## 扩展点最佳实践

1. **总是提供DEFAULT实现** - 作为兜底，防止找不到扩展点时抛异常
2. **扩展点命名规范** - {业务对象}{操作}ExtPt，如UserRegisterBonusExtPt
3. **业务场景设计** - 从粗到细：DEFAULT → VIP → VIP.register → VIP.register.email
4. **结果对象封装** - 使用专门的Result对象返回复杂结果
5. **日志记录** - 在扩展点中记录关键日志，方便调试
6. **异常处理** - 扩展点中抛出BizException，由全局处理器统一处理

---

## 已实现文件清单

### 扩展点接口定义

| 文件 | 路径 |
|------|------|
| ExtensionPointI | `user-domain/extension/ExtensionPointI.java` |
| Extension | `user-domain/extension/Extension.java` |
| BizScenario | `user-domain/extension/BizScenario.java` |
| ExtensionExecutor | `user-domain/extension/ExtensionExecutor.java` |
| ApplicationContextHelper | `user-domain/extension/ApplicationContextHelper.java` |

### 扩展点接口 (8个)

| 接口 | 文件 |
|------|------|
| UserValidatorExtPt | `user-domain/extension/UserValidatorExtPt.java` |
| UserRegisterBonusExtPt | `user-domain/extension/UserRegisterBonusExtPt.java` |
| LevelUpgradeExtPt | `user-domain/extension/LevelUpgradeExtPt.java` |
| RiskCheckExtPt | `user-domain/extension/RiskCheckExtPt.java` |
| PointCalculateExtPt | `user-domain/extension/PointCalculateExtPt.java` |
| NotificationChannelExtPt | `user-domain/extension/NotificationChannelExtPt.java` |
| OrderDiscountExtPt | `user-domain/extension/OrderDiscountExtPt.java` |
| RegisterValidatorExtPt | `user-domain/extension/RegisterValidatorExtPt.java` |

### 扩展点实现 (27个)

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

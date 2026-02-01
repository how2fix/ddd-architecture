# JDK 21 特性使用说明

本文档说明项目中使用的 JDK 21 新特性。

---

## 1. Record 类 (Java 14+)

### 说明
Record 是一种不可变数据载体，自动生成构造器、getter、equals、hashCode、toString 方法。

### 使用位置
| 文件 | 说明 |
|------|------|
| `Response.java` | 统一响应对象，使用紧凑构造函数验证参数 |
| `LevelUpgradeResult.java` | 等级升级结果 |
| `RiskCheckResult.java` | 风控检查结果 |
| `DiscountResult.java` | 订单优惠结果 |

### 示例
```java
// 旧方式 (Lombok)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelUpgradeResult {
    private boolean canUpgrade;
    private String targetLevel;
    private String reason;
    private Long requiredAmount;
}

// JDK 21 Record 方式
public record LevelUpgradeResult(
    boolean canUpgrade,
    String targetLevel,
    String reason,
    Long requiredAmount
) {
    // 紧凑构造函数用于验证
    public LevelUpgradeResult {
        if (canUpgrade && targetLevel == null) {
            throw new IllegalArgumentException("...");
        }
    }
}
```

---

## 2. Sealed 类 (Java 15+)

### 说明
Sealed 类限制哪些类可以继承或实现，增强类型安全和可维护性。

### 使用位置
| 文件 | 说明 |
|------|------|
| `BaseException.java` | sealed 抽象类，只允许 BizException 和 SysException 继承 |

### 示例
```java
// 基类声明 sealed，并指定允许的子类
public sealed abstract class BaseException extends RuntimeException
        permits BizException, SysException {

    // 使用 Pattern matching for switch
    public String recoverableMessage() {
        return switch (this) {
            case BizException e -> "业务异常，可恢复: " + e.getErrMsg();
            case SysException e -> "系统异常，需要技术介入: " + e.getErrCode();
        };
    }
}

// 子类必须使用 final, sealed, 或 non-sealed 修饰
public final class BizException extends BaseException { }
public final class SysException extends BaseException { }
```

---

## 3. Text Blocks (Java 13+)

### 说明
Text Blocks 使用三引号 `"""` 包裹多行字符串，适合 SQL、JSON、HTML 等场景。

### 使用位置
| 文件 | 说明 |
|------|------|
| `UserSqlConstants.java` | SQL 常量定义 |

### 示例
```java
// 旧方式：字符串拼接
String sql = "SELECT u.id, u.username, u.email\n" +
             "FROM t_user u\n" +
             "WHERE u.id = :userId";

// JDK 21 Text Blocks
String sql = """
        SELECT u.id, u.username, u.email
        FROM t_user u
        WHERE u.id = :userId
        """;
```

---

## 4. Pattern Matching for switch (Java 16+)

### 说明
增强的 switch 表达式支持模式匹配和类型判断。

### 使用位置
| 文件 | 说明 |
|------|------|
| `BaseException.java` | 异常类型匹配 |
| `DiscountResult.java` | 枚举值匹配 |
| 所有扩展点实现 | switch 表达式 |

### 示例
```java
// Sealed 类的模式匹配
String message = switch (exception) {
    case BizException e -> "业务异常: " + e.getErrMsg();
    case SysException e -> "系统异常: " + e.getErrCode();
};

// 枚举匹配
BigDecimal discount = switch (discountType) {
    case PERCENT -> originalAmount.multiply(discountRate);
    case AMOUNT, SHIPPING, COUPON -> discountAmount != null ? discountAmount : BigDecimal.ZERO;
};
```

---

## 5. Switch 表达式 (Java 14+)

### 说明
switch 可以作为表达式使用，支持箭头语法和 yield 关键字。

### 使用位置
所有扩展点实现类

### 示例
```java
// 箭头语法 (单行返回)
long bonusPoints = switch (continuousDays) {
    case 3 -> 20L;
    case 7 -> 50L;
    case 15 -> 100L;
    case 30 -> 300L;
    default -> 0L;
};

// yield 语法 (多行返回)
return switch (notificationType) {
    case SECURITY -> {
        if (isNightTime()) {
            yield Arrays.asList(SMS);
        } else {
            yield Arrays.asList(SMS, EMAIL);
        }
    }
    default -> Arrays.asList(EMAIL);
};
```

---

## 6. Stream.toList() (Java 16+)

### 说明
直接将 Stream 收集为不可变 List，替代 `collect(Collectors.toList())`。

### 使用位置
| 文件 | 说明 |
|------|------|
| `ExtensionExecutor.java` | 扩展点列表收集 |

### 示例
```java
// 旧方式
.toList(Collectors.toList())

// JDK 21
.toList()
```

---

## 7. String.formatted() (Java 15+)

### 说明
简化字符串格式化。

### 使用位置
| 文件 | 说明 |
|------|------|
| `LevelUpgradeResult.java` | 字符串格式化 |

### 示例
```java
// 旧方式
String.format("还差%d元可升级到%s", requiredAmount, targetLevel)

// JDK 21
"还差%d元可升级到%s".formatted(requiredAmount, targetLevel)
```

---

## 8. 方法引用改进

### 使用位置
| 文件 | 说明 |
|------|------|
| `ExtensionExecutor.java` | 使用 `this::locateExtensions` 方法引用 |

### 示例
```java
// 旧方式
extensionCache.computeIfAbsent(extensionPoint, k -> locateExtensions(k))

// JDK 21 更简洁
extensionCache.computeIfAbsent(extensionPoint, this::locateExtensions)
```

---

## JDK 21 特性总结

| 特性 | 版本 | 替代方案 | 文件数量 |
|------|------|---------|---------|
| Record | 14+ | Lombok @Data + @Builder | 4 |
| Sealed 类 | 15+ | - | 3 |
| Text Blocks | 13+ | 字符串拼接 | 1 |
| Pattern Matching for switch | 16+ | if-else, instanceof | 10+ |
| Switch 表达式 | 14+ | 传统 switch | 10+ |
| Stream.toList() | 16+ | collect(Collectors.toList()) | 1 |
| String.formatted() | 15+ | String.format() | 1 |

---

## 编译要求

- **JDK**: 21+
- **Maven**: 3.6.0+
- **Spring Boot**: 3.0+

编译命令：
```bash
mvn clean compile -DskipTests
```

运行命令：
```bash
cd user-start
mvn spring-boot:run
```

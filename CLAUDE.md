# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Domain-Driven Design (DDD) architecture reference project** that demonstrates clean architecture principles based on COLA (Clean Object-Oriented and Layered Architecture) from Alibaba. It contains architectural documentation (Mermaid diagrams) and a working implementation of a user registration system with **extension point mechanism**.

## Build and Run Commands

### Build the entire project
```bash
mvn clean install -DskipTests
```

### Run the application
```bash
cd user-start
mvn spring-boot:run
```
Application starts on `http://localhost:8080`

### Run specific module tests
```bash
cd <module-name>  # e.g., user-domain, user-app
mvn test
```

### H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:user_db`
- Username: `sa`
- Password: (empty)

## Module Structure (Maven Multi-Module)

```
ddd-architecture/
├── user-client/          # Client层 - 对外API契约（DTO、Command、Query）
├── user-adapter/         # Adapter层 - 协议适配（Controller）
├── user-app/             # Application层 - 业务编排（Executor、Assembler）
├── user-domain/          # Domain层 - 核心业务逻辑（Entity、Gateway接口、扩展点）
├── user-infrastructure/  # Infrastructure层 - 技术实现（Repository、Gateway实现）
└── user-start/           # Start模块 - 启动入口（Spring Boot应用）
```

**Dependency chain**: `user-start` → `user-adapter` → `user-app` → `user-domain` + `user-infrastructure` → `user-client`

## Architecture Documentation

The project contains comprehensive Mermaid diagrams in the `docs/` folder:

- **ddd-architecture-overview-v2.mermaid** - Overall architecture flow with module boxes and data flow
- **ddd-architecture-with-notes-v2.mermaid** - Detailed implementation view with design principles
- **ddd-architecture-diagram-v2.mermaid** - UML class diagram showing all relationships
- **ddd-architecture-sequence-v2.mermaid** - Sequence diagrams for normal flow, extension flow, exception handling

Additionally:
- **COLA_Features_Implementation_Guide.md** - Complete feature implementation guide
- **COLA_Extension_Point_Usage_Examples.md** - Extension point usage examples

---

## COLA Architecture Compliance Analysis

### Overall Score: 9.5/10

This implementation demonstrates **excellent alignment with COLA 5.0.0 principles**. Below is a detailed breakdown.

### ✅ Properly Implemented COLA Patterns

#### 1. Layer Structure (100% Compliant)
The project correctly implements all 5 COLA layers with clear separation:

| Layer | COLA Requirement | Implementation |
|-------|-----------------|----------------|
| Client | Define API contracts | `UserServiceI`, `UserDTO`, `UserRegisterCmd`, `UserByIdQry` |
| Adapter | Thin protocol conversion | `UserController` - only routing, no business logic |
| Application | Orchestration + CQRS | `UserRegisterCmdExe`, `UserByIdQryExe` |
| Domain | Core business logic | `User` entity, `Email`/`Phone` value objects |
| Infrastructure | Technical implementation | `UserRepositoryImpl`, `EmailGatewayImpl` |

#### 2. CQRS Pattern (100% Compliant)
```java
// Command (Write Operation)
// user-client/dto/cmd/UserRegisterCmd.java
public class UserRegisterCmd extends Command { ... }

// Query (Read Operation)
// user-client/dto/query/UserByIdQry.java
public class UserByIdQry extends Query { ... }

// Separate Executors
// user-app/executor/UserRegisterCmdExe.java (with @Transactional)
// user-app/executor/UserByIdQryExe.java (no transaction)
```

#### 3. Dependency Inversion Principle (100% Compliant)
```java
// Domain layer defines interface
// user-domain/gateway/IUserRepository.java
public interface IUserRepository {
    User save(User user);
    User findById(Long id);
}

// Infrastructure layer implements
// user-infrastructure/gatewayimpl/UserRepositoryImpl.java
@Repository
public class UserRepositoryImpl implements IUserRepository { ... }
```

#### 4. Rich Domain Model (100% Compliant)
```java
// user-domain/model/User.java
public class User {
    // Business logic encapsulated in aggregate root
    public void register(String encryptedPassword) {
        this.status = UserStatus.INACTIVE;
        this.password = encryptedPassword;
        this.registerTime = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == UserStatus.FROZEN) {
            throw new IllegalStateException("冻结用户不能激活");
        }
        this.status = UserStatus.ACTIVE;
    }
}
```

#### 5. Value Objects (100% Compliant)
```java
// user-domain/model/Email.java
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("...");

    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        this.value = value;
    }
}
```

#### 6. Anti-Corruption Layer (ACL) (100% Compliant)
```java
// Domain defines gateway interface
// user-domain/gateway/IEmailGateway.java
public interface IEmailGateway {
    boolean sendWelcomeEmail(String to, String username);
}

// Infrastructure provides isolation
// user-infrastructure/gatewayimpl/EmailGatewayImpl.java
@Component
public class EmailGatewayImpl implements IEmailGateway { ... }
```

#### 7. Transaction Boundary Control (100% Compliant)
```java
// user-app/executor/UserRegisterCmdExe.java
@Component
public class UserRegisterCmdExe {
    @Transactional(rollbackFor = Exception.class)
    public Response execute(UserRegisterCmd cmd) {
        // Orchestration within transaction boundary
    }
}
```

#### 8. Extension Point Mechanism (100% Compliant) ✅ NEW
```java
// user-domain/extension/ExtensionPointI.java
public interface ExtensionPointI { }

// user-domain/extension/Extension.java
@Extension(bizId = "VIP")
public class VipUserValidatorExt implements UserValidatorExtPt { ... }

// user-domain/extension/ExtensionExecutor.java
@Component
public class ExtensionExecutor {
    public <T extends ExtensionPointI, R> execute(
        Class<T> extensionPoint,
        BizScenario bizScenario,
        ExtensionInvoker<T, R> executor
    ) { ... }
}
```

**8 Extension Point Interfaces Implemented:**
1. `UserValidatorExtPt` - User validation (DEFAULT, VIP)
2. `UserRegisterBonusExtPt` - Registration bonus (DEFAULT, VIP)
3. `LevelUpgradeExtPt` - Level upgrade (NORMAL, VIP, ENTERPRISE)
4. `RiskCheckExtPt` - Risk control (login, register, payment, withdraw×2)
5. `PointCalculateExtPt` - Point calculation (purchase×2, checkin, continuous, comment)
6. `NotificationChannelExtPt` - Notification channels (DEFAULT, VIP, ENTERPRISE)
7. `OrderDiscountExtPt` - Order discount (NEW_USER, DEFAULT, VIP, ENTERPRISE)
8. `RegisterValidatorExtPt` - Registration validation (email, phone, enterprise, wechat)

**27 Extension Point Implementations** in `user-domain/extension/impl/`

#### 9. Standard Exception Hierarchy (100% Compliant) ✅ NEW
```java
// user-client/exception/BizException.java
public class BizException extends BaseException {
    public static BizException of(ErrorCode errorCode) { ... }
    public static BizException of(String errCode, String errMsg) { ... }
}

// user-client/exception/SysException.java
public class SysException extends BaseException {
    public static SysException wrap(String errCode, Throwable cause) { ... }
}

// user-adapter/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException e) { ... }
}
```

### COLA Best Practices Followed

1. **"No necessity, don't add entities"** - Value objects (Email, Phone) only where validation needed
2. **Thin Controller** - `UserController` only does routing and validation
3. **Business logic in Domain** - `User.register()`, `User.activate()` contain business rules
4. **Gateway interfaces face domain, not technology** - `IUserRepository` methods return `User`, not `UserPO`
5. **Transaction at Application layer** - `@Transactional` on `UserRegisterCmdExe`
6. **Extension points for multi-scenario** - 8 interfaces with 27 implementations
7. **Standard exception handling** - `BizException`/`SysException` with `GlobalExceptionHandler`

### Architecture Comparison Table

| COLA Principle | Implementation Status | Evidence |
|---------------|----------------------|----------|
| Clean Architecture | ✅ Full | Domain layer has zero dependencies on outer layers |
| Hexagonal Architecture | ✅ Full | Gateway interfaces as ports, Infrastructure as adapters |
| Onion Architecture | ✅ Full | Domain at core, dependencies point inward |
| CQRS | ✅ Full | Command/Query separation with executors |
| DDD | ✅ Partial | Aggregates and value objects, no bounded contexts yet |
| Dependency Inversion | ✅ Full | Domain defines interfaces, Infrastructure implements |
| Extension Points | ✅ Full | 8 interfaces, 27 implementations |
| Rich Domain Model | ✅ Full | Business logic in entities |
| Anti-Corruption Layer | ✅ Full | Gateway interfaces isolate external services |

---

## Core Architecture: 5 Layers

### 1. Adapter Layer (适配层)
- **Purpose**: Protocol conversion for HTTP/MQ/Scheduled tasks
- **Components**: Controllers, EventListeners, Jobs
- **Principle**: Isolate external protocols, validate parameters, thin layer with no business logic
- **Example**: `UserController` only routes to `UserServiceI`

### 2. Client Layer (对外契约)
- **Purpose**: Define external API contracts
- **Components**: Service interfaces, DTOs, Commands (write), Queries (read)
- **Pattern**: CQRS (Command Query Responsibility Segregation)
- **Example**: `UserServiceI`, `UserRegisterCmd`, `UserByIdQry`, `Response`, `BizException`

### 3. Application Layer (应用层)
- **Purpose**: Business process orchestration
- **Components**: Application services, Command executors, Query executors, Assemblers
- **Principle**: No business logic, orchestrates domain objects, controls transaction boundaries with @Transactional
- **Example**: `UserRegisterCmdExe.execute()` orchestrates validation, domain logic, persistence, notifications

### 4. Domain Layer (领域层) - THE CORE
- **Purpose**: Encapsulate all business rules and logic
- **Components**: Aggregate roots, Value objects, Domain services, Gateway interfaces (ACL), Repository interfaces, Extension points
- **Principle**: Rich domain model, technology-agnostic, interface-driven, defines contracts for Infrastructure to implement
- **Example**: `User` entity with `register()`, `activate()`, `freeze()` methods
- **Extension Points**: `ExtensionExecutor`, `BizScenario`, `@Extension` annotation

### 5. Infrastructure Layer (基础设施层)
- **Purpose**: Technical implementation and external integration
- **Components**: Repository implementations, Mappers, Persistence objects (PO), Converters, Gateway implementations
- **Principle**: Implements Domain layer interfaces (Dependency Inversion), handles DB/cache/MQ/third-party services
- **Example**: `UserRepositoryImpl` implements `IUserRepository`, `UserConverter` handles DO ↔ Domain mapping

## Key Design Principles

### Dependency Inversion (DIP)
```
┌──────────────────┐     defines      ┌──────────────────┐
│    Domain        │ ───────────────► │   IGateway       │
│   (Core)         │                  │   Interfaces     │
└──────────────────┘                  └──────────────────┘
                                              ▲
                                              │ implements
┌──────────────────┐                         │
│  Infrastructure  │ ────────────────────────┘
│  (Implementation)│
└──────────────────┘
```

**Key Points**:
- Domain layer defines interfaces (`IUserRepository`, `IEmailGateway`, `ISmsGateway`)
- Infrastructure layer implements these interfaces
- Dependency direction: Infrastructure → Domain (not the other way)
- Domain layer has ZERO dependencies on other layers

### Separation of Concerns
| Layer | Responsibility | Example |
|-------|---------------|---------|
| Domain | **What** - Business rules | `User.register()`, `User.activate()` |
| Application | **When** - Flow orchestration | `UserRegisterCmdExe.execute()` |
| Infrastructure | **How** - Technical details | `UserRepositoryImpl`, `EmailGatewayImpl` |

### Rich Domain Model vs Anemic Model

**Rich Model (Current Implementation)** ✅:
```java
// Business logic in entity
user.register(encryptedPassword);
user.activate();
user.changeEmail(newEmail);
```

**Anemic Model (Avoid)** ❌:
```java
// Service with only data holders
userService.register(user);
userService.activate(user);
userService.changeEmail(user, newEmail);
```

### Anti-Corruption Layer (ACL)
Gateway interfaces isolate external dependencies:
- **Domain** defines: `IEmailGateway`, `ISmsGateway`, `IUserRepository`
- **Infrastructure** implements: `EmailGatewayImpl`, `SmsGatewayImpl`, `UserRepositoryImpl`
- Changes to external services (email provider, SMS provider, database) don't affect Domain layer

## Extension Point Mechanism

### Core Components
- **ExtensionPointI**: Marker interface for all extension points
- **@Extension**: Annotation to mark extension implementations with `bizId`, `useCase`, `scenario`
- **BizScenario**: Business scenario identifier (bizId.useCase.scenario)
- **ExtensionExecutor**: Executes the appropriate extension based on scenario

### Usage Example
```java
@Autowired
private ExtensionExecutor extensionExecutor;

// Route to VIP implementation based on scenario
BizScenario scenario = BizScenario.valueOf("VIP", "register");

extensionExecutor.executeVoid(
    UserValidatorExtPt.class,
    scenario,
    ext -> ext.validate(user, scenario)
);
```

### Implemented Extension Points
8 interfaces with 27 implementations covering:
- User validation
- Registration bonuses
- Level upgrades
- Risk checks
- Point calculations
- Notification channels
- Order discounts
- Registration validation

## Request Flow

```
┌─────────────┐
│ HTTP Request│
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────────┐
│  Adapter Layer (UserController)             │
│  - Protocol conversion                      │
│  - @Valid parameter validation              │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│  Application Layer (UserRegisterCmdExe)    │
│  - @Transactional (boundary)                │
│  - Business validation                      │
│  - Orchestration                            │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│  Domain Layer (User + UserDomainService)   │
│  - Business rules                           │
│  - Invariants                               │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│  Infrastructure Layer                       │
│  - UserRepositoryImpl (persistence)         │
│  - EmailGatewayImpl (external service)      │
│  - SmsGatewayImpl (external service)        │
└─────────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│  Response DTO (through layers)              │
└─────────────────────────────────────────────┘
```

## Development Patterns

### CQRS Implementation

**Commands (Write)**:
```java
// user-client/dto/cmd/UserRegisterCmd.java
public class UserRegisterCmd extends Command {
    private String username;
    private String email;
    private String password;
}

// user-app/executor/UserRegisterCmdExe.java
@Component
public class UserRegisterCmdExe {
    @Transactional
    public Response execute(UserRegisterCmd cmd) { ... }
}
```

**Queries (Read)**:
```java
// user-client/dto/query/UserByIdQry.java
public class UserByIdQry extends Query {
    private Long userId;
}

// user-app/executor/UserByIdQryExe.java
@Component
public class UserByIdQryExe {
    public Response execute(UserByIdQry qry) { ... }
}
```

### Aggregate Root Pattern

**User Aggregate Root**:
```java
// user-domain/model/User.java
@Data
public class User {
    private Long id;              // Aggregate ID
    private Email email;          // Value Object
    private Phone phone;          // Value Object
    private UserStatus status;

    // Business methods maintaining invariants
    public void register(String encryptedPassword) { ... }
    public void activate() { ... }
    public void freeze() { ... }
}
```

### Value Objects

**Email Value Object**:
```java
// user-domain/model/Email.java
public class Email {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        this.value = value;
    }
}
```

## Package Structure (COLA Convention)

**Current Structure**:
```
com.example.user/
├── adapter/web/           # Controllers
├── app/
│   ├── executor/          # Command/Query executors
│   └── assembler/         # DTO converters
├── domain/
│   ├── model/             # Entities, Value Objects
│   ├── gateway/           # Repository/Gateway interfaces
│   ├── service/           # Domain Services
│   └── extension/         # Extension point mechanism
└── infrastructure/
    ├── gatewayimpl/       # Repository/Gateway implementations
    ├── mapper/            # MyBatis mappers
    └── converter/         # DO ↔ Domain converters
```

## Key Implementation Components

### User Registration Flow Example

| Layer | Component | File Path | Responsibility |
|-------|-----------|-----------|----------------|
| **Client** | `UserServiceI` | `user-client/api/UserServiceI.java` | API contract |
| **Client** | `UserRegisterCmd` | `user-client/dto/cmd/UserRegisterCmd.java` | Command DTO |
| **Adapter** | `UserController` | `user-adapter/web/UserController.java` | HTTP routing |
| **Application** | `UserRegisterCmdExe` | `user-app/executor/UserRegisterCmdExe.java` | Command execution |
| **Domain** | `User` | `user-domain/model/User.java` | Aggregate root |
| **Domain** | `IUserRepository` | `user-domain/gateway/IUserRepository.java` | Repository interface |
| **Domain** | `ExtensionExecutor` | `user-domain/extension/ExtensionExecutor.java` | Extension routing |
| **Infrastructure** | `UserRepositoryImpl` | `user-infrastructure/gatewayimpl/UserRepositoryImpl.java` | Repository impl |

## Common Pitfalls to Avoid

### 1. Layer Violation ❌
```java
// BAD: Adapter directly calling Domain
@RestController
public class UserController {
    @Autowired
    private UserDomainService domainService;  // VIOLATION
}

// GOOD: Adapter calls Application which calls Domain
@RestController
public class UserController {
    @Autowired
    private UserServiceI userService;  // CORRECT
}
```

### 2. Domain Depending on Infrastructure ❌
```java
// BAD: Domain importing Infrastructure classes
import com.example.infrastructure.UserPO;  // VIOLATION

// GOOD: Domain defines interface, Infrastructure implements
public interface IUserRepository { }
@Repository
class UserRepositoryImpl implements IUserRepository { }
```

### 3. Business Logic in Controller ❌
```java
// BAD: Controller with business logic
@PostMapping("/register")
public Response register(@RequestBody UserRegisterCmd cmd) {
    if (emailExists(cmd.getEmail())) {  // Business logic
        return Response.buildFailure("EMAIL_EXISTS");
    }
}

// GOOD: Controller only routes
@PostMapping("/register")
public Response register(@Valid @RequestBody UserRegisterCmd cmd) {
    return userService.register(cmd);
}
```

## Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Language |
| **Spring Boot** | 3.2.0 | Application framework |
| **MyBatis Plus** | 3.5.5 | ORM framework |
| **H2 Database** | 2.2.224 | In-memory dev database |
| **Lombok** | 1.18.30 | Reduce boilerplate |
| **Jakarta Validation** | 3.0.2 | Bean validation |

## Testing Strategy

### Unit Tests by Layer

| Layer | Test Strategy | Mock Dependencies |
|-------|--------------|-------------------|
| Domain | Test business logic in isolation | Mock Gateway interfaces |
| Application | Test orchestration logic | Mock Domain + Infrastructure |
| Infrastructure | Test technical implementations | Use in-memory database |
| Adapter | Test web layer only | Mock Application services |

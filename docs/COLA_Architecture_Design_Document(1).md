# COLA架构设计说明文档

**版本：** 5.0.0  
**文档创建日期：** 2026年1月27日  
**作者：** 基于阿里巴巴COLA开源项目整理

---

## 目录

1. [架构概述](#1-架构概述)
2. [设计理念与原则](#2-设计理念与原则)
3. [核心架构设计](#3-核心架构设计)
4. [分层架构详解](#4-分层架构详解)
5. [包结构设计](#5-包结构设计)
6. [核心组件](#6-核心组件)
7. [技术特性](#7-技术特性)
8. [最佳实践](#8-最佳实践)
9. [应用场景](#9-应用场景)
10. [版本演进](#10-版本演进)
11. [实施指南](#11-实施指南)
12. [注意事项与反模式](#12-注意事项与反模式)

---

## 1. 架构概述

### 1.1 COLA简介

**COLA**是**Clean Object-Oriented and Layered Architecture**的缩写，代表"整洁面向对象分层架构"。COLA是由阿里巴巴技术专家张建飞提出的一套应用架构最佳实践，旨在解决复杂业务系统中常见的代码混乱、耦合度高、可维护性差等问题。

### 1.2 核心价值

COLA架构的核心价值在于：

- **定义良好的应用结构**：通过清晰的分层和包结构，降低系统复杂度
- **提供可落地的工具**：不仅是理论，还提供Maven Archetype脚手架工具
- **治理应用复杂度**：从混乱状态走向有序状态，降低系统熵值
- **提升研发效率**：标准化的结构让团队协作更高效
- **增强可维护性**：清晰的职责划分使代码易于理解和修改

### 1.3 COLA组成

COLA分为两个主要部分：

1. **COLA架构（Architecture）**
   - 定义应用的分层结构
   - 定义包结构和命名规范
   - 提供项目脚手架工具（Archetype）

2. **COLA组件（Components）**
   - 通用功能组件库
   - 提升研发效率的工具集
   - 独立于架构，可单独使用

---

## 2. 设计理念与原则

### 2.1 核心设计思想

COLA架构融合了多种先进的架构思想：

#### 2.1.1 整洁架构（Clean Architecture）
- 业务逻辑与技术实现分离
- 依赖方向单向流动
- 核心业务不依赖外部技术细节

#### 2.1.2 六边形架构（Hexagonal Architecture）
- 使用端口-适配器模式
- 解耦技术细节
- 外部依赖可替换

#### 2.1.3 洋葱架构（Onion Architecture）
- 以领域为核心
- 依赖倒置原则
- 内层不依赖外层

#### 2.1.4 DDD思想
- 领域驱动设计理念
- 但不强制要求完整的DDD实践
- "无必要勿增实体"原则

### 2.2 设计原则

#### 2.2.1 分层原则
- **职责单一**：每层专注于特定职责
- **依赖单向**：高层依赖低层，反之不可
- **开放性**：应用层可以直接访问基础设施层（实用主义）

#### 2.2.2 分包原则
- **领域优先**：顶层按领域划分
- **功能内聚**：同类功能放在同一包
- **腐烂隔离**：问题局限在特定领域内

#### 2.2.3 面向对象原则
- **封装**：隐藏实现细节
- **继承**：复用通用逻辑
- **多态**：扩展点机制
- **充血模型**：领域对象包含业务方法（可选）

---

## 3. 核心架构设计

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────┐
│                    Adapter Layer                        │
│         (Web, Mobile, RPC, MQ, Scheduled Job)          │
│                                                         │
│  - controller/endpoint: REST/RPC接口                    │
│  - converter: DTO与领域对象转换                         │
│  - assembler: 数据组装                                  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Application Layer                     │
│              (App Service + CQRS)                       │
│                                                         │
│  - service: 应用服务（编排）                            │
│  - executor: 命令执行器 (Command)                       │
│  - query: 查询服务 (Query)                              │
│  - assembler: 上下文组装                                │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│                 (Core Business Logic)                   │
│                                                         │
│  - entity: 领域实体（可选）                             │
│  - model: 领域模型                                      │
│  - ability: 领域能力（业务逻辑）                        │
│  - gateway: 领域网关（接口定义）                        │
└─────────────────────────────────────────────────────────┘
                            ↑
┌─────────────────────────────────────────────────────────┐
│                Infrastructure Layer                     │
│              (Technical Implementation)                 │
│                                                         │
│  - gatewayimpl: 网关实现（防腐层ACL）                   │
│  - repository: 数据访问                                 │
│  - config: 配置                                         │
│  - mapper: 数据映射                                     │
│  - tunnel: 外部服务调用                                 │
└─────────────────────────────────────────────────────────┘
```

### 3.2 依赖关系

```
┌──────────┐
│  Client  │  (对外API定义，DTO)
└──────────┘
     ↑
     │ depends on
     │
┌──────────┐      ┌──────────┐
│ Adapter  │ ---> │   App    │
└──────────┘      └──────────┘
                       ↓
                  ┌──────────┐
                  │  Domain  │  (核心，不依赖任何层)
                  └──────────┘
                       ↑
                       │ implements
                       │
                  ┌──────────┐
                  │  Infra   │
                  └──────────┘
```

**关键特性：**
- Domain层是核心，不依赖任何其他层
- Infrastructure层实现Domain层定义的接口（依赖倒置）
- App层可以直接访问Infrastructure层（实用主义，避免过度抽象）
- Client层定义对外契约，其他模块依赖它

---

## 4. 分层架构详解

### 4.1 Client层（客户端层）

**职责：**
- 定义对外API契约
- 定义DTO（数据传输对象）
- 定义常量和枚举

**主要内容：**
```
client/
├── api/              # 对外API接口定义
│   ├── CustomerServiceI.java
│   └── OrderServiceI.java
├── dto/              # 数据传输对象
│   ├── CustomerDTO.java
│   ├── OrderDTO.java
│   └── cmd/          # 命令对象
│       └── CustomerAddCmd.java
│   └── query/        # 查询对象
│       └── CustomerListQuery.java
└── constant/         # 常量定义
    └── ErrorCode.java
```

**设计要点：**
- DTO要简单纯粹，只有数据，无业务逻辑
- 使用CQRS模式区分Command和Query
- 其他模块通过依赖client包来调用服务

### 4.2 Adapter层（适配层）

**职责：**
- 对接不同类型的终端（Web、Mobile、RPC等）
- 路由请求到应用层
- 数据格式转换和适配

**主要内容：**
```
adapter/
├── web/              # Web控制器
│   └── CustomerController.java
├── mobile/           # 移动端适配器
├── rpc/              # RPC服务实现
├── mq/               # 消息队列消费者
├── scheduled/        # 定时任务
└── converter/        # 转换器
    └── CustomerConverter.java
```

**设计要点：**
- 相当于MVC中的Controller
- 薄薄一层，不包含业务逻辑
- 负责参数校验、格式转换
- 调用Application层服务

**示例代码：**
```java
@RestController
@RequestMapping("/customer")
public class CustomerController {
    
    @Autowired
    private CustomerServiceI customerService;
    
    @PostMapping("/add")
    public Response<String> addCustomer(@RequestBody CustomerAddCmd cmd) {
        // 薄薄一层，直接调用应用服务
        return customerService.addCustomer(cmd);
    }
}
```

### 4.3 Application层（应用层）

**职责：**
- 获取输入，组装上下文
- 参数校验（业务规则校验）
- 调用领域层处理业务
- 发送消息通知
- 编排多个领域能力

**主要内容：**
```
app/
├── executor/         # 命令执行器（Command处理）
│   └── CustomerAddCmdExe.java
├── query/            # 查询服务（Query处理）
│   └── CustomerListQryExe.java
└── assembler/        # 上下文组装器
    └── CustomerAssembler.java
```

**设计要点：**
- 实现CQRS模式（命令查询职责分离）
- executor处理写操作（Command）
- query处理读操作（Query）
- 可以绕过Domain层直接访问Infrastructure层（简单查询）
- 不包含核心业务逻辑（业务逻辑在Domain层）

**示例代码：**
```java
// Command执行器
@Component
public class CustomerAddCmdExe {
    
    @Autowired
    private CustomerGateway customerGateway;
    
    public Response<String> execute(CustomerAddCmd cmd) {
        // 1. 参数校验
        // 2. 调用领域能力
        Customer customer = CustomerFactory.create(cmd);
        customer.validate();
        
        // 3. 持久化
        customerGateway.save(customer);
        
        return Response.buildSuccess(customer.getId());
    }
}

// Query执行器
@Component
public class CustomerListQryExe {
    
    @Autowired
    private CustomerMapper customerMapper;  // 直接访问Infrastructure
    
    public Response<List<CustomerDTO>> execute(CustomerListQuery qry) {
        // 简单查询可以直接访问数据库
        List<CustomerDO> list = customerMapper.list(qry);
        return Response.buildSuccess(convert(list));
    }
}
```

### 4.4 Domain层（领域层）

**职责：**
- 封装核心业务逻辑
- 定义领域模型和实体
- 提供领域能力（Ability）
- 定义领域网关接口（Gateway）

**主要内容：**
```
domain/
├── model/            # 领域模型（可选）
│   ├── Customer.java
│   └── Order.java
├── ability/          # 领域能力
│   ├── CustomerDomainService.java
│   └── OrderDomainService.java
└── gateway/          # 领域网关接口
    ├── CustomerGateway.java
    └── OrderGateway.java
```

**设计要点：**
- 领域层是核心，不依赖任何其他层
- model是可选的（COLA不强制DDD）
- Gateway接口由Infrastructure层实现（依赖倒置）
- 可以使用充血模型，也可以使用贫血模型

**充血模型示例：**
```java
// 领域实体（充血模型）
public class Customer {
    private String id;
    private String name;
    private CustomerType type;
    
    // 业务方法在实体内部
    public void upgrade() {
        if (this.type == CustomerType.NORMAL) {
            this.type = CustomerType.VIP;
        }
    }
    
    public void validate() {
        if (StringUtils.isEmpty(name)) {
            throw new BizException("客户名称不能为空");
        }
    }
}

// 领域服务（复杂业务逻辑）
@Component
public class CustomerDomainService {
    
    public void transfer(Customer from, Customer to, BigDecimal amount) {
        // 跨实体的业务逻辑
        from.deduct(amount);
        to.add(amount);
    }
}
```

**网关接口示例：**
```java
// 领域网关接口（在Domain层定义）
public interface CustomerGateway {
    Customer getById(String id);
    void save(Customer customer);
    List<Customer> listByType(CustomerType type);
}
```

### 4.5 Infrastructure层（基础设施层）

**职责：**
- 实现领域网关接口
- 处理技术细节（数据库、RPC、搜索引擎等）
- 提供防腐层（ACL）
- 外部依赖的转义处理

**主要内容：**
```
infrastructure/
├── gatewayimpl/      # 网关实现
│   └── CustomerGatewayImpl.java
├── repository/       # 数据仓储
│   └── CustomerRepository.java
├── mapper/           # MyBatis Mapper
│   └── CustomerMapper.java
├── dataobject/       # 数据对象（DO）
│   └── CustomerDO.java
├── tunnel/           # 外部服务调用
│   └── PaymentTunnel.java
├── config/           # 配置
│   └── DatabaseConfig.java
└── converter/        # 转换器
    └── CustomerConverter.java
```

**设计要点：**
- 实现Domain层定义的Gateway接口
- 对外部依赖进行封装和转义（防腐层）
- 处理DO与领域模型的转换
- 技术实现细节不向上泄露

**示例代码：**
```java
// 网关实现
@Component
public class CustomerGatewayImpl implements CustomerGateway {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerConverter converter;
    
    @Override
    public Customer getById(String id) {
        CustomerDO customerDO = customerMapper.selectById(id);
        return converter.toDomain(customerDO);
    }
    
    @Override
    public void save(Customer customer) {
        CustomerDO customerDO = converter.toDO(customer);
        customerMapper.insert(customerDO);
    }
}

// 转换器（DO <-> Domain）
@Component
public class CustomerConverter {
    
    public Customer toDomain(CustomerDO customerDO) {
        if (customerDO == null) return null;
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDO, customer);
        return customer;
    }
    
    public CustomerDO toDO(Customer customer) {
        if (customer == null) return null;
        CustomerDO customerDO = new CustomerDO();
        BeanUtils.copyProperties(customer, customerDO);
        return customerDO;
    }
}
```

### 4.6 Start模块（启动模块）

**职责：**
- 应用启动入口
- 全局配置
- 依赖聚合

**主要内容：**
```
start/
├── Application.java           # SpringBoot启动类
├── application.yml            # 配置文件
└── resources/
    └── logback.xml
```

---

## 5. 包结构设计

### 5.1 顶层包结构原则

**先按领域分包，再按功能分包**

```
com.company.project/
├── customer/          # 客户领域
│   ├── controller/
│   ├── executor/
│   ├── query/
│   ├── ability/
│   └── gateway/
└── order/             # 订单领域
    ├── controller/
    ├── executor/
    ├── query/
    ├── ability/
    └── gateway/
```

**优势：**
- 领域内聚，降低耦合
- 团队并行开发互不干扰
- 代码腐烂局限在特定领域
- 便于微服务拆分

### 5.2 完整包结构示例

```
demo-web/
├── demo-web-client/           # Client层
│   └── src/main/java/com/company/demo/
│       ├── api/
│       │   ├── CustomerServiceI.java
│       │   └── OrderServiceI.java
│       ├── dto/
│       │   ├── cmd/
│       │   │   ├── CustomerAddCmd.java
│       │   │   └── OrderCreateCmd.java
│       │   ├── query/
│       │   │   ├── CustomerListQuery.java
│       │   │   └── OrderListQuery.java
│       │   └── data/
│       │       ├── CustomerDTO.java
│       │       └── OrderDTO.java
│       └── constant/
│           └── ErrorCode.java
│
├── demo-web-adapter/          # Adapter层
│   └── src/main/java/com/company/demo/
│       ├── customer/
│       │   └── web/
│       │       └── CustomerController.java
│       └── order/
│           └── web/
│               └── OrderController.java
│
├── demo-web-app/              # Application层
│   └── src/main/java/com/company/demo/
│       ├── customer/
│       │   ├── executor/
│       │   │   └── CustomerAddCmdExe.java
│       │   └── query/
│       │       └── CustomerListQryExe.java
│       └── order/
│           ├── executor/
│           │   └── OrderCreateCmdExe.java
│           └── query/
│               └── OrderListQryExe.java
│
├── demo-web-domain/           # Domain层
│   └── src/main/java/com/company/demo/
│       ├── customer/
│       │   ├── model/
│       │   │   └── Customer.java
│       │   ├── ability/
│       │   │   └── CustomerDomainService.java
│       │   └── gateway/
│       │       └── CustomerGateway.java
│       └── order/
│           ├── model/
│           │   └── Order.java
│           ├── ability/
│           │   └── OrderDomainService.java
│           └── gateway/
│               └── OrderGateway.java
│
├── demo-web-infrastructure/   # Infrastructure层
│   └── src/main/java/com/company/demo/
│       ├── customer/
│       │   ├── gatewayimpl/
│       │   │   └── CustomerGatewayImpl.java
│       │   ├── mapper/
│       │   │   └── CustomerMapper.java
│       │   └── dataobject/
│       │       └── CustomerDO.java
│       └── order/
│           ├── gatewayimpl/
│           │   └── OrderGatewayImpl.java
│           ├── mapper/
│           │   └── OrderMapper.java
│           └── dataobject/
│               └── OrderDO.java
│
└── start/                     # 启动模块
    └── src/main/java/com/company/demo/
        └── Application.java
```

### 5.3 包结构功能说明表

| 包名 | 功能 | 是否必需 |
|------|------|----------|
| **Client模块** |
| api | 对外API接口定义 | 是 |
| dto.cmd | 命令对象（写操作） | 是 |
| dto.query | 查询对象（读操作） | 是 |
| dto.data | 数据传输对象 | 是 |
| constant | 常量、枚举 | 是 |
| **Adapter模块** |
| web | Web控制器 | 按需 |
| mobile | 移动端适配器 | 按需 |
| rpc | RPC服务实现 | 按需 |
| mq | 消息队列消费者 | 按需 |
| scheduled | 定时任务 | 按需 |
| **App模块** |
| executor | 命令执行器 | 是 |
| query | 查询服务 | 是 |
| assembler | 上下文组装 | 按需 |
| **Domain模块** |
| model | 领域模型 | **否**（可选） |
| ability | 领域能力/服务 | 是 |
| gateway | 领域网关接口 | 是 |
| **Infrastructure模块** |
| gatewayimpl | 网关实现 | 是 |
| repository | 数据仓储 | 按需 |
| mapper | MyBatis Mapper | 按需 |
| dataobject | 数据对象 | 是 |
| tunnel | 外部服务调用 | 按需 |
| config | 配置 | 按需 |
| converter | 对象转换 | 是 |

---

## 6. 核心组件

COLA提供了一系列可复用的通用组件，位于`cola-components`目录下：

### 6.1 DTO组件（cola-component-dto）

**功能：**
- 定义统一的Response格式
- 提供分页查询支持
- 统一异常处理

**核心类：**
```java
// 统一响应对象
public class Response<T> {
    private boolean success;
    private String errCode;
    private String errMessage;
    private T data;
    
    public static <T> Response<T> buildSuccess(T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static Response buildFailure(String errCode, String errMessage) {
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }
}

// 分页请求
public class PageQuery extends Query {
    private int pageIndex = 1;
    private int pageSize = 10;
    
    // getters and setters
}

// 分页响应
public class PageResponse<T> extends Response<T> {
    private int totalCount;
    private int pageSize;
    private int pageIndex;
    
    // getters and setters
}
```

### 6.2 Exception组件（cola-component-exception）

**功能：**
- 定义异常体系
- 区分业务异常和系统异常

**核心类：**
```java
// 业务异常（可预期，需要友好提示用户）
public class BizException extends RuntimeException {
    private String errCode;
    
    public BizException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
    }
}

// 系统异常（不可预期，需要告警）
public class SysException extends RuntimeException {
    private String errCode;
    
    public SysException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
    }
}
```

### 6.3 Extension组件（cola-component-extension-starter）

**功能：**
- 提供扩展点机制
- 支持业务身份（BizScenario）
- 实现可插拔的业务逻辑

**核心概念：**
```java
// 扩展点接口
public interface CustomerCheckExtPt extends ExtensionPointI {
    void check(Customer customer);
}

// 默认实现
@Extension(bizId = "DEFAULT")
public class DefaultCustomerCheckExt implements CustomerCheckExtPt {
    @Override
    public void check(Customer customer) {
        // 默认校验逻辑
    }
}

// VIP客户特殊实现
@Extension(bizId = "VIP", useCase = "register")
public class VipCustomerCheckExt implements CustomerCheckExtPt {
    @Override
    public void check(Customer customer) {
        // VIP客户的特殊校验逻辑
    }
}

// 使用扩展点
@Component
public class CustomerService {
    @Autowired
    private ExtensionExecutor extensionExecutor;
    
    public void register(Customer customer, BizScenario bizScenario) {
        // 根据业务场景自动选择合适的扩展实现
        extensionExecutor.execute(
            CustomerCheckExtPt.class, 
            bizScenario,
            ext -> ext.check(customer)
        );
    }
}
```

**BizScenario（业务场景）：**
```
BizScenario = bizId + useCase + scenario

例如：
- bizId: VIP（VIP客户）
- useCase: register（注册场景）
- scenario: 具体子场景（可选）

完整标识：VIP.register
```

### 6.4 状态机组件（cola-component-statemachine）

**功能：**
- 提供状态机框架
- 简化状态流转逻辑
- 保证状态转换的合法性

**示例：**
```java
// 定义状态和事件
enum OrderState {
    CREATED, PAID, SHIPPED, COMPLETED
}

enum OrderEvent {
    PAY, SHIP, COMPLETE
}

// 构建状态机
StateMachine<OrderState, OrderEvent, OrderContext> machine = 
    StateMachineBuilder.<OrderState, OrderEvent, OrderContext>builder()
        .externalTransition()
            .from(OrderState.CREATED)
            .to(OrderState.PAID)
            .on(OrderEvent.PAY)
            .when(checkCondition())
            .perform(doAction())
        .and()
        .externalTransition()
            .from(OrderState.PAID)
            .to(OrderState.SHIPPED)
            .on(OrderEvent.SHIP)
            .perform(doAction())
        .build("OrderStateMachine");

// 触发状态转换
OrderState newState = machine.fireEvent(
    OrderState.CREATED, 
    OrderEvent.PAY, 
    orderContext
);
```

### 6.5 其他组件

| 组件名称 | 功能 | 依赖 |
|---------|------|------|
| cola-component-domain-starter | Spring托管的领域实体组件 | 无 |
| cola-component-catchlog-starter | 异常处理和日志组件 | exception、dto |
| cola-component-test-container | 测试容器组件 | 无 |

---

## 7. 技术特性

### 7.1 CQRS（命令查询职责分离）

COLA在应用层实现了CQRS模式：

**Command（命令）：**
- 处理写操作（新增、修改、删除）
- 由Executor执行
- 通常涉及领域逻辑
- 返回操作结果

**Query（查询）：**
- 处理读操作
- 由Query服务执行
- 可以绕过Domain层直接访问数据库
- 返回数据

**优势：**
- 读写分离，优化性能
- 简单查询不需要复杂的领域模型
- 复杂查询可以直接使用SQL优化

### 7.2 依赖倒置原则

Domain层定义接口（Gateway），Infrastructure层实现接口：

```
┌──────────┐
│  Domain  │ 定义 Gateway 接口
└──────────┘
     ↑
     │ implements
     │
┌──────────┐
│  Infra   │ 实现 GatewayImpl
└──────────┘
```

**优势：**
- Domain层不依赖Infrastructure层
- 技术实现可以随时替换
- 便于单元测试（Mock Gateway）

### 7.3 防腐层（ACL - Anti-Corruption Layer）

Infrastructure层提供防腐层，隔离外部依赖：

```java
// 外部服务接口（第三方）
public interface ExternalPaymentService {
    PaymentResult pay(PaymentRequest request);
}

// 防腐层（在Infrastructure实现）
@Component
public class PaymentTunnel {
    
    @Autowired
    private ExternalPaymentService externalService;
    
    // 转义外部接口，提供领域友好的方法
    public PaymentResult pay(Order order) {
        PaymentRequest request = convertToExternalRequest(order);
        PaymentResult result = externalService.pay(request);
        return convertToDomainResult(result);
    }
}
```

**优势：**
- 隔离外部依赖变化
- 保护领域模型纯洁性
- 便于替换外部服务

### 7.4 扩展点机制

通过@Extension注解实现业务可插拔：

```java
// 不同业务线的实现
@Extension(bizId = "tmall")
public class TmallPriceExtension implements PriceExtPt {
    // 天猫的价格计算逻辑
}

@Extension(bizId = "taobao")
public class TaobaoPriceExtension implements PriceExtPt {
    // 淘宝的价格计算逻辑
}

// 根据bizId自动选择实现
extensionExecutor.execute(PriceExtPt.class, bizScenario, ext -> ext.calculate(order));
```

---

## 8. 最佳实践

### 8.1 领域模型设计

**原则：无必要勿增实体**

```java
// ❌ 过度设计
public class Customer {
    private CustomerId id;           // 值对象
    private CustomerName name;       // 值对象
    private CustomerType type;       // 值对象
    // 太多值对象，增加复杂度
}

// ✅ 简洁设计
public class Customer {
    private String id;
    private String name;
    private CustomerType type;
    
    // 只在需要时添加业务方法
    public void upgrade() {
        if (this.type == CustomerType.NORMAL) {
            this.type = CustomerType.VIP;
        }
    }
}
```

### 8.2 DTO设计

**DTO应该简单纯粹：**

```java
// ✅ 好的DTO设计
@Data
public class CustomerAddCmd extends Command {
    @NotBlank(message = "客户名称不能为空")
    private String name;
    
    @NotNull(message = "客户类型不能为空")
    private CustomerType type;
    
    private String phone;
}

// ❌ 不好的设计（包含业务逻辑）
public class CustomerAddCmd extends Command {
    private String name;
    
    // DTO不应该包含业务逻辑
    public void validate() {
        if (StringUtils.isEmpty(name)) {
            throw new BizException("名称不能为空");
        }
    }
}
```

### 8.3 Gateway接口设计

**Gateway应该面向领域，而非技术：**

```java
// ✅ 面向领域的接口
public interface CustomerGateway {
    Customer getById(String id);
    void save(Customer customer);
    List<Customer> findByType(CustomerType type);
}

// ❌ 面向技术的接口（不推荐）
public interface CustomerGateway {
    CustomerDO selectById(String id);
    int insert(CustomerDO customerDO);
    List<CustomerDO> selectByCondition(Map<String, Object> params);
}
```

### 8.4 异常处理

**区分业务异常和系统异常：**

```java
// 业务异常（预期内，需要友好提示）
if (customer.getBalance().compareTo(amount) < 0) {
    throw new BizException("INSUFFICIENT_BALANCE", "余额不足");
}

// 系统异常（预期外，需要告警）
try {
    result = externalService.call();
} catch (Exception e) {
    throw new SysException("EXTERNAL_SERVICE_ERROR", "外部服务调用失败", e);
}
```

### 8.5 事务处理

**事务应该在Application层控制：**

```java
@Service
public class OrderServiceImpl implements OrderServiceI {
    
    @Autowired
    private OrderCreateCmdExe orderCreateCmdExe;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<String> createOrder(OrderCreateCmd cmd) {
        return orderCreateCmdExe.execute(cmd);
    }
}
```

### 8.6 简单查询优化

**简单查询可以绕过Domain层：**

```java
// 复杂的业务查询，涉及领域逻辑
@Component
public class CustomerDetailQryExe {
    
    @Autowired
    private CustomerGateway customerGateway;
    
    @Autowired
    private OrderGateway orderGateway;
    
    public Response<CustomerDetailDTO> execute(String customerId) {
        // 涉及多个聚合根，需要领域逻辑
        Customer customer = customerGateway.getById(customerId);
        List<Order> orders = orderGateway.findByCustomerId(customerId);
        
        // 领域计算
        BigDecimal totalAmount = customer.calculateTotalAmount(orders);
        
        return Response.buildSuccess(assemble(customer, orders, totalAmount));
    }
}

// 简单的列表查询，直接访问数据库
@Component
public class CustomerListQryExe {
    
    @Autowired
    private CustomerMapper customerMapper;  // 直接使用Mapper
    
    public PageResponse<CustomerDTO> execute(CustomerListQuery qry) {
        // 简单查询，不需要领域逻辑
        List<CustomerDO> list = customerMapper.selectByPage(qry);
        int total = customerMapper.countByCondition(qry);
        
        return PageResponse.buildSuccess(convert(list), total);
    }
}
```

---

## 9. 应用场景

### 9.1 适用场景

COLA架构特别适合以下场景：

1. **复杂业务系统**
   - 业务逻辑复杂，需要清晰的职责划分
   - 多领域交叉，需要良好的模块化设计

2. **中大型团队协作**
   - 多人并行开发
   - 需要标准化的代码结构
   - 需要降低代码冲突

3. **长期维护的项目**
   - 需要良好的可维护性
   - 业务变化频繁
   - 需要支持渐进式重构

4. **微服务架构**
   - 需要明确的服务边界
   - 便于服务拆分
   - 统一的架构风格

5. **中台建设**
   - 通用能力沉淀
   - 扩展点机制支持多业务线
   - 前台灵活组装能力

### 9.2 不适用场景

以下场景不建议使用COLA：

1. **简单CRUD系统**
   - 业务逻辑简单
   - 没有复杂的领域模型
   - 纯数据展示和操作

2. **快速原型开发**
   - 时间紧迫
   - 需求不明确
   - 可能频繁推翻重来

3. **小型团队单人开发**
   - 团队规模小（1-2人）
   - 项目规模小
   - 过度设计反而增加成本

4. **特定技术场景**
   - 批量数据处理（ETL）
   - 高性能计算
   - 实时流处理

### 9.3 实际案例

COLA已被应用于多种业务场景：

- **CRM系统**：客户关系管理
- **电商系统**：订单、库存、支付
- **物流系统**：运输、仓储、配送
- **外卖系统**：订单、配送、结算
- **排课系统**：课程、教师、学生

---

## 10. 版本演进

### 10.1 版本历史

| 版本 | 发布时间 | 主要变化 |
|------|---------|----------|
| 1.0.0 | 2018年 | 初版发布，包含较多概念 |
| 2.0.0 | 2019年 | 简化架构，引入扩展点 |
| 3.0.0 | 2020年 | 进一步简化 |
| 3.1.0 | 2020年 | 重构分包逻辑，按领域分包 |
| 4.0.0 | 2020年 | 大幅简化，回归本质 |
| 5.0.0 | 2024年 | 支持JDK17和SpringBoot 3.x |

### 10.2 COLA 5.0.0新特性

1. **技术栈升级**
   - 支持JDK 17+
   - 支持Spring Boot 3.x
   - 支持Jakarta EE

2. **新增轻量级架构**
   - cola-archetype-light
   - 基于package的轻量级分层
   - 适合中小型项目

3. **增强单元测试支持**
   - cola-component-unittest组件
   - 简化测试编写
   - 支持JUnit 5

4. **增强测试容器**
   - 支持JUnit 5 Extension
   - 更好的集成测试支持

### 10.3 演进趋势

COLA的演进一直遵循"简化"原则：

```
COLA 1.0 → COLA 2.0 → COLA 3.0 → COLA 4.0 → COLA 5.0
   复杂  →    简化  →    再简化 →  回归本质 →  技术升级
```

**核心理念：**
- 奥卡姆剃刀原则（如无必要，勿增实体）
- 实用主义（够用就好，不过度设计）
- 渐进式演进（支持分步实施）

---

## 11. 实施指南

### 11.1 快速开始

#### 步骤1：安装环境

```bash
# 需要的环境
- JDK 17+
- Maven 3.6+
- IDE（推荐IntelliJ IDEA）
```

#### 步骤2：创建项目

```bash
# 创建Web应用
mvn archetype:generate \
    -DgroupId=com.company.demo \
    -DartifactId=demo-web \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.company.demo \
    -DarchetypeArtifactId=cola-framework-archetype-web \
    -DarchetypeGroupId=com.alibaba.cola \
    -DarchetypeVersion=5.0.0

# 创建Service应用（无Web层）
mvn archetype:generate \
    -DgroupId=com.company.demo \
    -DartifactId=demo-service \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.company.demo \
    -DarchetypeArtifactId=cola-framework-archetype-service \
    -DarchetypeGroupId=com.alibaba.cola \
    -DarchetypeVersion=5.0.0
```

#### 步骤3：运行项目

```bash
# 编译安装
cd demo-web
mvn clean install -DskipTests

# 运行应用
cd start
mvn spring-boot:run

# 测试
curl http://localhost:8080/helloworld
```

### 11.2 渐进式实施策略

**阶段1：基础分层**
- 先建立基本的分层结构
- Client、Adapter、App、Domain、Infrastructure
- 暂不使用复杂特性

**阶段2：CQRS分离**
- 引入Command和Query分离
- Executor处理命令
- Query处理查询

**阶段3：领域建模（可选）**
- 根据业务复杂度决定是否引入领域模型
- 简单业务可以使用贫血模型
- 复杂业务使用充血模型

**阶段4：扩展点机制**
- 在需要多业务线差异化时引入
- 使用Extension组件
- 定义BizScenario

**阶段5：其他组件**
- 根据需要引入状态机
- 引入测试组件
- 优化和重构

### 11.3 团队培训要点

1. **架构理念培训**
   - 分层架构的意义
   - 依赖倒置原则
   - 关注点分离

2. **代码规范培训**
   - 包结构规范
   - 命名规范
   - 代码位置规范

3. **最佳实践培训**
   - DTO设计
   - Gateway设计
   - 异常处理
   - 事务管理

4. **实战演练**
   - 完整需求实现
   - Code Review
   - 重构练习

---

## 12. 注意事项与反模式

### 12.1 常见误区

#### 误区1：过度设计

```java
// ❌ 错误：过度抽象
public class CustomerId {
    private String value;
    // 简单的ID不需要封装成值对象
}

public class CustomerName {
    private String value;
    // 简单的名称不需要封装成值对象
}

// ✅ 正确：够用就好
public class Customer {
    private String id;
    private String name;
}
```

#### 误区2：为了DDD而DDD

```java
// ❌ 错误：强行使用聚合根
public class Order {  // 聚合根
    private OrderId id;
    private List<OrderItem> items;  // 实体
    private OrderStatus status;     // 值对象
    private Money totalAmount;      // 值对象
    // 业务简单，不需要这么复杂
}

// ✅ 正确：简单业务简单设计
public class Order {
    private String id;
    private List<OrderItem> items;
    private String status;
    private BigDecimal totalAmount;
}
```

#### 误区3：所有查询都走Domain层

```java
// ❌ 错误：简单查询也走Domain
@Component
public class CustomerListQryExe {
    
    @Autowired
    private CustomerGateway gateway;  // 没必要
    
    public Response<List<CustomerDTO>> execute(CustomerListQuery qry) {
        List<Customer> list = gateway.findByCondition(qry);
        return Response.buildSuccess(convert(list));
    }
}

// ✅ 正确：简单查询直接访问数据库
@Component
public class CustomerListQryExe {
    
    @Autowired
    private CustomerMapper mapper;  // 直接使用
    
    public Response<List<CustomerDTO>> execute(CustomerListQuery qry) {
        List<CustomerDO> list = mapper.selectByPage(qry);
        return Response.buildSuccess(convert(list));
    }
}
```

#### 误区4：强制所有项目使用COLA

- 不是所有项目都适合COLA
- 简单CRUD不需要复杂架构
- 要评估项目规模和复杂度

### 12.2 反模式

#### 反模式1：层级穿透

```java
// ❌ 错误：Adapter直接调用Domain
@RestController
public class CustomerController {
    
    @Autowired
    private CustomerDomainService domainService;  // 越级调用
    
    public Response add(CustomerAddCmd cmd) {
        return domainService.add(cmd);  // 错误
    }
}

// ✅ 正确：通过App层
@RestController
public class CustomerController {
    
    @Autowired
    private CustomerServiceI customerService;  // 调用App层
    
    public Response add(CustomerAddCmd cmd) {
        return customerService.add(cmd);  // 正确
    }
}
```

#### 反模式2：Domain层依赖Infrastructure层

```java
// ❌ 错误：Domain层依赖Infrastructure
public class Customer {
    
    @Autowired
    private CustomerMapper mapper;  // Domain不能依赖Infrastructure
    
    public void save() {
        mapper.insert(this);  // 错误
    }
}

// ✅ 正确：通过Gateway接口
public class Customer {
    // Domain层只定义业务逻辑，不处理持久化
    
    public void validate() {
        // 业务验证
    }
}

// Gateway接口（Domain层定义）
public interface CustomerGateway {
    void save(Customer customer);
}

// 实现（Infrastructure层）
@Component
public class CustomerGatewayImpl implements CustomerGateway {
    
    @Autowired
    private CustomerMapper mapper;
    
    @Override
    public void save(Customer customer) {
        CustomerDO customerDO = convert(customer);
        mapper.insert(customerDO);
    }
}
```

#### 反模式3：业务逻辑在Adapter层

```java
// ❌ 错误：Controller包含业务逻辑
@RestController
public class CustomerController {
    
    @Autowired
    private CustomerMapper mapper;
    
    public Response add(CustomerAddCmd cmd) {
        // 校验逻辑不应该在Controller
        if (StringUtils.isEmpty(cmd.getName())) {
            return Response.buildFailure("名称不能为空");
        }
        
        // 业务逻辑不应该在Controller
        CustomerDO customerDO = new CustomerDO();
        BeanUtils.copyProperties(cmd, customerDO);
        mapper.insert(customerDO);
        
        return Response.buildSuccess();
    }
}

// ✅ 正确：Controller只做路由
@RestController
public class CustomerController {
    
    @Autowired
    private CustomerServiceI customerService;
    
    public Response add(CustomerAddCmd cmd) {
        return customerService.add(cmd);  // 委托给App层
    }
}
```

### 12.3 性能注意事项

1. **避免过度转换**
   - DTO → Domain → DO的转换有成本
   - 简单查询不需要转换
   - 批量操作注意性能

2. **合理使用缓存**
   - 在Gateway层实现缓存
   - 不要在Domain层使用缓存

3. **数据库访问优化**
   - 避免N+1查询
   - 使用批量操作
   - 合理使用分页

### 12.4 测试策略

1. **单元测试**
   - Domain层：测试业务逻辑
   - App层：Mock Gateway
   - Infrastructure层：使用内存数据库

2. **集成测试**
   - 测试完整链路
   - 使用TestContainer

3. **E2E测试**
   - 测试用户场景
   - 使用真实数据库

---

## 附录

### A. Maven依赖示例

```xml
<!-- COLA组件依赖 -->
<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-dto</artifactId>
    <version>5.0.0</version>
</dependency>

<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-exception</artifactId>
    <version>5.0.0</version>
</dependency>

<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-statemachine</artifactId>
    <version>5.0.0</version>
</dependency>

<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-extension-starter</artifactId>
    <version>5.0.0</version>
</dependency>
```

### B. 参考资料

- **GitHub仓库**：https://github.com/alibaba/COLA
- **作者博客**：
  - [COLA 4.0：应用架构的最佳实践](https://blog.csdn.net/significantfrank/article/details/110934799)
  - [COLA 3.1版本说明](https://blog.csdn.net/significantfrank/article/details/109529311)
- **推荐书籍**：《程序员的底层思维》（张建飞 著）

### C. 社区支持

- **GitHub Issues**：https://github.com/alibaba/COLA/issues
- **GitHub Discussions**：https://github.com/alibaba/COLA/discussions

---

## 总结

COLA架构是一套经过实践验证的应用架构最佳实践，它的核心价值在于：

1. **清晰的分层结构**：Adapter、App、Domain、Infrastructure四层职责明确
2. **合理的包设计**：先按领域分包，再按功能分包，实现高内聚低耦合
3. **实用主义**：不过度设计，够用就好
4. **可落地性**：提供脚手架工具和丰富的组件库
5. **渐进式演进**：支持分步实施，不需要一次性重构

COLA不是银弹，它适合复杂业务系统、中大型团队协作、长期维护的项目。对于简单的CRUD系统或快速原型开发，传统的三层架构可能更合适。

**关键原则：**
- 无必要勿增实体
- 分层清晰，职责明确
- 依赖倒置，隔离变化
- 实用主义，避免过度设计

**实施建议：**
- 从基础分层开始
- 渐进式引入特性
- 根据团队能力和项目需求调整
- 持续重构和优化

希望这份文档能够帮助您深入理解COLA架构，并在实际项目中成功应用！

---

**文档版本历史：**
- v1.0 - 2026-01-27 - 初始版本

**维护者：** 基于COLA开源项目整理
**联系方式：** https://github.com/alibaba/COLA

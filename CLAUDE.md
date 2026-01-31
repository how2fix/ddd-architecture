# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Domain-Driven Design (DDD) architecture reference project** that demonstrates clean architecture principles based on COLA (Clean Object-Oriented and Layered Architecture) from Alibaba. It serves as an architectural template with detailed documentation in Mermaid diagrams - not an implementation with actual source code.

## Architecture Documentation

The project contains three comprehensive Mermaid diagrams in the `docs/` folder:

- **ddd-architecture-overview.mermaid** - Overall architecture flow with external systems, layer responsibilities, and data transformation
- **ddd-architecture-diagram.mermaid** - UML class diagram showing relationships between all components
- **ddd-architecture-with-notes.mermaid** - Detailed implementation view with design principles and best practices

Additionally, **COLA_Architecture_Design_Document(1).md** contains complete architectural guidance including layer responsibilities, package structure, components, best practices, and anti-patterns.

## Core Architecture: 5 Layers

### 1. Adapter Layer (适配层)
- **Purpose**: Protocol conversion for HTTP/MQ/Scheduled tasks
- **Components**: Controllers, EventListeners, Jobs
- **Principle**: Isolate external protocols, validate parameters, thin layer with no business logic

### 2. Client Layer (对外契约)
- **Purpose**: Define external API contracts
- **Components**: Service interfaces, DTOs, Commands (write), Queries (read)
- **Pattern**: CQRS (Command Query Responsibility Segregation)

### 3. Application Layer (应用层)
- **Purpose**: Business process orchestration
- **Components**: Application services, Command executors, Query executors, Assemblers
- **Principle**: No business logic, orchestrates domain objects, controls transaction boundaries with @Transactional

### 4. Domain Layer (领域层) - THE CORE
- **Purpose**: Encapsulate all business rules and logic
- **Components**: Aggregate roots, Value objects, Domain services, Gateway interfaces (ACL), Repository interfaces
- **Principle**: Rich domain model, technology-agnostic, interface-driven, defines contracts for Infrastructure to implement

### 5. Infrastructure Layer (基础设施层)
- **Purpose**: Technical implementation and external integration
- **Components**: Repository implementations, Mappers, Persistence objects (PO), Converters, Gateway implementations
- **Principle**: Implements Domain layer interfaces (Dependency Inversion), handles DB/cache/MQ/third-party services

## Key Design Principles

### Dependency Inversion (DIP)
- Domain layer defines interfaces (`IUserRepository`, `INotificationGateway`)
- Infrastructure layer implements these interfaces
- Dependency direction: Infrastructure → Domain (not the other way)

### Separation of Concerns
- Domain: **What** (business rules)
- Application: **When** (business flow/orchestration)
- Infrastructure: **How** (technical implementation)

### Rich Domain Model vs Anemic Model
- Prefer: `user.register()` (business logic in entity)
- Avoid: `userService.register(user)` (anemic service with only data holders)

### Anti-Corruption Layer (ACL)
- Gateway interfaces isolate third-party dependencies
- Repository interfaces isolate database concerns
- Changes to external services don't affect Domain layer

## Request Flow

```
HTTP/MQ/Schedule → Adapter (protocol conversion)
                → Application (orchestration, @Transactional)
                → Domain (business rules)
                → Infrastructure (persistence/external calls)
                → Response through layers as DTO
```

## Development Patterns

- **CQRS**: Separate Command (write via Executor) and Query (read via Query Executor) objects
- **Aggregate Root**: Consistency boundary (e.g., User aggregate)
- **Value Objects**: Immutable objects with validation (e.g., Email)
- **Domain Services**: Cross-aggregate business logic
- **Extension Points**: @Extension(bizId) for multi-tenant/business-line variations

## Package Structure (COLA Convention)

```
com.company.project/
├── customer/              # Domain-first packaging
│   ├── controller/        # Adapter layer
│   ├── executor/          # App layer (Command)
│   ├── query/             # App layer (Query)
│   ├── ability/           # Domain services
│   └── gateway/           # Gateway interfaces
└── order/                 # Another bounded context
    └── (same structure)
```

## Common Pitfalls to Avoid

1. **Layer violation**: Adapter should never directly call Domain - always go through Application
2. **Domain depending on Infrastructure**: Domain defines interfaces, Infrastructure implements them
3. **Business logic in Adapter/Controller**: Controllers should be thin - just routing
4. **Over-design**: "No necessity, don't add entities" - simple CRUD doesn't need full DDD
5. **All queries through Domain**: Simple queries can bypass Domain and access Infrastructure directly (COLA pragmatic approach)

# Agents.md

本文件为 AI 编码助手（Codex / Claude Code / Cursor / Copilot 等）提供仓库级指南。改动代码前请先阅读，尤其注意"安全红线"与"已知问题"两节。

## 项目概述

"one" 是一个自研的基础开发平台（单体架构，预留微服务拆分），提供后台管理端（sys）、平台端（plt）、用户端（web）三类 API，并集成 OAuth2 认证授权、多租户、多数据源、缓存、消息（RabbitMQ / WebSocket / SSE）、对象存储、Elasticsearch、Quartz 定时任务、Spring AI 多厂商模型接入，以及微信 / 钉钉 / 飞书 / 短信 / 翻译等开放平台能力。

## 技术栈

- Java 25（`sourceCompatibility` / `targetCompatibility` = 25，编译参数 `-parameters`）
- Gradle 9.7（wrapper；依赖版本集中在 `gradle/libs.versions.toml`；构建约定在 `buildSrc`）
- Spring Boot 4.1、Spring Security（Authorization Server + Resource Server）、Spring AI 2.0、Spring Cloud（预留）
- 持久化：MyBatis-Plus 3.5（主）、JPA/Hibernate（遗留与示例）、PostgreSQL
- 中间件：Redis（Redisson）、RabbitMQ、Elasticsearch、Quartz
- 其他：CosId（雪花 ID）、Hutool、MapStruct、Lombok、springdoc-openapi、JavaCV/FFmpeg、GraalVM Native（实验）

## 常用命令

Windows 使用 `.\gradlew.bat`，Linux/macOS 使用 `./gradlew`。

- 全量构建：`./gradlew clean bootJar`（产出 `platform-services/*/build/libs/*.jar`）
- 单独构建主服务：`./gradlew :platform-services:app-server:bootJar`
- 运行全部测试：`./gradlew test`
- 运行测试宿主模块测试：`./gradlew :platform-commons:commons-webapp:test`
- 本地启动主服务：`./gradlew :platform-services:app-server:bootRun`（或 IDE 运行 `AppServerApplication`；注意该 main 方法非 public，见"已知问题"）
- 初始化本地数据库：`tools/database/bin/pgsql_init.cmd`（Windows）或 `pgsql_init.sh`（Linux）；SQL 脚本位于 `tools/database/pgsql/`，新建表必须同步追加到 `db_pgsql_schema_core.sql`
- CI 产物复制脚本：`tools/scripts/build.sh` / `build.cmd`

## 运行配置（Profile）

- 默认 profile：`local`（见 `platform-services/app-server/src/main/resources/application.yml` 的 `spring.profiles.active`）
- `application-local.yml`、`application-production.yml` 已被 gitignore（本地/生产私有配置，不入库，不要提交）
- `application-development.yml`、`application-native.yml` 已入库
- 服务端口：app-server `8181`、admin-server `8282`（Spring Boot Admin）
- 所有平台能力开关集中在 `platform.*` 配置树（如 `platform.tenancy.enabled`、`platform.ai.enabled`、`platform.data.mybatis.enabled`）

## 模块地图

```
one (Gradle root)
├── platform-commons
│   ├── commons-core          平台核心库：R/异常/枚举/工具、数据层抽象（MyBatis/JPA/ES）、缓存、多租户、
│   │                         日志、消息（Rabbit/WebSocket/SSE）、存储、序列、扩展（验证码/IP/敏感词/HTTP）、
│   │                         AI 抽象（factory/service）、开放平台 SDK（微信/钉钉/飞书/短信/翻译）
│   ├── commons-core-starter  commons-core 的 Spring Boot 自动装配层：39 个 AutoConfiguration +
│   │                         44 个 @ConfigurationProperties，按 platform.*.enabled 开关启用
│   ├── commons-parser        JavaCV / FFmpeg / Tesseract 原生依赖封装（多平台 classifier）
│   ├── commons-console       Spring Shell 控制台应用（console.jar）
│   ├── commons-native        GraalVM Native Image 验证应用（native.jar）
│   └── commons-webapp        开发/测试宿主：JPA、MyBatis-Plus、ES、AI/AgentScope 示例；全部测试挂在此模块
├── platform-modules
│   ├── commons/commons-api   空聚合模块（仅转出 system-api，无源码）
│   ├── commons/commons-starter 平台级 starter：安全默认配置（JwtAuthenticationConverter、
│   │                         BearerTokenResolver）、HttpExchange 客户端代理、MessageSource、默认租户存储
│   └── system
│       ├── system-api        系统域 API 模块：全部业务域的 @HttpExchange 接口（XxxApi）、
│       │                     DTO/Form/Request/VO/BO、枚举、常量；含 CustomJwtGrantedAuthoritiesConverter
│       ├── system-impl       系统域实现模块：Controller、Service/Impl、Repository（MyBatis-Plus +
│       │                     XML Mapper）、XxxApiImpl、Entity/Converter
│       └── system-security   OAuth2 授权服务器 + 资源服务器、自定义认证（密码/OTP/社交）、JWT、验证码过滤器
└── platform-services
    ├── admin-server          Spring Boot Admin 服务端（8282）
    └── app-server            主业务服务（8181）：聚合 commons-starter + system-impl + system-security
```

### 业务域（system 模块内）

`ai`（模型/工具/MCP/知识库/Agent）、`catalog`、`commons`（验证码/首页/仪表盘）、`config`、`core`（用户/角色/权限/租户/登录会话）、`dev`、`dict`、`i18n`、`im`、`job`（Quartz）、`log`、`message`、`open`（微信/钉钉/飞书）、`region`、`security`（OAuth2 客户端/授权/AppKey）、`site`（公告/横幅/友链）、`storage`（附件）、`tag`。

### 控制器分类与 URL 约定

| 分类 | URL 前缀 | 说明 |
|---|---|---|
| `*SysController` | `/api/v1/sys/**` | 后台管理端 |
| `*PltController` | `/api/v1/plt/**` | 平台端（运维/开发者） |
| `*WebController` | `/api/v1/web/**` | 用户端 |
| `*ExchangeController` | `/exchange/**` | 内部服务间调用（微服务预留） |
| MCP Server | `/api/mcp` | Spring AI MCP |

## 代码约定（必须遵守）

### 分层与包结构

每个业务域按固定分层组织，新增业务功能必须沿用：

`api`（XxxApi 接口）→ `controller` → `domain`（entity / converter / dto / form / request / vo / bo）→ `repository`（Mapper + XML）→ `service` / `service.impl`

- 接口层 `XxxApi`：Spring `@HttpExchange` 注解接口，URL 前缀 `/exchange/xxx`
- 实现层 `XxxApiImpl`：`@Service` 本地实现，被同进程 controller 与安全模块直接注入
- `XxxExchangeController`：将 `XxxApi` 以 HTTP 暴露给其他服务（当前单体中已有，注意安全红线）
- 对象转换使用 MapStruct：`XxxConverter.INSTANCE`，禁止手写 getter/setter 拷贝
- 实体继承 `AbstractEntity` / `BaseEntity` / `BaseTenantEntity`（自动获得审计字段、乐观锁 `version`、软删除 `deleted_at`）
- 服务继承 `BaseEntityService` 或 `BaseCachingEntityService`（后者写操作自动失效缓存）
- 包名一律 `cc.wdev.platform.*`；模块间只能通过 Gradle project 依赖引用，禁止跨模块直接使用未导出的类

### 响应与异常

- 所有接口返回统一响应体 `R<T>`（`code` / `message` / `data`），成功用 `R.success(data)`
- 业务错误抛 `ServiceException(ResponseCodeEnum.XXX)`，不要用 `R.error()` 作为业务分支的返回值
- 参数校验使用 `jakarta.validation`（`@Valid` + 注解），不要手写 null 判断链
- 分页：请求对象携带 `PageRequest`（`getPageable()`），返回 Spring Data `Page`；用 `MyBatisPlusUtils` 在 `IPage` 与 `Page` 间转换
- 多语言文案通过 i18n 标签表 / `messages_*.properties` 提供，不要硬编码中文文案到响应

### 安全（红线，改动前必读）

- 安全链是"默认放行"（`anyRequest().permitAll()`），没有全局兜底拒绝，安全完全依赖方法级注解。**新增任何端点必须显式加 `@PreAuthorize("hasAnyAuthority('...')")` 或 `@Authenticated`**；只有确需匿名的端点才使用 `@Anonymous` / `@PermitAll`
- 权限字符串格式示例：`system:user`、`system:role`、`system:config`、`dev:ai:config:model`；角色前缀 `ROLE_`，数据范围前缀 `DATA_SCOPE_`
- `/exchange/**` 目前无鉴权且会返回敏感数据（用户密码哈希、OAuth2 客户端密钥、系统配置）。**禁止向 exchange 接口新增更敏感的数据**；新增内部接口前先与维护者确认鉴权方案
- 租户上下文 `TenantContext` 取自客户端请求头 `x-tenant-id` 且未被校验，服务层不能信任它代表"当前用户所属租户"；涉及租户归属的数据应以认证用户（`SecurityUtils`）为准
- 密码必须经 `SecurityUtils.encode()`（BCrypt）后落库；禁止明文存储
- 禁止把异常消息（`e.getLocalizedMessage()`）直接回给客户端；不要向日志写入密码、token、密钥
- 敏感接口（登录、验证码等）注意验证码/限流机制（`@RateLimiter`、`CaptchaAuthenticationFilter`），不要绕过

### 数据层

- SQL 一律参数化：XML Mapper 用 `#{}`，**禁止 `${}` 字符串拼接**
- 新表 SQL 追加到 `tools/database/pgsql/db_pgsql_schema_core.sql`，遵循现有规范：`id BIGSERIAL` 主键、`version`、`active`、`created_by/created_at/updated_by/updated_at/deleted_by/deleted_at` 审计列、字段与表 COMMENT、必要的索引
- 多租户表继承 `BaseTenantEntity`；确需绕过租户过滤的查询用 `@InterceptorIgnore(tenantLine = "true")`（必须清楚后果）
- 删除一律软删除（`softDelete*`），禁止物理 `DELETE`
- 乐观锁 `version` 由 MyBatis-Plus 自动处理，不要手动覆盖
- 列表查询必须分页或有上限，禁止无界全表 `.list()`

### 缓存与性能

- 实体级缓存：继承 `BaseCachingEntityService` 并实现 `getCacheKeyGenerator()`，写操作自动失效缓存
- 限流：`@RateLimiter`（基于 Redisson）
- 权限相关查询（`getUserAuthority`）目前每个请求都会查库，新增代码不要继续放大该模式

### 测试

- 测试写在 `platform-commons/commons-webapp/src/test`，命名 `*Tests`，JUnit 5
- 现有测试依赖真实 PostgreSQL/Redis/ES 且 `@Rollback(false)`，会污染本地库；**新测试优先使用 mock / 内存库（如 H2），不要依赖外部服务**
- CI（`.github/workflows/build.yml`）目前只执行 `clean bootJar`，不运行测试——改动公共层后请本地手动运行相关测试

## 构建与依赖

- 新依赖优先在 `gradle/libs.versions.toml` 的 `[versions]` / `[libraries]` / `[bundles]` 中声明，不要在 `build.gradle.kts` 里裸写坐标
- 版本强制覆盖集中在 `buildSrc/src/main/kotlin/java-conventions.gradle.kts`，不要散落覆盖
- 模块依赖必须通过 Gradle `project(...)` 引用；`commons-core` 对外能力多为 `compileOnly`（由 starter/应用模块提供运行时依赖），新增可选能力沿用该模式

## 已知问题（2026-08 代码分析结论，改动相关代码前先确认是否已修复）

- `/exchange/**` 匿名可访问（严重，整改中）：`UserExchangeController` 等 10 个 exchange 控制器无鉴权
- 租户上下文信任请求头 `x-tenant-id` 且未与 JWT `tid` 校验（严重）
- `NoticeSysController`（`/api/v1/sys/notice/*`）无任何权限注解
- `AppServerApplication.main` 非 public（包私有），IDE / `bootRun` 直接启动可能失败，生产以 `bootJar` 打包运行为主
- `NettyWebSocketServer.destroy()` 关闭条件写反（`isShutdown()` 应为 `!isShutdown()`），事件循环线程组不会优雅关闭
- 全局异常处理把内部异常消息回给客户端且统一返回 HTTP 400
- `spring.main.allow-circular-references` 与 `allow-bean-definition-overriding` 同时开启（历史包袱；请勿新增循环依赖或同名 Bean 定义）
- `NoticeServiceImpl.findNoticeByPage` 存在 try-catch 兜底查询，错误时静默改变数据范围
- 开发 profile 中数据源/中间件口令为硬编码（仅限本地开发环境，禁止带入生产配置）

## 禁止事项

- 不要提交真实密钥/口令；配置只允许占位或 `${ENV_VAR}` 引用
- 不要修改或提交 `application-local.yml`、`application-production.yml`（已 gitignore，属本地/生产私有配置）
- 不要删除 `tools/deploy/binaries/` 下的本地部署产物（目录已 gitignore，仅存 `.gitkeep`）
- 不要使用 `System.out.println` / `printStackTrace`（测试辅助除外）
- 不要在业务代码中写 `Thread.sleep`、空循环等待
- 不要通过 `apply_patch` 之外的方式（如 shell 重定向）改写代码文件

## 新增业务功能的推荐步骤

1. 在 `system-api` 中定义 `XxxApi`（`@HttpExchange`）与 DTO/Form/Request/VO
2. 在 `system-impl` 中实现 `XxxApiImpl`（`@Service`）与 `XxxService` / `XxxServiceImpl`，实体继承 `BaseEntity`/`BaseTenantEntity`，转换用 MapStruct
3. 若涉及持久化，新增 `XxxRepository`（MyBatis-Plus）并同步补充 SQL 脚本与 XML（如需）
4. 新增 `XxxSysController` / `XxxWebController`，**每个端点必须加 `@PreAuthorize` 或 `@Authenticated`**
5. 涉及用户可见操作时加 `@OperationLog`；列表接口分页并返回 `R<Page<...>>`
6. 在 `commons-webapp` 补充测试（优先不依赖外部中间件）

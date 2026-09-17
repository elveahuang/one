# Agents.md

本文件是仓库的长期架构索引与协作约定，供 AI 编码助手直接复用。修改前先读“安全”与“禁止事项”，再按任务定位相关源码；无需每次重新扫描全仓。

> 最近静态核实：2026-09-17，基线提交 `5c10778`。文档描述源码事实，不代表服务已启动、测试已通过或部署环境已验证。源码与本文冲突时以源码为准，并随相关改动更新本文。

## 使用与维护方式

- 开始任务先看 `git status --short`，保留用户现有修改和未跟踪文件，不将它们误认为自己的产出。
- 默认只读本文和任务涉及的模块、配置、调用方及测试；跨模块调整时再沿依赖扩展阅读。
- 新增入口、修改配置键、调整协议或修复已知问题时同步更新对应章节，避免重复全仓分析。
- 记录稳定的文件路径与类/方法名，不依赖易漂移的行号、类数量、完整依赖版本清单。
- 明确区分“已实现”“示例/依赖接入”“待验证”；不要把历史缺陷当作新代码模板。
- 本地配置、构建产物和未提交测试不能作为已提交仓库能力的唯一依据。

## 项目概述

"one" 是一个自研的基础开发平台（单体架构，预留微服务拆分），提供后台管理端（sys）、平台端（plt）、用户端（web）三类 API，并集成 OAuth2
认证授权、多租户、多数据源、缓存、消息（RabbitMQ / WebSocket / SSE）、对象存储、Elasticsearch、Quartz 定时任务、Spring AI 多厂商模型接入，以及微信 / 钉钉 / 飞书 / 短信 /
翻译等开放平台能力。

## 技术栈

- Java 25（`sourceCompatibility` / `targetCompatibility` = 25，编译参数 `-parameters`）
- Gradle 9.7.1（wrapper；依赖版本集中在 `gradle/libs.versions.toml`；构建约定在 `buildSrc`）
- Spring Boot 4.1、Spring Security（Authorization Server + Resource Server）、Spring AI 2.0、Spring Cloud（预留）
- 持久化：MyBatis-Plus 3.5（主）、JPA/Hibernate（遗留与示例）、PostgreSQL
- 中间件：Redis（Redisson）、RabbitMQ、Elasticsearch、Quartz
- 其他：CosId（雪花 ID）、Hutool、MapStruct、Lombok、springdoc-openapi、JavaCV/FFmpeg、GraalVM Native（实验）

## 常用命令

Windows 使用 `.\gradlew.bat`，Linux/macOS 使用 `./gradlew`。

- 全量构建：`./gradlew clean bootJar`（产出 `platform-services/*/build/libs/*.jar`）
- 单独构建主服务：`./gradlew :platform-services:app-server:bootJar`
- 全量测试：`./gradlew test`（约定排除 `integration` 标签，但当前无测试打该标签，实际会执行依赖外部中间件的 SpringBootTest，谨慎运行，详见「测试」节）
- 运行测试宿主模块测试：`./gradlew :platform-commons:commons-webapp:test`
- 本地启动主服务：`./gradlew :platform-services:app-server:bootRun`（或 IDE 运行 `AppServerApplication`；使用 JDK 25，非 public 的 main 在 Java 25 下是合法入口，不据此判断启动失败）
- 初始化本地数据库：`tools/database/bin/pgsql_init.cmd`（Windows）或 `pgsql_init.sh`（Linux）。**警告：脚本链含 `DROP DATABASE` / `DROP TABLE` / `TRUNCATE`，重跑会整库清空，仅用于本地初始化**；SQL 脚本位于 `tools/database/pgsql/`，新建表必须同步追加到 `db_pgsql_schema_core.sql`
- CI 产物复制脚本：`tools/scripts/build.sh` / `build.cmd`

## 运行配置（Profile）

- 默认 profile：`local`（见 `platform-services/app-server/src/main/resources/application.yml` 的 `spring.profiles.active`）
- `application-local.yml`、`application-production.yml` 已被 gitignore（本地/生产私有配置，不入库，不要提交）
- `application-development.yml`、`application-native.yml` 已入库
- 服务端口：app-server `8181`、admin-server `8282`（Spring Boot Admin）、commons-webapp `8080`（测试宿主）；全部应用默认 `profiles.active: local`；app-server 显式关闭 Consul
- 所有平台能力开关集中在 `platform.*` 配置树，注意默认值分两档：
  - 默认开（`matchIfMissing=true`）：`platform.async/cache/jwt/log/mail/sequence/storage/template/web`、`platform.data.mybatis`、`platform.captcha/http/keyword/sensitive`、`platform.message.rabbit/broadcast`、`platform.websocket`、`platform.ai`
  - 默认关（需显式 `enabled=true`）：`platform.data.core/datasource/jpa/elasticsearch`、`platform.jdbc`、`platform.swagger`、`platform.ip/selenium/parser`、全部 `platform.oapis.*`、`platform.sms/translator/face-body`、`platform.test`；`platform.tenancy.enabled` 默认关（多租户拦截器需显式开启）
  - Quartz 无 `platform.*` 自定义开关，走标准 `spring.quartz.*`（job 域可配独立数据源 `spring.datasource.job`）

## 模块地图

```
one (Gradle root)
├── platform-commons
│   ├── commons-core          平台核心库：R/异常/枚举/工具、数据层抽象（MyBatis/JPA/ES）、缓存、多租户、
│   │                         日志、消息（Rabbit/WebSocket/SSE）、存储、序列、扩展（验证码/IP/敏感词/HTTP）、
│   │                         AI 抽象（factory/service）、开放平台 SDK（微信/钉钉/飞书/短信/翻译）
│   ├── commons-core-starter  commons-core 的 Spring Boot 自动装配层：自动配置注册表与属性类按能力组织；
│   │                         实际是否启用还取决于 classpath、条件注解和用户提供的 Bean
│   ├── commons-javacv        JavaCV / FFmpeg / Tesseract 原生依赖封装（多平台 classifier）
│   ├── commons-console       Spring Shell 控制台应用（console.jar）
│   ├── commons-native        GraalVM Native Image 验证应用（native.jar）
│   └── commons-webapp        开发/测试宿主：JPA、MyBatis-Plus、ES、AI 示例；主要测试宿主（app-server / console 也有少量测试）
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

`ai`（模型/工具/MCP/知识库/Agent）、`catalog`、`commons`（验证码/首页/仪表盘）、`config`、`core`（用户/角色/权限/租户/登录会话）、`dev`、`dict`、`i18n`、`im`、`job`（Quartz）、
`log`、`message`、`open`（微信/钉钉/飞书）、`region`、`security`（OAuth2 客户端/授权/AppKey）、`site`（公告/横幅/友链）、`storage`（附件）、`tag`。

### 控制器分类与 URL 约定

| 分类                  | URL 前缀         | 说明                         |
|-----------------------|------------------|------------------------------|
| `*SysController`      | `/api/v1/sys/**` | 后台管理端                   |
| `*PltController`      | `/api/v1/plt/**` | 平台端（运维/开发者）        |
| `*WebController`      | `/api/v1/web/**` | 用户端                       |
| `*ExchangeController` | `/exchange/**`   | 内部服务间调用（微服务预留） |
| MCP Server            | `/api/mcp`       | Spring AI MCP                |

## 代码约定（必须遵守）

### 分层与包结构

每个业务域按固定分层组织，新增业务功能必须沿用：

`api`（XxxApi 接口）→ `controller` → `domain`（entity / converter / dto / form / request / vo / bo）→ `repository`（Mapper + XML）→ `service` / `service.impl`

- 接口层 `XxxApi`：Spring `@HttpExchange` 注解接口，URL 前缀 `/exchange/xxx`
- 实现层 `XxxApiImpl`：`@Service` 本地实现，被同进程 controller 与安全模块直接注入
- `XxxExchangeController`：将 `XxxApi` 以 HTTP 暴露给其他服务（当前单体中已有，注意安全红线）
- 对象转换使用 MapStruct：`XxxConverter.INSTANCE`，禁止手写 getter/setter 拷贝
- 实体按需继承（`commons-core` 的 MyBatis 体系）：`AbstractEntity` 仅含 `id`；`BaseEntity` 增加 `version`、`active` 与审计字段（`created_by/created_at/updated_by/updated_at/deleted_by/deleted_at`）；`BaseTenantEntity` 再增加租户字段。需要租户隔离的表才用 `BaseTenantEntity`
- 基类未使用 `@TableLogic`，查询不会自动过滤已软删数据；删除一律走服务层 `softDelete*`（显式更新状态与删除审计），禁止物理 `DELETE`
- 服务继承 `BaseEntityService` 或 `BaseCachingEntityService`；后者只覆盖标准保存/更新/删除入口的缓存失效，自定义 Mapper 写路径需自行审查缓存一致性
- 包名一律 `cc.wdev.platform.*`；模块间只能通过 Gradle project 依赖引用，禁止跨模块直接使用未导出的类

### 响应与异常

- 所有接口返回统一响应体 `R<T>`（`code` / `message` / `data`），成功用 `R.success(data)`
- 业务错误抛 `ServiceException(ResponseCodeEnum.XXX)`，不要用 `R.error()` 作为业务分支的返回值
- 参数校验使用 `jakarta.validation`（`@Valid` + 注解），不要手写 null 判断链
- 分页：请求对象携带 `PageRequest`（`getPageable()`），返回 Spring Data `Page`；用 `MyBatisPlusUtils` 在 `IPage` 与 `Page` 间转换
- 多语言文案通过 i18n 标签表 / `messages_*.properties` 提供，不要硬编码中文文案到响应

### 安全（红线，改动前必读）

- 安全链分三层（`system-security` 的 `WebSecurityConfiguration`）：`/api/mcp` 要求 API Key 鉴权（`mcpServerApiKey` + `CustomApiKeyService`）；`/api/**`（`apiSecurityFilterChain`，Order 2）在白名单 `GLOBAL_API_EXCLUDE_URLS` 之外是 `anyRequest().permitAll()`，JWT 解码不改变放行结果；其余请求（Order 3）同为默认放行。授权服务器自身链（`AuthorizationServerConfiguration`）要求 `authenticated()`。
- 因此常规业务 API 依然依赖方法级注解。**新增任何端点必须显式加 `@PreAuthorize("hasAnyAuthority('...')")` 或 `@Authenticated`**；只有确需匿名的端点才使用 `@Anonymous` / `@PermitAll`
- 权限字符串格式示例：`system:user`、`system:role`、`system:config`、`dev:ai:config:model`；角色前缀 `ROLE_`，数据范围前缀 `DATA_SCOPE_`
- `/exchange/**` 的 10 个 `*ExchangeController`（`UserExchangeController`、`ClientExchangeController` 等）没有方法级鉴权注解，且 `/exchange/**` 不在 `/api/**` 安全链匹配范围内。 **禁止向 exchange 接口新增更敏感的数据**；新增内部接口前先与维护者确认鉴权方案
- 租户上下文 `TenantContext` 基于 MDC：Servlet 过滤器按请求头 `x-tenant-id` / `x-tenant-code` / `x-tenant-root-ind`（`GlobalConstants`）或 resolver（ID/code/域名）初始化，普通请求在 finally 中清理；但请求头未与 JWT `tid` 做一致性校验（含根租户标识）。服务层不能信任请求头即"当前用户所属租户"；涉及租户归属的数据应以认证用户为准，注意 `SecurityUtils#getTid` 存在回退到 `TenantContext` 的路径，不能仅凭它完成归属校验
- 密码必须经 `SecurityUtils.encode()`（BCrypt）后落库；禁止明文存储
- 禁止把异常消息（`e.getLocalizedMessage()`）直接回给客户端；不要向日志写入密码、token、密钥
- 敏感接口（登录、验证码等）注意验证码/限流机制（`@RateLimiter`、`CaptchaAuthenticationFilter`），不要绕过

### 数据层

- SQL 一律参数化：XML Mapper 用 `#{}`， **禁止 `${}` 字符串拼接**
- 新表 SQL 追加到 `tools/database/pgsql/db_pgsql_schema_core.sql`，遵循现有规范：`id BIGSERIAL` 主键、`version`、`active`、
  `created_by/created_at/updated_by/updated_at/deleted_by/deleted_at` 审计列、字段与表 COMMENT、必要的索引
- 多租户表继承 `BaseTenantEntity`；确需绕过租户过滤的查询用 `@InterceptorIgnore(tenantLine = "true")`（必须清楚后果）
- 删除一律软删除（`softDelete*`），禁止物理 `DELETE`
- 乐观锁 `version` 由 MyBatis-Plus 自动处理，不要手动覆盖
- 列表查询必须分页或有上限，禁止无界全表 `.list()`

### 缓存与性能

- 实体级缓存：继承 `BaseCachingEntityService` 并实现 `getCacheKeyGenerator()`，写操作自动失效缓存
- 限流：`@RateLimiter`（基于 Redisson）
- JWT 权限转换（`CustomJwtGrantedAuthoritiesConverter`）按 uid 调用 `UserApi#getUserAuthority` 查库组装权限码/`ROLE_`/`DATA_SCOPE_`，即每次需要权限转换的请求都会走权限查询，新增代码不要继续放大该模式；两个同名 converter Bean（`PlatformSecurityConfiguration` 与 `CommonSecurityConfiguration`）改动时需同时检查

### 测试

- 测试集中在三处：`platform-commons/commons-webapp/src/test`（主宿主：公共层、AI 示例等）、`platform-services/app-server/src/test`（含 9 个 API 级测试，如 `BizTypeApiTests`、`AttachmentApiTests`）、`platform-commons/commons-console`；命名 `*Tests`，JUnit 5
- 构建约定排除 `integration` 标签，但当前仓库没有任何测试打该标签——即 `./gradlew test` 会实际执行含外部中间件依赖的 SpringBootTest；基类（`webapp` 的 `BaseTests`/`BaseWebTests`）为 `@Transactional + @Rollback(false)`，会污染本地库
- **新测试优先使用 mock / 内存库（如 H2），不要依赖外部服务**
- CI（`.github/workflows/build.yml`）目前只执行 `clean bootJar` 并拷贝 `app.jar`/`admin.jar` 到 `dist`，不运行测试——改动公共层后请本地手动运行相关测试

## AI 模块架构与响应模式

### 1. 技术实现与模型供应商分离 (Service Provider vs Model Provider)

平台采用正交解耦的双层抽象，彻底分离"技术实现（底层协议/SDK）"与"模型供应商（厂商实体/凭证画像）"：

- **技术实现方案（`AiServiceProvider`）**：定义底层通信协议与技术引擎。
    - 核心值：`SPRING_AI_DEEPSEEK`（factory chat 缺省引擎）、`SPRING_AI_OPENAI`、`SPRING_AI_ANTHROPIC`、`AGENTIC_SPRING_AI_DASHSCOPE`（speech/transcription 缺省）、`ALIYUN_DASHSCOPE_SDK`（service 轨非 chat 缺省）、`TENCENT_HUNYUAN_SDK`、`OPENAI_SDK`（service chat 缺省）、`CUSTOM`。
    - 职责：关注网络通信、协议封包（OpenAI 兼容协议 / Anthropic Messages API / 厂商原生 RPC）、SSE Token 流式解析与 Spring AI Advisor/Tool 适配。
- **模型供应商（`AiModelProvider`）**：定义厂商实体、凭证与模型能力画像。
    - 核心值：`OPENAI`、`ANTHROPIC`、`DEEPSEEK`、`ALIYUN`、`TENCENT`、`ORCAROUTER`。
    - 职责：关注商业身份、`apiKey`、`baseUrl`、模型清单（`Model` record 支持深度思考、联网搜索等）及能力类型矩阵 `AiModelType`（`TEXT`、`EMBEDDING`、`IMAGE`、`AUDIO_TRANSCRIPTION`、`AUDIO_SPEECH`、`RERANK`）；模型名按"精确名或 `前缀-` 前缀"匹配。
- **架构解耦价值**：
    - **M:N 协议复用**：所有兼容 OpenAI 规范的厂商（DeepSeek、OrcaRouter、Aliyun Compatible 等）统一复用 `OpenAiChatModelFactory`，无需为每个厂商重复编写客户端。
    - **双轨调用体系**：`AiManager` + `factory/*ModelFactory`（面向 Spring AI 生态，产出 `ChatModel`/`ChatClient`，支持记忆/RAG/日志/Tool/Advisor 链）与 `AiServiceManager` + `service/*ModelService`（面向原生 SDK 同步直连）；`AiManager` 按"无参配置 → 按引擎枚举 → 按 ModelConfig + supports 校验"三级解析，`platform.ai.fallback-enabled=true` 时未命中回落默认引擎，否则抛异常。**模型实例无缓存，每次 `getModel(config)` 新建客户端**。
    - **扁平化配置规范**：配置项 `platform.ai.factory.*-service-provider` / `platform.ai.service.*-service-provider`（选引擎）与 `platform.ai.providers.<id>.*`（配厂商凭证，key 统一小写）完全解耦。
- **接入入口**：演示宿主 `commons-webapp` 的 `ChatController`（`/chat/text`、`/chat/stream`，`@Anonymous`，仅测试环境）；正式用户端为 system 域 `AiChatWebController`（`@Authenticated`，`/api/v1/web/ai/chat/*`），经 `AiChatApiImpl` 按 `AiChatType`（CHAT/AGENT/KB/STATIC）取 `ChatClient`；业务模型实体经 `AiHelper` 转为 `SimpleModelConfig` 复用双轨。

### 2. AiResponseType 三种响应模式与执行链路

以 `platform-commons/commons-webapp` 中的 `ChatController`（`/chat/text`、`/chat/stream`）与 `AiServiceImpl` 为演示接入入口（正式端点为 system 域 `AiChatWebController`），通过 `AiResponseType` 驱动不同的提示词约束与数据处理管道（链路实现在 `commons-core` 的 `utils/AiUtils`）：

| 响应类型                   | 约束机制                                                                                                | 同步处理 (`chatText`)                                                              | 流式处理 (`chatStream`)                                                                                                                              | 典型场景                                                       |
|----------------------------|---------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| **`TEXT`**（普通文本）     | Prompt 注入限制：只输出 Markdown 正文，禁止任何卡片或围栏标记。                                         | `spec.call().content()` 直接返回文本。                                             | `spec.stream().content()` 直接透传 Token 流，首字延迟（TTFT）最低。                                                                                  | 纯文本问答、知识库检索（RAG）、长文创作。                      |
| **`JSON`**（围栏交互卡片） | Prompt 注入限制：Markdown 正文 + 末尾唯一一个 ` ```json-render ` 围栏。                                 | `spec.call().content()` 返回图文混合文本。                                         | `AiUtils.processStream` 状态机滑动窗口解析：正文以 `type="text"` 事件实时打字机推送，尾部卡片完整积攒后一次性推入 json-render 块（未闭合卡片在流结束时会恢复输出）；整体 `Flux.concat(START, flux, END)` 包装，异常降级 ERROR。 | 渐进式流式图文交互、答疑推荐商品/课程卡片。                    |
| **`STRICT`**（严格结构化） | 强约束：基于 `UiComponentRegistry` 动态合成 `UiResponseSchema` 的 JSON Schema，禁止输出非 blocks 内容。 | `spec.call().entity(converter, validateSchema)` 进行强制 Schema 校验，序列化返回。 | **并非逐 Token 流式**：整包接收并校验反序列化后，按 `UiBlock` 切分为 `type="block"` 事件（START/END 包装，校验失败仅返回 ERROR）。                    | 动态表单、微前端组件驱动（Blocks UI）、高可靠 Agent 调度决策。 |

### 3. AI 周边能力与扩展入口

- **会话记忆**：`applyBaseAdvisors` 挂日志/上下文/会话元数据/会话记忆 Advisor（`TurnCountTrigger(20)` + 滑动窗口压缩）；`SessionService` 默认内存实现，app 端被 `SystemAiConfiguration` 以 `CustomSessionRepository`（数据库表 `AiSessionEntity` + `AiSessionEventEntity`，TTL 60 天）覆盖；会话续接校验"本人 + 本租户"。
- **长期记忆**：`AutoMemoryToolsAdvisor` + `AutoDreamAdvisor`，目录按 `platform.ai.memory.path/{tid}/{uid}` 隔离。
- **Agent 工具**：`SkillsTool` + `TaskTool`（Claude 风格子代理），受 `platform.ai.agent.*` 开关；内置 `CommonTools`（版本/时间，AOT 注册），示例工具 `webapp/ai/tools/CoreTools`（ES 课程/讲师搜索）。
- **RAG**：全局 `RetrievalAugmentationAdvisor`；KB 级经 `AiHelper.applyRagAdvisors`（租户过滤 + 可选重排）；向量化流水线 `AiVectorServiceImpl`（任务表 + 事务提交后异步 + 失败退避重试）；VectorStore 支持 ES/MariaDB/PgVector，索引名带租户前缀（`AiRagUtils`）。
- **MCP**：system 域登记表（`AiMcpServerEntity`，权限 `dev:ai:config:mcp`）；Server 端鉴权走 `mcpServerApiKey()` + `CustomApiKeyRepository`；模型密钥落库前经 `AiSecretUtils.encrypt` 加密，更新时 `****` 掩码不覆盖旧值。
- **Embabel**：仅引入依赖（`embabelAgentVersion=1.5.2`）与空壳测试，**无业务接入**，不要当作已完成功能使用或引用。
- **扩展任务**：新增厂商 → `AiModelProvider` 加枚举 + 录入 `ai_model` 表（`AiModelPltController`）或配 `platform.ai.providers.*`；新增引擎 → 实现 `*ModelFactory`/`*ModelService` 子接口并在对应 AutoConfiguration 注册；新增 UI 组件 → 实现 `UiComponentDefinition` 声明为 Bean（props Schema 必须内联，禁止 `$ref`），注册后缓存 Schema 自动失效重建。

## 构建与依赖

- 新依赖优先在 `gradle/libs.versions.toml` 的 `[versions]` / `[libraries]` / `[bundles]` 中声明，不要在 `build.gradle.kts` 里裸写坐标
- 版本强制覆盖集中在 `buildSrc/src/main/kotlin/java-conventions.gradle.kts`（含 27 个 BOM 导入、约 40 组 `resolutionStrategy` 强制版本、全局排除列表），不要散落覆盖；库模块的 Jar 任务统一 exclude `application*.yml`
- 约定插件分层：`java-conventions` → `java-library-conventions` → `spring-boot-conventions` → `spring-boot-native-conventions`，模块按需套用
- 产物名固定：app-server → `app.jar`、admin-server → `admin.jar`、webapp → `webapp.jar`（外部化 Class-Path）、console → `console.jar`、native → `native.jar`；两个服务均配 `bootBuildImage`（bellsoft musl builder、JVM 25、CDS）
- 模块依赖必须通过 Gradle `project(...)` 引用；`commons-core` 对外能力多为 `compileOnly`（由 starter/应用模块提供运行时依赖），新增可选能力沿用该模式
- 已知 CI 隐患：`tools/actions/build-native.yml` 引用 `platform-boot-server:app-server:nativeCompile`，实际项目路径是 `platform-services:app-server`，该 workflow 任务路径疑似过期

## 已知问题（2026-09-17 静态核实，基线 `5c10778`；改动相关代码前先确认是否已修复，未列运行时复现结论）

- `/exchange/**` 无鉴权（严重）：10 个 `*ExchangeController` 均有实际 MVC 映射但无权限注解，且返回含密码哈希、OAuth2 客户端密钥等敏感字段；部分返回未包 `R<T>`；`AuthorizationConsentExchangeController` 仍有空实现
- 租户上下文信任请求头（租户 ID 与根租户标识），未与 JWT `tid` 校验（严重）；`SecurityUtils#getTid` 会回退 `TenantContext`
- `NoticeSysController`（message 域）三个端点无权限注解；详情接口按 ID 更新已读无收件人归属约束；`NoticeServiceImpl#findNoticeByPage` 异常分支静默改按"当前收件人"兜底查询，改变数据范围
- `NettyWebSocketServer.destroy()` 关闭条件写反（以 `isShutdown()` 作为发起关闭的前置条件，且父组条件混入子组状态），事件循环线程组不会优雅关闭
- `GlobalExceptionHandler`：`SystemException`/兜底 `Exception`/部分数据库异常仍把内部异常消息返回客户端，多数返回 HTTP 400（404/方法不支持等另有分支）；缺参处理会记录请求参数与请求头，是日志脱敏审查点
- `spring.main.allow-circular-references` 与 `allow-bean-definition-overriding` 同时开启（历史包袱；请勿新增循环依赖或同名 Bean 定义）
- 登录主体构造使用的简化构造器将账户状态布尔值置 true：数据库的禁用/锁定状态未完整映射到登录主体校验
- `tools/database/pgsql/db_pgsql_schema_core.sql` 为含 `DROP TABLE` 的初始化脚本，不是生产增量迁移方案；仅限本地开发环境使用的硬编码口令存在于历史记录中（待复核，禁止带入任何入库配置）

## 禁止事项

- 不要提交真实密钥/口令；配置只允许占位或 `${ENV_VAR}` 引用
- 不要修改或提交 `application-local.yml`、`application-production.yml`（已 gitignore，属本地/生产私有配置）
- 不要删除 `tools/deploy/binaries/` 下的本地部署产物（目录已 gitignore，仅存 `.gitkeep`）
- 不要使用 `System.out.println` / `printStackTrace`（测试辅助除外）
- 不要在业务代码中写 `Thread.sleep`、空循环等待
- 不要通过 `apply_patch` 之外的方式（如 shell 重定向）改写代码文件

## 新增业务功能的推荐步骤

### 完整样例（推荐先照抄一条真实链路再改）

`config` 域是最完整的参考实现（`dict` 同构）：
`system-api/.../config/api/ConfigApi`（`@HttpExchange` + Request/Form/VO/strategy）→ `system-impl` 中 `ConfigApiImpl`（`@Service`）+ `ConfigExchangeController`（HTTP 暴露）→ `ConfigSysController`（extends `AbstractController`，方法级 `@PreAuthorize("hasAnyAuthority('system:config')")`，写操作加 `@OperationLog`）→ `ConfigServiceImpl extends BaseCachingEntityService`（实现 `getCacheKeyGenerator()`，双 scope 走 `BizTypeApi` 判定）→ `ConfigRepository`（MyBatis-Plus，无需 XML；全库仅 13 个 XML Mapper，简单 CRUD 不必写 XML）。`dict` 域结构相同，可作第二参考。

### 步骤

1. 在 `system-api` 中定义 `XxxApi`（`@HttpExchange`）与 DTO/Form/Request/VO
2. 在 `system-impl` 中实现 `XxxApiImpl`（`@Service`）与 `XxxService` / `XxxServiceImpl`，实体继承 `BaseEntity`/`BaseTenantEntity`，转换用 MapStruct
3. 若涉及持久化，新增 `XxxRepository`（MyBatis-Plus）并同步补充 SQL 脚本与 XML（如需）
4. 新增 `XxxSysController` / `XxxWebController`， **每个端点必须加 `@PreAuthorize` 或 `@Authenticated`**
5. 涉及用户可见操作时加 `@OperationLog`；列表接口分页并返回 `R<Page<...>>`
6. 在 `commons-webapp` 或 `app-server` 补充测试（优先不依赖外部中间件）

## 横切功能导航（按任务查入口）

| 能力 | 配置树 | 代码入口 |
|------|--------|----------|
| 对象存储 | `platform.storage` | commons-core `storage` + system `storage` 附件域（`AttachmentApiTests` 在 app-server） |
| 定时任务 | 标准 `spring.quartz.*` + `spring.datasource.job` | system `job` 域 + Quartz 定制装配（无 `platform.*` 开关） |
| 消息/WebSocket | `platform.message.rabbit/broadcast`、`platform.websocket` | commons-core `message`（Rabbit/WebSocket/SSE/Netty）+ system `message` 域 |
| 日志 | `platform.log` | logback 模板在 `commons-core/src/main/resources/cc/wdev/logging/logback/`，业务表 `sys_*_log` |
| 开放平台 | `platform.oapis.*`、`platform.sms`、`platform.translator`、`platform.face-body` | commons-core `oapis`（微信/钉钉/飞书/Telegram/短信/翻译/人脸），默认全关 |
| 数据源/分库 | `platform.data.datasource.*`、`sharding` bundle | `DataSourceCustomAutoConfiguration`（master/slave/job；当前 Primary 直接返回 master，无自动读写路由） |
| 验证码/IP/敏感词 | `platform.captcha/http/keyword/sensitive` | commons-core `extensions` |
| 前端资源 | — | 无独立 Node 工程；静态资源在 `commons-webapp` 与 `app-server` 的 `resources/public` + Thymeleaf `templates/` |

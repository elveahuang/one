# Agents.md

本文件是仓库的长期架构索引与协作约定，供 AI 编码助手直接复用。修改前先读「安全」与「禁止事项」，再按任务定位相关源码；无需每次重新扫描全仓。

本文只记录**当前源码事实**与**长期约定**，不记录修复流水账。已闭环的问题不要回填这里；发现新缺口时改对应章节（架构变了）或「已知问题」（确实还没修），并顺手删掉已经不再成立的描述。**源码与本文冲突时以源码为准。** 文档描述源码事实，不代表服务已启动、测试已通过或部署环境已验证。

## 目录

- [使用与维护方式](#使用与维护方式)
- [项目概述](#项目概述) · [技术栈](#技术栈) · [常用命令](#常用命令)
- [运行配置（Profile）](#运行配置profile)
- [模块地图](#模块地图) · [业务域](#业务域) · [控制器与 URL 约定](#控制器与-url-约定)
- [配套仓库与前后端协同](#配套仓库与前后端协同)（本地联调 / 启动清单 / 契约速查）
- [代码约定](#代码约定)（分层 / 实体与软删 / 响应与异常 / 安全 / 数据层 / 缓存 / 序列化 / i18n / 日志 / 测试）
- [AI 模块架构](#ai-模块架构与响应模式)
- [构建与依赖](#构建与依赖)
- [已知问题](#已知问题)
- [禁止事项](#禁止事项)
- [新增业务功能](#新增业务功能的推荐步骤) · [横切能力导航](#横切能力导航按任务查入口)

## 使用与维护方式

- 开始任务先看 `git status --short`，保留用户现有修改和未跟踪文件，不将它们误认为自己的产出。
- 默认只读本文和任务涉及的模块、配置、调用方及测试；跨模块调整时再沿依赖扩展阅读。
- 记录稳定的文件路径与类/方法名，不依赖易漂移的行号、类数量、完整依赖版本清单。
- 明确区分“已实现”“示例/依赖接入”“待验证”；不要把历史缺陷当作新代码模板。
- 本地配置、构建产物和未提交测试不能作为已提交仓库能力的唯一依据。
- 改配置键、加端点、调协议时同步更新对应章节，避免下一个助手重复全仓分析。

## 项目概述

"one" 是一个自研的基础开发平台（单体架构，预留微服务拆分），提供后台管理端（sys）、平台端（plt）、用户端（web）三类 API，并集成 OAuth2
认证授权、多租户、多数据源、缓存、消息（RabbitMQ / WebSocket / SSE）、对象存储、Elasticsearch、Quartz 定时任务、Spring AI 多厂商模型接入，以及微信 / 钉钉 / 飞书 / 短信 /
翻译等开放平台能力。

## 技术栈

- Java 25（`java-conventions` 同时设 `sourceCompatibility`/`targetCompatibility` 与 toolchain 为 25，编译参数 `-parameters`）
- Gradle 9.8.0（wrapper；依赖版本集中在 `gradle/libs.versions.toml`；构建约定在 `buildSrc`）
- Spring Boot 4.1.1、Spring Security 7.1（Authorization Server + Resource Server）、Spring AI 2.0.1、Spring Cloud（预留）
- 持久化：MyBatis-Plus 3.5.17（主）、JPA/Hibernate（遗留与示例）、PostgreSQL
- 中间件：Redis（Redisson）、RabbitMQ、Elasticsearch、Quartz
- JSON：**序列化走 Jackson 3**（`tools.jackson.databind.*`），注解仍用 `com.fasterxml.jackson.annotation.*`（Jackson 3 刻意保留该注解包，不是泄漏）。**classpath 上同时存在 Jackson 2.21.5**（elasticsearch-java、tika、openai-java、arrow、geoip2、swagger-core 等第三方传递依赖拉进来的），它不参与 HTTP 消息转换，不要误读。
- 其他：CosId（雪花 ID）、Hutool、MapStruct、Lombok、springdoc-openapi、JavaCV/FFmpeg、GraalVM Native（实验）、embabel-agent（**仅依赖，无业务接入**）

## 常用命令

Windows 使用 `.\gradlew.bat`，Linux/macOS 使用 `./gradlew`（不要写成 `./gradlew.bat`，cmd 下会报「'.' 不是内部或外部命令」）。

- 全量构建：`./gradlew clean bootJar`（产出 `platform-services/*/build/libs/*.jar`）
- 单独构建主服务：`./gradlew :platform-services:app-server:bootJar`
- 只编译主服务源码（改后端后最快的验证）：`./gradlew :platform-services:app-server:compileJava`
- 全量测试：`./gradlew test`（**谨慎**，见「测试」节）
- 运行测试宿主模块测试：`./gradlew :platform-commons:commons-webapp:test`
- 本地启动主服务：`./gradlew :platform-services:app-server:bootRun`（或 IDE 运行 `AppServerApplication`；使用 JDK 25，非 public 的 main 在 Java 25 下是合法入口，不据此判断启动失败）
- 初始化本地数据库：`tools/database/bin/pgsql_init.cmd`（Windows）或 `pgsql_init.sh`（Linux）。**警告：脚本链含 `DROP DATABASE` / `DROP TABLE` / `TRUNCATE`，重跑会整库清空，仅用于本地初始化**；SQL 脚本位于 `tools/database/pgsql/`，新建表必须同步追加到 `db_pgsql_schema_core.sql`
- CI 产物复制脚本：`tools/scripts/build.sh` / `build.cmd`
- wrapper 发行版若本地未缓存且下载卡住，可用 `~/.gradle/wrapper/dists/` 下已缓存的版本直接调 `gradle.bat`；**不要为此改 `gradle-wrapper.properties`**

## 运行配置（Profile）

- 默认 profile：`local`。**五个应用模块都硬编码 `spring.profiles.active: local`**
- `application-local.yml`、`application-production.yml` 已被 gitignore（本地/生产私有配置，不入库，不要提交）；`application.yml`、`application-development.yml` 已入库，`application-native.yml` 只在 app-server 与 commons-native
- 服务端口：app-server `8181`、admin-server `8282`、commons-webapp `8080`；commons-console（`web-application-type: none`）与 commons-native **不监听端口**
- app-server 显式关闭 Consul

### `platform.*` 能力开关

开关全部定义在 `commons-core-starter` 的 `autoconfigure/**` 下，全仓没有别处的 `@ConditionalOnProperty`。

**判读时必须区分两层语义**——`@ConditionalOnProperty(matchIfMissing = true)` 只决定「这个自动配置要不要装配」，而 properties 类里的 `boolean enabled` 字段默认值往往仍是 `false`，决定「装配后行为是否开启」。**大量「默认开」的能力是 Bean 在但功能关**，必须显式 `platform.xxx.enabled=true` 才真正生效（`cache`/`mail`/`sequence`/`storage`/`template`/`web`/`captcha`/`data.mybatis`/`message.broadcast`/`ai` 均属此类）。

| 档位 | 配置键 |
| --- | --- |
| 条件默认装配（`matchIfMissing=true`） | `async`、`cache`、`jwt`、`log`、`mail`、`sequence`、`storage`、`template`、`web`、`data.mybatis`、`captcha`、`http`、`keyword`、`sensitive`、`message.rabbit`、`message.broadcast`、`websocket`、`ai` |
| 无条件装配 | `parser`（`ParseAutoConfiguration` 上**没有** `@ConditionalOnProperty`，`ParseProperties.enabled` 默认 `true`） |
| 需显式 `enabled=true` | `data.core`、`data.jdbc`、`data.datasource`（含 `.master`/`.slave`/`.job`）、`data.jpa`、`data.elasticsearch`、`swagger`、`ip`、`selenium`、全部 `oapis.*`、`sms`、`translator`、`face-body`、`test` |

注意几点：

- **不存在 `platform.jdbc` 这个键**，jdbc 模板的真实前缀是 `platform.data.jdbc`。
- **`platform.tenancy.enabled` 的实际语义不是「配置类默认值」**：`TenantConfig.enabled` 字段默认 `true`，但真正生效的两个 Bean（`MyBatisCustomAutoConfiguration` 的 `META_OBJECT_HANDLER_PREFIX`、`JpaCustomAutoConfiguration`）用的是**不带 `matchIfMissing` 的 `@ConditionalOnProperty`**，即不显式配就一律不装配租户拦截器。app-server 的 `application-local/development/native.yml` 都显式配了 `true`——本地与联调按租户过滤是常态，判断“数量对不上”先想到租户作用域。
- **JWT 有效期实际是 60 分钟 / 刷新 14 天**（`platform.jwt.access-token-time-to-live` / `refresh-token-time-to-live`）。`JwtConfig` 自己的 `@Builder.Default`（15 分钟 / 3 天）会被 `JwtAutoConfiguration` 用 `JwtProperties` 的值无条件覆盖，**看默认值要看 `JwtProperties` 而不是 `JwtConfig`**。
- Quartz **没有** `platform.*` 开关，走标准 `spring.quartz.*`（`QuartzCustomAutoConfiguration` 只判 `@ConditionalOnClass`）。job 域独立数据源 `spring.datasource.job` 属于 `platform.data.datasource.job.enabled=true` 的可选项，不开则回退到主数据源。
- 自动配置注册表：`commons-core-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，共 40 条（无旧式 `spring.factories`）。
- 有 properties 类但无对应自动配置条目的：`platform.data.mongodb`、`platform.rate-limit`。

## 模块地图

`settings.gradle.kts` 共 13 个子项目，无 `projectDir` 重映射：

```
one (Gradle root, group cc.wdev)
├── platform-commons
│   ├── commons-core          平台核心库：R/异常/枚举/工具、数据层抽象（MyBatis/JPA/ES）、缓存、多租户、
│   │                         日志（LogStore/切面）、消息（Rabbit/WebSocket/SSE）、存储、序列、
│   │                         扩展（验证码/IP/敏感词/HTTP/parser）、序列化（CommonModule）、
│   │                         AI 抽象（factory/service/ui/advisor）、开放平台 SDK（微信/钉钉/飞书/短信/翻译）
│   ├── commons-core-starter  commons-core 的 Spring Boot 自动装配层 + 所有 platform.* 属性类
│   ├── commons-javacv        JavaCV / FFmpeg / Tesseract 原生依赖封装（多平台 classifier）
│   ├── commons-console       Spring Shell 控制台应用（console.jar，无端口）
│   ├── commons-native        GraalVM Native Image 验证应用（native.jar，无端口）
│   └── commons-webapp        开发/测试宿主：JPA、MyBatis-Plus、ES、AI 示例；主要测试宿主
├── platform-modules
│   ├── commons/commons-api   空聚合模块（仅转出 system-api，无源码）
│   ├── commons/commons-starter 平台级 starter：安全默认配置（JwtAuthenticationConverter、
│   │                         BearerTokenResolver）、HttpExchange 客户端代理、MessageSource
│   └── system
│       ├── system-api        系统域 API 模块：全部业务域的 @HttpExchange 接口（XxxApi）、
│       │                     DTO/Form/Request/VO/BO、枚举、常量；含 CustomJwtGrantedAuthoritiesConverter
│       ├── system-impl       系统域实现模块：Controller、XxxApiImpl、Service/Impl、Repository（Mapper）、
│       │                     Entity/Converter、i18n 资源；XML Mapper 在 resources/repository/system/
│       └── system-security   OAuth2 授权服务器 + 资源服务器、自定义认证（密码/OTP/社交以
│                             Converter+Provider 形式，非 Filter）、JWT、验证码过滤器、
│                             WebSecurityConfiguration
└── platform-services
    ├── admin-server          Spring Boot Admin 服务端（8282）
    └── app-server            主业务服务（8181）：聚合 commons-starter + system-impl + system-security
```

**`WebSecurityConfiguration` 这个类名在全仓不唯一**，有 3 个同名类：system-security（生效的那个）、admin-server、commons-webapp。改安全配置时先看 import。

### 业务域

`system-impl` 下共 19 个包，其中 18 个是业务域：`ai`（模型/工具/MCP/知识库/Agent）、`catalog`、`commons`（验证码/首页）、`config`、`core`（用户/角色/权限/租户/登录会话）、`dev`、`dict`、`i18n`、`im`、`job`（Quartz）、
`log`、`message`、`open`（微信/钉钉/飞书）、`region`、`security`（OAuth2 客户端/授权/AppKey）、`site`（公告/横幅/友链）、`storage`（附件）、`tag`。
第 19 个 `configuration` **不是业务域**，是各域 `@Configuration` 基础设施的汇总包（`SystemAiConfiguration`、`SystemLogConfiguration`、`SystemTenantConfiguration` 等）。

### 控制器与 URL 约定

| 分类                  | URL 前缀         | 说明                         |
|-----------------------|------------------|------------------------------|
| `*SysController`      | `/api/v1/sys/**` | 后台管理端                   |
| `*PltController`      | `/api/v1/plt/**` | 平台端（运维/开发者）        |
| `*WebController`      | `/api/v1/web/**` | 用户端                       |
| `*ExchangeController` | `/exchange/**`   | 内部服务间调用（微服务预留） |
| MCP Server            | `/api/mcp`       | Spring AI MCP                |

注意：

- `*WebController` 不一定都在 `/api/v1/web/**` 下，例如 `AiChatWebController` 实际用 `API_V1_PREFIX`（`/api/v1/ai/chat/*`）。**以各控制器方法上的映射常量为准**。
- `*PltController` 也有走 sys 前缀的已知偏差（见「已知问题」）。
- `*ExchangeController` **不返回 `R<T>`**，直接返回裸类型（见「响应与异常」）。

## 配套仓库与前后端协同

本仓的配套前端仓为 `one-ui-vue`（本地路径 `D:\Workspace\github\one-ui-vue`，独立 Git 仓库；pnpm workspace：`packages/webapp` 桌面管理端、`packages/mobile` 移动端、`packages/mp` 小程序、`packages/commons` 共享层）。其内部结构与任务地图见 `one-ui-vue/AGENTS.md`。协同开发时：

1. 先查下节契约速查表定位两端入口，再分别读两侧源码；只有速查表无法解释的问题才扩大搜索。
2. 修改任何下表涉及的对外契约（URL、参数名、响应结构、认证协议）时，必须同步检查前端消费点并更新本节，两份文档同步维护。
3. 本地联调的标准方式是 Nginx + `wdev.cc` 域名同源反代（见下节），**无需修改前端 `VITE_APP_SERVER`、vite proxy 或注入租户头**；前端仓库自身没有 mock/proxy。

### 本地联调环境（Nginx + wdev.cc）

- Windows hosts 将 `wdev.cc`/`z.wdev.cc`/`s.wdev.cc`/`sc.wdev.cc` 映射到 127.0.0.1（`# fetch-wdev-hosts` 标记块）；443 为本地证书，curl 验证需 `-k`。
- Docker 容器 `nginx`（配置 `D:\Data\compose\nginx\config\nginx.conf`）`server_name wdev.cc *.wdev.cc`：
  - `/webapp` → `host.docker.internal:8081`（webapp vite dev）、`/mobile` → `:8082`（mobile vite dev）；
  - `/api`、`/oauth` 等其余路径 → `:8181`（app-server）；`/ws` → 8181 且 nginx 把 query `token` 映射为 Authorization 头（与前端 `access_token` query 机制并存）；
  - 另有 `/devops/sba|rabbitmq|grafana|consul|adminer|redis` 运维入口。
- 租户按 Host 域名解析：前端**只发 `x-tenant-id`、不发 `x-tenant-root-ind`**，而 `TenantContext.handleServletRequest` 的条件是 `isEmpty(tenantId) || isBlank(tenantRootInd)`（见「已知问题」）——实际生效的往往是域名兜底解析。联调时换租户要改 Host，不要只改请求头。
- 用 IP/localhost 直连会报 1003002 租户不存在；**租户是否过期属于数据状态（1003004），不是链路故障**。
- vite 端口被占会自动顺延，而 nginx upstream 固定 8081/8082——联调前必须确认默认端口上只有目标 dev server。
- OAuth2 客户端密钥：`sys_client.client_secret` 入库形态为 `{noop}<明文>`（DelegatingPasswordEncoder 按 `{id}` 前缀识别，故 `{noop}c4e859c6…` 的真实密钥就是那串 hex）。**env 与库不一致时 `POST /oauth/token` 直接 401 `invalid_client`，先于任何用户口令校验**——curl 排查时先看这个错码，别误判为用户密码错。仓库种子 `db_pgsql_data_core.sql` 里 webapp/admin/mobile 三端共用同一串 hex，与 `packages/webapp/.env.dev` 一致；不一致的是 `.env.pro` / `.env.sit` / `packages/mobile/.env.*`（仍为 `webapp`），它们指向真实环境、密钥未知，**需向维护者索取，不要靠猜修改**。
- **不要只用 SQL 改 `sys_client`**：`ClientServiceImpl`（`BaseCachingEntityService`）按 id/code 缓存实体，SQL 直写不会失效缓存，`findByClientId` 仍读旧值。改完必须删 Redis（db 11）的 `client:code_<clientId>`（例：`docker exec redis redis-cli -a redis -n 11 DEL client:code_webapp`），否则登录依旧 401。

### AI 自主开发基础流程（启动与验证清单）

1. 前置：JDK 25、pnpm、Docker Desktop；中间件 `docker start pgsql redis rabbitmq`（Redis 密码 `redis`；PG `root/root`，库 `one_platform`/`one_quartz`；RabbitMQ vhost `one`）。
2. 起后端：`./gradlew :platform-services:app-server:bootRun`（后台），8181 监听即就绪（约 1-2 分钟）。
3. 起前端（前端仓）：`pnpm run webapp:start`、`pnpm run mobile:start`（后台）。
4. 验证：`curl -k -X POST https://wdev.cc/api/v1/initialize` 返回 `code:200`；浏览器开 `https://wdev.cc/webapp/` 自动跳 `/webapp/login` 渲染登录表单（`sys_config` 无 `LOGIN_CAPTCHA_ENABLED` 时 `loginCaptchaEnabled=false`，无需验证码）。
5. 登录账号：ROOT 租户 `admin`。**用户口令是 BCrypt 落库、不可逆推，必须向维护者索取，不要做口令猜测，也不要写进本文档或任何入库文件。** 登录前先按上条核对 `sys_client` 密钥与前端 env 是否一致，否则 401 与用户口令无关。
6. 浏览器登录验证：用真实输入事件填写登录页（合成 `click()`/`requestSubmit()` 不触发 Vue 提交逻辑），成功后网络序列为 `POST /oauth/token` 200 → `GET /api/v1/user` 200 → 跳工作台 `/webapp/home`，控制台出现 `system WebSocket connected successfully`。未登录访问受保护页面会跳 `/login?redirect=<原路径>`，登录页优先按 redirect 回跳。
7. 后端重启会使旧 JWT 失效（浏览器会话被踢回登录页），验证前先重新取 token。无法用真实输入事件时（例如内置浏览器面板不可用），可在页面上下文内用 `fetch('/oauth/token')`（Basic `webapp:<env secret>`）自行换取 token，再写 `localStorage` 的 `ACCESS_TOKEN`、`REFRESH_TOKEN` 与持久化 store `user`（其 `accessToken`/`refreshToken` 是**独立的一份**，三处都要更新）。
8. 停止：终止后台任务后 Windows 常残留子进程继续监听端口，用 `netstat -ano` 找 PID `taskkill /F /T` 补杀。

### 调试指南

#### 远程调试（IDEA）

1. 以 debug 模式启动 app-server：
   ```sh
   ./gradlew :platform-services:app-server:bootRun --debug-jvm
   ```
   或设置环境变量 `JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`

2. IDEA 中创建 Remote JVM Debug 配置，端口 5005，连接 `localhost:5005`

3. 在代码中设置断点，触发请求即可进入调试

#### 日志查看

- 控制台日志：`bootRun` 直接输出到控制台
- 文件日志：`logs/` 目录（`logging.logback.path` 默认 `logs`）
- 日志模板：`commons-core/src/main/resources/cc/wdev/logging/logback/`
- 本地/开发环境走 CONSOLE appender，生产环境走 CONSOLE+FILE+LOKI

#### 常见问题排查

| 问题 | 可能原因 | 排查方式 |
| --- | --- | --- |
| 启动失败：数据库连接失败 | PostgreSQL 未启动或密码错误 | 检查 `application-local.yml` 的 `spring.datasource.master.url/username/password` |
| 启动失败：Redis 连接失败 | Redis 未启动或密码错误 | 检查 `platform.cache.redis.host/port/password` |
| 启动失败：RabbitMQ 连接失败 | RabbitMQ 未启动或 vhost 不存在 | 检查 `platform.message.rabbit.host/port/virtual-host` |
| 端口被占用 | 其他进程占用 8181 | `netstat -ano \| findstr 8181` 找到 PID 后 `taskkill /F /PID <pid>` |
| 租户不存在（1003002） | 请求头未传租户且域名未解析 | 检查 `x-tenant-id` 请求头或 hosts 配置 |
| 401 invalid_client | OAuth2 客户端密钥不匹配 | 检查 `sys_client.client_secret` 与前端 env 是否一致 |
| 403 无权限 | `sys_authority` 无对应权限码 | 检查权限码是否存在于 `sys_authority` 表 |

### AI 工具协作规范

#### 代码修改验证清单

修改代码后，按以下顺序验证：

1. **编译检查**：`./gradlew :platform-services:app-server:compileJava`
2. **单元测试**：`./gradlew :platform-services:app-server:test`（谨慎执行，可能依赖外部中间件）
3. **启动验证**：`./gradlew :platform-services:app-server:bootRun`
4. **接口验证**：`curl` 或浏览器验证关键端点
5. **前端联调**：浏览器访问 `https://wdev.cc/webapp` 验证页面功能

#### 多文件修改协调

- 跨模块修改时，先修改 API 模块（`system-api`），再修改实现模块（`system-impl`）
- 前后端契约变更时，先修改后端，再修改前端，最后同步两份 AGENTS.md
- 数据库变更时，同步更新 `tools/database/pgsql/db_pgsql_schema_core.sql`

#### 代码风格

- 遵循现有代码风格，不主动格式化无关文件
- 新增代码必须有适当的注释，解释"为什么"而非"做什么"
- 遵循各语言的命名约定（Java 驼峰、TS/JS 驼峰、CSS kebab-case）

### 契约速查

| 契约 | 后端事实（本仓） | 前端消费点（one-ui-vue） |
| --- | --- | --- |
| 响应体 | `R{code,message,data}`，成功码 200（`ResponseCodeEnum.SUCCESS`），`data` NON_NULL；Jackson 会把 `isSuccess()` 序列化为只读 `success` 布尔字段（无 timestamp）。**`/exchange/**` 例外，不包 `R`** | `commons/src/types/common.ts` 的 `R` 类；HTTP 层不解业务码，调用方手工判 `code==200` |
| 分页请求 | `PageRequest`：`page` 从 1 起、`size` 默认 10、`sort` 单字段、`order`、`q`；`getPageable()` 内部转 0 基 | `data-table` page 从 1 直发；`data-list` 内部从 0 起、请求时 +1，最终与后端一致 |
| 分页响应 | 原生 Spring Data `Page` JSON（content/pageable/totalElements/last…），无自定义序列化；**但 `CommonModule` 把 Long 序列化成字符串，故 `totalElements` 等是字符串** | `PageResponse` 类型，字段全可选 |
| 登录 | `POST /oauth/token` 表单：`grant_type=password`、`username`、`password`（前端明文直传）、`scope=openid`、`client_id/client_secret`（Basic 亦可用，客户端存 `sys_client` 表，密钥为 `{noop}<明文>` 且与实体缓存联动）；验证码走 `captcha_key/captcha_value`，仅 password grant 且 `LOGIN_CAPTCHA_ENABLED` 时由 `CaptchaAuthenticationFilter` 校验 | `modules/core/api/commons/auth.ts` + `VITE_APP_OAUTH_*` 注入 |
| 令牌有效期 | 访问令牌 **60 分钟**、刷新令牌 **14 天**（`platform.jwt.*`；`JwtConfig` 自身的 15min/3d 默认值不生效） | 401 拦截器单飞刷新重放 |
| 刷新/登出 | 标准 `grant_type=refresh_token` 走 `/oauth/token`；登出 `POST /api/v1/user/logout`；token 响应为 OAuth2 标准顶层字段 | `refreshApi` |
| 用户资料 | `GET /api/v1/user` 返回 `UserInfoDto`（含 tenantId、roles、authorities 等） | `UserStore` 登录后拉取；`tenantId` 成为租户头来源 |
| 租户协议 | 请求头 `x-tenant-id`（数值优先）/ `x-tenant-code` / `x-tenant-root-ind`（`GlobalConstants`），resolver 兜底按域名，响应头回写 | `http.ts` 拦截器 `headers.set('x-tenant-id', user.tenantId)` |
| 验证码 | `POST /api/v1/captcha/code` 返回 `{key,image}`；另有 `/captcha/mail`、`/captcha/sms` 及各 `/check`（`CaptchaController`） | 两端 captcha 组件与 `captcha.ts` |
| 应用初始化 | `POST /api/v1/initialize`（匿名）返回 `R<InitializeVo>`（登录验证码开关、访问限制、app.title/webSocketServer 等） | `base.ts` → `AppStore.initialize` |
| 工作台/仪表盘 | `GET /api/v1/sys/get-system-info`（`registerCount` 走租户过滤的 `UserApi#getCount`，`onlineCount` = 未结束且 1 小时内有访问的会话去重用户数——该窗口宽于 60 分钟的访问令牌 TTL，是会话式活跃近似，见 `LoginSessionServiceImpl.ONLINE_WINDOW`）、`GET /api/v1/sys/login-platform-line-chart?type=1|3|4&date=&goHeavy=`（均 `system:workbench`，`DashboardSysController`） | webapp `modules/core/views/workbench.vue`（着陆页 `/home`），`api/platform.ts` 的 `platformCountsApi`/`loginPlatformLineChartApi` |
| 权限码来源 | 方法级 `hasAnyAuthority('x:y')` 的字符串必须存在于 `sys_authority.code`，否则非 admin 用户一律 403（JWT 权限由 `CustomJwtGrantedAuthoritiesConverter` 查库拼装，不做前缀推导）。admin 经 `*:*:*` 根权限短路放行。**仓库没有任何 `sys_authority` 种子数据**，见「已知问题」 | 前端菜单/路由的 `authorities` 同样按字符串精确匹配 `sys_authority.code`（组级菜单用 `group:xxx`，模块级用 `module:xxx`），缺失即菜单不可见 |
| AI 流式 | `POST /api/v1/ai/chat/stream`，`text/event-stream`，`Flux<String>`；请求体自定义 `SimpleChatRequest`（prompt/conversationId/modelCode/agentCode/kbId/chatType/withRag），非 OpenAI 格式；同前缀还有 `/text`、`/list`、`/details`、`/start`、`/delete` | `utils/ai.ts` `TextStreamChatTransport` 直连 `/api/v1/ai/chat/stream`，不走 axios（需自带 `Authorization` 与 `x-tenant-id`） |
| WebSocket | 两条通道：主服务（8181）servlet 注册 `/ws/message`（`WebSocketHandlerConfiguration` + `SystemWebSocketHandler`，握手走 Spring Security；`DefaultBearerTokenResolver.setAllowUriQueryParameter(true)` 使 query `access_token` 可用）——前端实际使用这条；另有 Netty 独立端口 8686 路径 `/ws`（query 参数名为 `authorization`，`NettyUtils.getAuthorization`） | `store/socket.ts` 连 `${server}/ws/message?access_token=...`，server 优先 initialize 返回的 `app.webSocketServer` |
| 前缀消费现状 | `/api/v1/sys|plt|web` 常量齐备；前端目前只调用 sys、web（banner/announcement/address）与无前缀端点，`/api/v1/plt/**` 尚无消费者 | `modules/*/api/*.ts`（注意 `api/platform.ts` 实际也调 `/api/v1/sys/**`） |

## 代码约定（必须遵守）

### 分层与包结构

每个业务域按固定分层组织，**但 `domain` 下的类型按模块切分**，新增业务功能必须沿用：

- `system-api` 侧：`api`（`XxxApi` 接口）、`domain/dto|form|request|vo|bo`、`enums`、`strategy`
- `system-impl` 侧：`api`（`XxxApiImpl`）、`controller/{system,plt,webapp,exchange}`、`domain/entity`、`domain/converter`、`repository`、`service` + `service/impl`
- 对象转换使用 MapStruct：`XxxConverter.INSTANCE`，禁止手写 getter/setter 拷贝
- 包名一律 `cc.wdev.platform.*`；模块间只能通过 Gradle project 依赖引用，禁止跨模块直接使用未导出的类

**API 接口与 HTTP 暴露的细节**（这些不是铁律，新写代码前先看邻近域怎么做）：

- `XxxApi` 用 Spring `@HttpExchange`，URL 前缀 `EXCHANGE_PREFIX + "/xxx"`（`MappingConstants.EXCHANGE_PREFIX = "/exchange"`）。当前 10 个：`User`、`Client`、`Captcha`、`Social`、`LoginSession`、`Tenant`、`Message`、`Config`、`Authorization`、`AuthorizationConsent`。
- `XxxApiImpl` 一律是 `@Service`，被同进程 controller 与安全模块直接注入。多数域放在 `api` 包下，`security`/`message` 放在 `api/impl` 子包。
- `XxxExchangeController` 数量与 `XxxApi` **不对应**：`DictApi` 没有 `@HttpExchange` 也没有 exchange controller；`TenantExchangeController`、`ClientExchangeController`、`AuthorizationExchangeController` 绕过 `XxxApi` 直接注入 `XxxService`；`AuthorizationConsentExchangeController` 是空壳桩。
- 另有 `DataVGeoApi` 这类**出站** `@HttpExchange` 客户端（调外部地理服务），与内部 exchange 接口无关。

**控制器基类**：`AbstractController`（`commons-core` 的 `cc.wdev.platform.commons.web.servlet.controller`）提供 `protected GlobalContext context` 与 `redirect()`/`forward()` 两个前缀拼接助手。61 个控制器直接 `extends` 它。`AbstractEntityController` 继承它但**全仓无人使用**，是死类，不要模仿。

**Repository**：写 `@Mapper interface XxxRepository extends BaseEntityRepository<Entity, Long>`。注意 `BaseEntityRepository` **有三个同名类**（`data/mybatis`、`data/jpa`、`data/elasticsearch`），业务域用的是 mybatis 那个——它只是 `extends BaseMapper<T>` 的**空标记接口**，第二个类型参数 `K` 完全没有被使用，`BaseEntityService<T, K, R>` 里的 `K` 同样是幽灵参数，**别以为 ID 类型在编译期被约束了**。JPA 那个是真有方法的（`JpaRepository + JpaSpecificationExecutor`），别混用。

**XML Mapper 不与 `repository` 包同目录**，统一放 `system-impl/src/main/resources/repository/system/*.xml`（当前 12 个）。扫描路径由 `MyBatisCustomAutoConfiguration#mybatisPlusPropertiesCustomizer` 设为 `classpath*:/mapper/**/*.xml` + `classpath*:/repository/**/*.xml`。简单 CRUD 一律用泛型 Mapper，不写 XML。

**业务类型注册表（`BizTypeApi`）**：这是跨域的横切能力，不只是 config 域的。`CoreApiImpl` 在启动时调 `BizTypeApiImpl#initialize()`，扫描全仓 `BaseBizTypeEnum` 实现并 upsert 进 `sys_biz_type`（group/code/scope/labelKey + 枚举 `getConfig()` 序列化后的 JSON）。两种用法：

- **类型注册**（多数域）：`DictServiceImpl`、`TagServiceImpl`、`AttachmentApiImpl` 注入 `BizTypeApi` 解析「类型码 → 配置 JSON」；`RoleSysController`/`DictTypeSysController`/`TagTypeSysController` 用它渲染下拉。
- **scope 判定**（仅 `ConfigServiceImpl`）：`BizScopeTypeEnum.PLATFORM` → 存 `tenantId = 0` 且仅根租户可见；`SYSTEM` → 存当前 `TenantContext.getTenantId()`。注意它的缓存键用的是 `SimpleTenantCacheKeyGenerator`，**PLATFORM 分支的键也带当前租户**（`config:<tenantId>_<configKey>`），所以同一份平台配置会按租户各缓存一份——是冗余不是串号，别误判成缓存污染。

### 实体与软删

实体基类在 `commons-core` 的 MyBatis 体系下（`data/mybatis/domain`，JPA 与 Elasticsearch 各有一套同名平行基类，字段集基本一致但 ES 的 `BaseEntity` 没有 `version` 且审计人是 `String`）：

- `AbstractEntity`（实现 `IdEntity`）：仅 `id`
- `SimpleEntity`：`id` + `version` + `active` + `createdAt/createdBy`——**没有 `updated_*`、没有 `deleted_*`**
- `SimpleTenantEntity extends SimpleEntity`：加 `tenantId`。关联表、操作日志、AI 会话表用这一支
- `BaseEntity`：`SimpleEntity` 之外再加 `updatedAt/updatedBy` + `deletedAt/deletedBy`
- `BaseTenantEntity extends BaseEntity`：加 `tenantId`。需要租户隔离的主表用这一支

软删语义：

- 全仓**没有任何 `@TableLogic`**，也没有 `logic-delete-*` 的 MyBatis-Plus 全局配置，查询不会自动过滤已删数据。删除必须显式走服务层 `softDelete*`。
- `BaseEntityService#softDeleteBatch` 的行为随实体类型变化：`BaseEntity` → 置 `active=DISABLED` + `deletedAt/By`；`SimpleEntity`/`SimpleTenantEntity` → **只置 `active`**；裸 `AbstractEntity` → **字段根本不更新却仍执行一次 UPDATE，是静默空操作**。给非 `BaseEntity`/`SimpleEntity` 的实体用 `softDelete*` 等于没删。
- `EntityService` 接口同时声明了硬删（`deleteById`/`deleteBatchById`/`delete`/`deleteBatch`/`deleteAll`）与软删两套入口，`BaseEntityService` 两套都实现了。**默认走软删**（见「数据层」的例外清单）。

### 响应与异常

- `/api/v1/**` 接口返回 `R<T>`（`code`/`message`/`data`），成功用 `R.success(data)`；`message` 默认 `"Success"`
- 业务错误抛 `ServiceException(ResponseCodeEnum.XXX)`，**不要用 `R.error()` 作为业务分支的返回值**——它出的是 `code=500/系统错误`，客户端无法区分业务错误
- 参数校验使用 `jakarta.validation`（`@Valid` + 注解），不要手写 null 判断链
- 分页：请求对象携带 `PageRequest`（`getPageable()`），返回 `R<Page<...>>`；用 `MyBatisPlusUtils` 在 `IPage` 与 `Page` 间转换
- 多语言文案通过 i18n 提供，不要硬编码中文文案到响应（见「i18n」）
- 业务 DTO 对外序列化时若含凭据字段，用 Jackson `@JsonProperty(access = WRITE_ONLY)`（序列化忽略、反序列化保留）。**不要用 MapStruct `@Mapping(ignore = true)`**——`UserLoginDto.password`、`ClientDto.clientSecret` 这类字段在进程内要被 `CustomUserDetailsService`、`CustomOAuth2ClientService` 读取才能完成登录与 OAuth2 客户端注册，converter 级 ignore 会直接打断认证链路

### 安全（红线，改动前必读）

`system-security` 的 `WebSecurityConfiguration`（`cc.wdev.platform.security.configuration`）用 `@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)` 开启方法级安全，共四条链：

| 优先级 | 匹配 | 放行规则 |
| --- | --- | --- |
| `HIGHEST_PRECEDENCE` | 授权服务器 endpoints（`AuthorizationServerConfiguration`） | `anyRequest().authenticated()` |
| 1 | `/api/mcp/**` | `anyRequest().authenticated()` + `mcpServerApiKey()`（`CustomApiKeyRepository`） |
| 2 | `/api/**` | `GLOBAL_API_EXCLUDE_URLS` 放行 + `anyRequest().permitAll()`；配了 JWT 解码与 `CustomJwtGrantedAuthoritiesConverter`，但**解码不改变放行结果** |
| 3 | 其余（无 matcher 兜底） | `GLOBAL_WEB_EXCLUDE_URLS` 放行 + `anyRequest().permitAll()`，会话策略 `IF_REQUIRED` |

关键事实：

- **`GLOBAL_API_EXCLUDE_URLS` 是空数组**，即整条 `/api/**` 链实际全放行，鉴权**完全依赖方法级注解**。`GLOBAL_WEB_EXCLUDE_URLS` 才是非空的。
- **`/exchange/**` 不匹配链 1 与链 2**（matcher 分别是 `/api/mcp/**`、`/api/**`），落到无 matcher 的兜底链 3，而链 3 同样是 `anyRequest().permitAll()`。
- 可用注解：`@PreAuthorize("hasAnyAuthority('x:y')")`、`@Authenticated`（元注解 `isAuthenticated()`）、`@Anonymous`（`permitAll()`）、`@Super`（要求 `*:*:*`）。**`@PermitAll` 不是自定义注解，是 JSR-250 的 `jakarta.annotation.security.PermitAll`**，只因 `jsr250Enabled=true` 才生效；不要与 `@PreAuthorize` 混挂在同一方法上，两个拦截器都会介入、行为不确定。
- `@RateLimiter`（`commons.core.cache.aspect.RateLimitAspect`，Redis/Lua）限流键为 `RateLimiter:<requestURI>:<ip|clientId>:<spelKey>`，支持 `#{...}` SpEL 取方法参数；`@OperationLog`（`commons.core.log.aspect.OperationLogAspect`，是个 `record`）**都不是元注解驱动**，各自独立切面，别指望继承语义。
- 权限字符串格式示例：`system:user`、`system:role`、`system:config`、`dev:ai:config:model`；角色前缀 `ROLE_`，数据范围前缀 `DATA_SCOPE_`。
- 认证方式（密码/OTP/社交）不是 Filter，而是三对 `AuthenticationConverter` + `AuthenticationProvider`（`OAuth2PasswordAuthentication*` / `OAuth2OtpAuthentication*` / `OAuth2SocialAuthentication*`），注册在 `AuthorizationServerConfiguration#getAuthenticationProviders`。`system-security` 里**只有 `CaptchaAuthenticationFilter` 一个 Filter**（`addFilterAfter(CsrfFilter.class)`，且只对 password grant 生效）。

因此：

- **新增任何端点必须显式加 `@PreAuthorize` 或 `@Authenticated`**；只有确需匿名的端点才用 `@Anonymous` / JSR-250 `@PermitAll`。
- 10 个 `*ExchangeController` 都没有方法级鉴权注解。**禁止向 exchange 接口新增更敏感的数据**；新增内部接口前先与维护者确认鉴权方案。
- 租户上下文 `TenantContext` 基于 MDC：Servlet 侧由 `ServletWebFilter`（响应式侧 `ReactiveWebFilter`）调 `TenantContext.handleServletRequest` 初始化，解析顺序是 `x-tenant-id` → `x-tenant-code` → 域名（`DefaultTenantResolver`），最后清理。**但请求头未与 JWT `tid` 做一致性校验**（`SecurityConstants.JWT_KEY_TID` 全仓只被写 2 次、读 3 次，没有一处做比对），且 `handleServletRequest` 的条件判定有缺陷（见「已知问题」）。服务层不能信任请求头即「当前用户所属租户」；`SecurityUtils#getTid` 在**未认证**时才回退到 `TenantContext`，已认证但 JWT 无 `tid` claim 时直接返回 `null`，不能仅凭它完成归属校验。
- 密码必须经 `SecurityUtils.encode()` 落库；**它返回的是 `{bcrypt}$2a$…`（`DelegatingPasswordEncoder` + BCrypt 默认），不是裸 BCrypt 串**，校验与比对都要走同一编码器。
- 禁止把异常消息（`e.getLocalizedMessage()`）直接回给客户端；不要向日志写入密码、token、密钥。
- 敏感接口（登录、验证码等）注意验证码/限流机制，不要绕过。

### 数据层

- SQL 一律参数化：XML Mapper 用 `#{}`，**禁止 `${}` 字符串拼接**（当前 12 个 XML 已全部合规）
- 新表 SQL 追加到 `tools/database/pgsql/db_pgsql_schema_core.sql`，遵循现有规范：`id BIGSERIAL` 主键、`version`、`active`、审计列、字段与表 COMMENT、必要的索引
- 多租户表继承 `BaseTenantEntity`；确需绕过租户过滤的查询用 `@InterceptorIgnore(tenantLine = "true")`（必须清楚后果）
- 乐观锁 `version` 由 MyBatis-Plus 自动处理，不要手动覆盖
- 列表查询必须分页或有上限，禁止无界全表 `.list()`
- **`.in(condition, col, collection)` 的集合参数按实参提前求值**，`condition` 为 `false` 也照样求值——集合构造必须用 `Arrays.asList(...)` 这类 null 安全写法，不能用 `Arrays.stream(arr).toList()`（`arr` 为 null 时必 NPE）

**删除语义**：默认软删（`softDelete*`），但**纯关联表允许物理删除**，且这条不能改回去。判据是三条同时成立：

1. 写入路径是「先删后插」——`AttachmentApiImpl#saveAttachmentRelation`、`Dict`/`Tag`/`Address`/`Region` 各自的 `RelationServiceImpl#saveRelation`、`AiRelationServiceImpl`、`CatalogRelationServiceImpl`、`EntityRelationServiceImpl`、`UserBizRelationServiceImpl`。改软删会让被移除的旧关联每次保存再插一份、永久累积。
2. 读取侧并不统一过滤 `active`——`BannerRepository.xml#findForUser`、`LinkRepository.xml` 里 `sys_dict_relation` 的 `EXISTS` 子查询都没有 `sdr.active = 1`，软删后「已解绑的标签」仍会命中筛选条件。
3. 表结构不支持软删语义——`sys_*_relation` 多继承 `SimpleTenantEntity`/`AbstractEntity`，只有 `active` 没有 `deleted_at/deleted_by`（`softDeleteBatch` 对非 `BaseEntity` 也只置 `active`），且均无唯一索引，无法据以恢复。

例外清单：`sys_attachment_relation`、`sys_dict_relation`、`sys_tag_relation`、`sys_address_relation`、`sys_region_relation`、`sys_ai_relation`、`sys_catalog_relation`、`sys_entity_relation`、`sys_user_biz_relation`。**新增关联表若沿用「保存即重写」模式，同步加入本清单**；若需要保留删除历史，则改成 `BaseEntity` + 显式差量更新，并给所有读取侧补 `active` 过滤，**二者不要混用**。

### 缓存与性能

- 实体级缓存：继承 `BaseCachingEntityService`。`getCacheKeyGenerator()` **是 default 方法不是抽象方法**（默认按类名生成 key），但缓存键形状不是 `id`/`code` 的服务应显式覆盖 `getCacheKeyGenerator()` 乃至 `setCache`/`deleteCache`。
- **缓存失效只按 `byId(id)` 和 `byCode(code)` 两个键**。任何直接调 mapper 或链式 wrapper 写库（`.remove()`、`lambdaUpdate().update()`）的路径**完全绕过失效**；需要缓存一致性的服务必须自己接。租户级缓存键用 `SimpleTenantCacheKeyGenerator`（`TenantCacheKeyGenerator`），它总会把当前 `TenantContext.getTenantId()` 拼进键里。
- `insert` 会 `setCache` 回填，`save`/`updateById` 只 `deleteCache` 失效。
- 限流：`@RateLimiter`（基于 Redisson）
- JWT 权限转换（`CustomJwtGrantedAuthoritiesConverter`）按 uid 调 `UserApi#getUserAuthority` 查库组装权限码/`ROLE_`/`DATA_SCOPE_`，**无缓存，即每次需要权限转换的请求都走一次权限查询**，新增代码不要继续放大该模式。两个同名 `JwtAuthenticationConverter` Bean（`commons-starter` 的 `PlatformSecurityConfiguration` 与 `system-security` 的 `CommonSecurityConfiguration`）**实现逐行相同**，app-server 同时扫描两个包且开了 `allow-bean-definition-overriding`，所以实际只有一个生效——改这一个不等于改了两个。

### 序列化

`CoreAutoConfiguration#jsonMapperBuilderCustomizer` 通过 `JsonMapperBuilderCustomizer` 挂 `CommonModule`，并设全局 `NON_NULL`（value + content）。`CommonModule`（`commons-core/utils/jackson`）注册：

- `long`（基本类型）、`Long`、`BigInteger` → `ToStringSerializer`（**只序列化不反序列化**，入参 Long 仍按数字解析）
- `Date`、`LocalDateTime` → 各自的 serializer/deserializer

`SimpleModule` **不是 Spring Bean**，同一份 `CommonModule` 还被 `JacksonUtils` 与 `OAuth2Utils` 各自 add。

**任何 Long 字段出参都是 JSON 字符串**（含 `Page.totalElements`/`numberOfElements`），前端需 `Number()`。新增 VO/DTO 时别把这个当成 bug 报。

### i18n

- 资源包：`system-impl/src/main/resources/i18n/system/label/messages*.properties` 与 `i18n/system/validation/messages*.properties`，各含 `messages`（默认）、`zh_CN`、`zh_TW`、`en_US` 四个语言版本
- `MessageSource` Bean 在 `commons-starter` 的 `PlatformCommonConfiguration`，basename 三个来自 `SystemConstants`：`classpath:org/springframework/security/messages`、`classpath:i18n/system/label/messages`、`classpath:i18n/system/validation/messages`；`useCodeAsDefaultMessage(true)`
- 静态取文案用 `MessageSourceUtils.getMessage(code, ...)`（`MessageSourceAware`）
- **key 命名约定**：`BaseBizTypeEnum#getLabelKey()` 生成 `label__<group>__<value>`（小写）
- 数据库侧另有 `sys_label`（`LabelEntity`，每语言一列 + `*StaticInd` 标记）、`sys_entity_label`（`EntityLabelEntity`，按 `className`+`propertyName` 翻译枚举/字段值）、`sys_tenant_lang`——**没有名为 `I18nEntity` 的类**
- 两条要知道的限制：`CustomLocaleResolver.resolveLocale` 直接返回 `GlobalConstants.DEFAULT_LOCALE`，**完全忽略请求的 `Accept-Language`**；加上 `useCodeAsDefaultMessage(true)`，缺翻译会静默显示 key 本身而不报错

### 日志

- 模板在 `commons-core/src/main/resources/cc/wdev/logging/logback/`：`base.xml`（共享 pattern，含 MDC `%X{requestId}`）、`console-appender.xml`、`file-appender.xml`（`SizeAndTimeBasedRollingPolicy`，路径 `logging.logback.path` 默认 `logs`）、`loki-appender.xml`。**没有分 profile 的模板文件**
- profile 分支在各模块自己的 `logback-spring.xml` 里用 `<springProfile>` include；app-server 的 dev/local 走 CONSOLE，production/native 走 CONSOLE+FILE+LOKI
- 业务审计走 `@OperationLog` → `OperationLogAspect` → `LogStore#saveOperationLog`。默认实现 `DefaultLogStore` 只 `log.info`；**app-server 实际装配的 `SystemLogConfiguration` 里是委托 `LogApi` 的实现，经 RabbitMQ 异步落库到 `sys_operation_log`**（`OperationLogEntity extends SimpleTenantEntity`）。`saveApplicationLog`/`saveUrlLog` 没有对应的落库路径

### 测试

- 测试集中在三处：`platform-commons/commons-webapp/src/test`（主宿主：公共层、AI 示例等）、`platform-services/app-server/src/test`（API 级测试，如 `BizTypeApiTests`、`AttachmentApiTests`）、`platform-commons/commons-console`；命名 `*Tests`，JUnit 5
- 构建约定排除 `integration` 标签，但仓库里没有任何测试打该标签——即 `./gradlew test` 会实际执行依赖外部中间件的 SpringBootTest；基类（webapp 的 `BaseTests`/`BaseWebTests`）是 `@Transactional + @Rollback(false)`，**会污染本地库**
- **新测试优先使用 mock / 内存库（如 H2），不要依赖外部服务**
- CI（`.github/workflows/build.yml`）只在 push to main 时跑 `clean bootJar` 并把 `app.jar`/`admin.jar` 拷到 `dist`，**不运行测试**——改动公共层后请本地手动运行相关测试

## AI 模块架构与响应模式

### 1. 技术实现与模型供应商分离 (Service Provider vs Model Provider)

平台采用正交解耦的双层抽象，彻底分离「技术实现（底层协议/SDK）」与「模型供应商（厂商实体/凭证画像）」：

- **技术实现方案（`AiServiceProvider`，`commons/ai/enums`）**：`SPRING_AI_DEEPSEEK`、`SPRING_AI_OPENAI`、`AGENTIC_SPRING_AI_DASHSCOPE`、`ALIYUN_DASHSCOPE_SDK`、`TENCENT_HUNYUAN_SDK`、`OPENAI_SDK`、`CUSTOM`。关注网络通信、协议封包（OpenAI 兼容协议 / 厂商原生 RPC）、SSE Token 解析与 Advisor/Tool 适配。**`CUSTOM` 没有任何实现，也没有任何解析器会返回它。**
- **模型供应商（`AiModelProvider`）**：`OPENAI`、`DEEPSEEK`、`ALIYUN`、`TENCENT`、`ORCAROUTER`。每个枚举自带 `Model` 清单与 `AiModelType[]` 能力矩阵（`TEXT`/`EMBEDDING`/`IMAGE`/`AUDIO_TRANSCRIPTION`/`AUDIO_SPEECH`/`RERANK`），模型名按「精确名或 `前缀-` 前缀」匹配。关注商业身份、`apiKey`、`baseUrl`。
- **双轨调用体系**：
  - `AiManager` + `ai/factory/**ModelFactory`（面向 Spring AI 生态，产出 `ChatModel`/`ChatClient`，支持记忆/RAG/日志/Tool/Advisor 链）。按能力分子包：`chat`、`embedding`、`image`、`audio/{speech,transcription}`、`vectorstore`。
  - `AiServiceManager` + `ai/service/**ModelService`（面向原生 SDK 同步直连，无 ChatClient/advisor/vector-store 面）。子包同上，另加 `rerank`。
  - **模型实例无缓存**：`getModel(config)` 每次新建客户端，`getChatClient(config)` 每次新建 `ChatClient`；只有 factory 本身是单例。
- **配置结构**（`AiProperties`，前缀 `platform.ai`）：
  - `platform.ai.factory.*` / `platform.ai.service.*` 各自成对提供 `chat-service-provider`（引擎）与 `chat-model-provider`（厂商），其余能力同理（embedding/transcription/speech/image/rerank）。**两个键都要配，只配引擎会拿不到厂商。**
  - `platform.ai.providers.<id>.{enabled, commons.{api-key,base-url}, <能力>.name}`，map key 统一小写。**provider id 是自由文本**，yml 里配的 `tencent`/`orcarouter` 并不是枚举常量，解析走 `BaseEnum.getEnumByValue(..., default)`。
- **`AiManager` 的三级解析**（`AiManagerImpl`，chat 为例，其余能力同形）：
  1. 无参：按 `factory.chat-service-provider` 找已注册的 factory，找不到**直接抛异常**（无兜底）
  2. 按枚举：找不到时**只有这一级**会读 `platform.ai.fallback-enabled`（默认 `false`）决定回落还是抛
  3. 按 `ModelConfig`：校验 serviceProvider/name 非空，`factory.supports(config)` 通过才返回，否则**无条件**回落到第 1 级
  4. 各静态 `getXxxServiceProvider(...)` 解析器对未知值有硬编码默认（chat→`SPRING_AI_DEEPSEEK`，service 轨→`OPENAI_SDK` / `ALIYUN_DASHSCOPE_SDK`）
- 接入入口：演示宿主 `commons-webapp` 的 `ChatController`（`/chat/text`、`/chat/stream`，`@Anonymous`，仅测试环境）；正式用户端为 system 域 `AiChatWebController`（`@Authenticated`，前缀 `API_V1_PREFIX` 即 `/api/v1/ai/chat/*`，**不含 web 段**），经 `AiChatApiImpl` 按 `AiChatType`（CHAT/AGENT/KB/STATIC）取 `ChatClient`；业务模型实体经 `AiHelper` 转为 `SimpleModelConfig` 复用双轨。

### 2. AiResponseType 三种响应模式与执行链路

由 `AiResponseType`（`TEXT`/`JSON`/`STRICT`，默认 `TEXT`）驱动不同的提示词约束与数据处理管道，实现在 `commons/ai/utils/AiUtils`，入口是 `commons-webapp` 的 `ChatController` 与 `AiServiceImpl`（正式端点为 `AiChatWebController`）：

| 响应类型 | 约束机制 | 同步处理 | 流式处理 | 典型场景 |
| --- | --- | --- | --- | --- |
| **`TEXT`**（默认） | Prompt 注入：只输出 Markdown 正文，禁止任何卡片或围栏标记 | `spec.call().content()` 返回文本 | `spec.stream().content()` **原样透传**，不过状态机、不加 START/END 事件，首字延迟最低 | 纯文本问答、RAG 检索、长文创作 |
| **`JSON`**（围栏交互卡片） | Prompt 注入：Markdown 正文 + 末尾唯一一个 ` ```json-render ` 围栏 | **与 TEXT 走同一分支**（`content()` 返回图文混合文本），同步链路不区分 | `AiUtils.processStream` 滑动窗口状态机：正文以 `type="text"` 实时推送，尾部卡片完整积攒后一次性推入 json-render 块；`Flux.concat(START, flux, END)` 包装，未闭合卡片在流结束时恢复输出，异常降级 ERROR | 渐进式流式图文交互、答疑推荐卡片 |
| **`STRICT`**（严格结构化） | 强约束：基于 `UiComponentRegistry` 动态合成 JSON Schema，禁止输出非 blocks 内容 | `spec.call().entity(converter, validateSchema)` 强制 Schema 校验后序列化 | **并非逐 Token 流式**：同样走阻塞式 `call().entity(...)`，再按 `UiBlock` 切分成 `type="block"` 事件（START/END 包装，校验失败只返回 ERROR） | 动态表单、微前端组件驱动、高可靠 Agent 决策 |

`UiResponseSchema` 不是独立类，是 `UiComponentRegistry` 内的嵌套 `record`。

### 3. AI 周边能力与扩展入口

- **会话记忆**：`AiManagerImpl#applyBaseAdvisors` 挂 4 个 advisor——`CustomLoggingAdvisor`（order 0）、`CustomContextAdvisor`（order 150）、`SessionMetadataAdvisor`、`SessionMemoryAdvisor`。压缩参数在 `AiUtils.getSessionMemoryAdvisor`（不在 `applyBaseAdvisors` 里）：`TurnCountTrigger(20)` + `SlidingWindowCompactionStrategy.maxEvents(10)`。`SessionService` 默认内存实现（`AiUtils.getSessionService`），app 端被 `SystemAiConfiguration` 以 `CustomSessionRepository`（表 `sys_ai_session` + `sys_ai_session_event`，TTL 60 天）覆盖；会话续接校验「本人 + 本租户」。
- **长期记忆**：`AutoMemoryToolsAdvisor` + `AutoDreamAdvisor`，目录为 `platform.ai.memory.path/{tid}/{uid}`（`AiManagerImpl#applyMemoryAdvisor`，受 `platform.ai.memory.enabled` + `path` 非空双重门控）。
- **Agent 工具**：`platform.ai.agent.enabled` 门控下注册 `SkillsTool`，且 skills 与 agents 资源**都**能解析出 `Resource` 时才注册 `TaskTool`（子代理默认走 `builder.clone()`，与主链共享工具）。内置 `CommonTools`（`getVersion`/`getCurrentDateTime`）以 Bean + AOT（`@RegisterReflection` + `AiRuntimeHints` 双保险）注册；示例工具在 `commons-webapp` 的 `ai/tools/CoreTools`（ES 课程/讲师搜索）。
- **RAG**：全局 `RetrievalAugmentationAdvisor`（`allowEmptyContext(false)`，空上下文提示「找不到相关的内容。」）；KB 级经 `AiHelper.applyRagAdvisors`（`resolveDocumentRetriever` 带租户 `Filter.Expression`，可选重排）。向量化流水线 `AiVectorServiceImpl`（任务表 `AiKbTaskEntity` + `executeAfterCommit` 事务提交后异步 + 按 `batchSize` 分批），**重试退避机制在 `AiKbJob`/`AiKbApi`（`markPendingForRetry` + `nextRetryAt`），不在 `AiVectorServiceImpl` 里**。VectorStore 支持 ES/MariaDB/PgVector（`platform.ai.rag.store.type`，默认 `elasticsearch`）。
- **索引命名**：`AiRagUtils` 统一处理——ES 索引 `t{tenantId}-{prefix}-{collection}`，SQL 表 `t{tenantId}_{prefix}_{collection}`（截断 63 字符）。真正生效的配置是 `platform.ai.rag.store.elasticsearch.index-name` 与 `platform.ai.rag.store.{pgvector,mariadb}.table-name`；**`platform.ai.rag.store.index-prefix` 绑定了、被 `AiUtils` 拷贝一次，但从此再没被读，不参与任何索引名拼接**。是否加租户前缀由各 store 的 `prefixWithTenant` 决定（ES 默认 `false`）。
- **MCP**：system 域登记表（`AiMcpServerEntity`，权限 `dev:ai:config:mcp`）；Server 端鉴权走 `mcpServerApiKey()` + `CustomApiKeyRepository`（`findByKeyId` 委托 `AiApiKeyApi`）。模型密钥落库前经 `AiSecretUtils.encrypt`（`enc:` 前缀 + AES，读取时无前缀的按历史明文放行），更新时 `****` 掩码不覆盖旧值（注意实现是 `contains("****")` 子串判断，不是全等掩码）。
- **Embabel**：仅引入依赖（`embabel-agent` 1.5.2）与一个空测试 `EmbabelAgentTests`，**零 Java 源码引用**。
- **扩展任务**：新增厂商 → `AiModelProvider` 加枚举 + 录入 `ai_model` 表（`AiModelPltController`）或配 `platform.ai.providers.*`；新增引擎 → 实现 `*ModelFactory`/`*ModelService` 子接口并在对应 `AiAutoConfiguration`/`AiServiceAutoConfiguration` 注册；新增 UI 组件 → 实现 `UiComponentDefinition` 声明为 Bean（`propsSchema()` **必须内联，禁止 `$ref`**），`UiComponentRegistry.register` 是 synchronized 且会置空 `cachedJsonSchema`，缓存自动失效重建。

## 构建与依赖

- 新依赖优先在 `gradle/libs.versions.toml` 的 `[versions]` / `[libraries]` / `[bundles]` 中声明，不要在 `build.gradle.kts` 里裸写坐标
- **版本强制覆盖全部集中在 `buildSrc/src/main/kotlin/java-conventions.gradle.kts` 这一个文件**（全仓唯一含 `resolutionStrategy` / `dependencyManagement` 的地方）：27 个 `mavenBom` 导入 + 35 条 `resolutionStrategy.eachDependency` 规则（共 37 个 `useVersion` 与 1 个 `selectArtifact`）+ 34 条全局 `exclude`。**不要散落覆盖。**
- **BOM 导入顺序决定胜负**：Spring Boot 的 BOM 在第 37 行先导入，后导入的（如 Jackson 3.2.2、embabel）被它的约束压住。`libs.versions.toml` 里的 `jacksonVersion = 3.2.2` 实际不生效，运行时解析到 3.1.5。看到版本对不上先查这个。
- 约定插件共 4 个，链式叠加 `java-conventions` → `java-library-conventions` → `spring-boot-conventions` → `spring-boot-native-conventions`。实际套用是分档的：
  - `java-library-conventions`：commons-core、commons-core-starter、commons-javacv、commons-api、commons-starter、system-api、system-impl、system-security
  - `spring-boot-conventions`：commons-console、commons-webapp、admin-server、app-server
  - `spring-boot-native-conventions`：仅 commons-native
- 产物名固定（`bootJar.archiveFileName`）：app-server → `app.jar`、admin-server → `admin.jar`、webapp → `webapp.jar`、console → `console.jar`、native → `native.jar`
- **webapp 与 console 是「外壳 jar」**：`bootJar` 里 `exclude("*.jar")` 剔除内嵌依赖，改把 runtimeClasspath 复制到 `build/libs/libs-internal/`（`platform-*.jar`）与 `build/libs/libs-external/`（其余），并在 manifest 写 `Class-Path`。启动需要这些目录就位，不是单文件自包含包。app-server/admin-server/native 则是正常自包含包。
- `bootBuildImage` 配在 3 个模块：app-server、admin-server、commons-native，统一 `bellsoft/buildpacks.builder:musl` + `BP_JVM_VERSION=25` + CDS，镜像名 `boot-app-server`/`boot-admin-server`/`native`；后两个服务额外挂 `tools/buildpacks/bindings` 绑定。commons-webapp 与 commons-console 不配。
- 库模块的 Jar 任务统一 `exclude 'application*.yml'`。
- 模块依赖必须通过 Gradle `project(...)` 引用；`commons-core` 对外能力多为 `compileOnly`（运行时依赖由 starter / 应用模块提供），新增可选能力沿用该模式

## 已知问题

以下是**尚未闭环**的问题。改动相关代码前先确认是否已修复；修好一项就从这里删掉，不要留「已修复」条目。

**安全**

- `/exchange/**` 无鉴权（严重）：10 个 `*ExchangeController` 均有实际 MVC 映射但无任何方法级权限注解，落到兜底链 3（同样 `permitAll`），且**不返回 `R<T>`**；部分返回含密码哈希、OAuth2 客户端密钥等敏感字段（已用 `@JsonProperty(WRITE_ONLY)` 挡序列化）。`AuthorizationConsentExchangeController` 仍是空实现（`findByKey` 直接 `return null`），`ClientExchangeController#save` 空实现。
- 租户上下文信任请求头（严重）：请求头 tenantId 与根租户标识未与 JWT `tid` 校验——`SecurityConstants.JWT_KEY_TID` 全仓只被写 2 次、读 3 次，**没有一处做比对**。请求头驱动的 MDC 值直接决定 MyBatis 租户拦截器的过滤结果。
- `TenantContext.handleServletRequest` 的条件判定有缺陷：`if (isEmpty(tenantId) || isBlank(tenantRootInd))` 用的是「或」。前端只发 `x-tenant-id`、不发 `x-tenant-root-ind`，于是条件恒真，**调用方传入的 tenantId 被丢弃并整体走 resolver 重新解析**。本地联调因为域名兜底恰好解析到同一租户而没暴露。该方法也完全不读 `x-tenant-code`（那个 key 只在 `DefaultTenantResolver` 里处理）。
- `SecurityUtils#getTid` 在已认证但 JWT 无 `tid` claim 时返回 `null`（不像 `getUid` 那样有默认值），调用方未判空会 NPE。
- 操作日志把完整 `Authorization` 请求头（含 JWT）写进 `sys_operation_log.request_headers`，是日志脱敏缺口。
- `GlobalExceptionHandler`：`SystemException` / 兜底 `Exception` / 部分数据库异常仍把内部异常消息返回客户端，多数返回 HTTP 400；缺参处理会记录请求参数与请求头。

**数据与配置**

- `sys_catalog_relation.idx` 与 `sys_entity_relation.relation_index` 是全库仅有的两个异类：列是 `VARCHAR(2000) NOT NULL DEFAULT ''`，而其余 8 处 `idx` 都是 `INT NOT NULL DEFAULT 999`；实体声明 `Integer`，`CatalogRelationServiceImpl`、`EntityRelationServiceImpl` 也确实在写自增序号。写入侧靠 PG 赋值转换侥幸可用，读取侧遇到 `''` 会解析失败。**改实体还是改 schema 是决策项，未定。**
- 存在物理删除的调用点：`RoleServiceImpl`（`delete`/`deleteBatchById`）、`TenantServiceImpl`（`delete`），以及各关联服务的 `lambdaUpdateWrapper().remove()`。后属「数据层」的关联表例外，前两者不是——`sys_role`/`sys_tenant` 是主表且无删除审计。
- `sys_authority` **整张表没有任何种子数据**：`db_pgsql_schema_core.sql` 只有 DDL，`db_pgsql_tools.sql` 只有 truncate，`db_pgsql_data_core.sql` 只种 10 张表（`sys_client`/`sys_entity_relation`/`sys_identity`/`sys_label`/`sys_lang`/`sys_organization`/`sys_package`/`sys_position`/`sys_tenant`/`sys_user`），**不含 `sys_authority`**。定义权限树的 `AuthorityTreeEnum`（`system-api/.../commons/enums/AuthorityTreeEnum.java`）**全仓零引用**，是死代码。后果：全仓 40 个被 `hasAnyAuthority('...')` 引用的权限码在库里**一个都没有**，对应端点对所有非 admin 用户恒 403（admin 经 `*:*:*` 短路放行）。已实测 403 的代表：`system:client`、`system:client:view`、`system:client:delete`、`system:client:chcek`（**拼写错误，应为 `check`**，改对拼写前先确认前端菜单是否也照抄了这个错码）、`open:wechat:cp`、`open:wechat:ma`、`open:dingtalk`、`open:lark`、`resource:address:view`、`resource:address:delete`。**补数据要先决定初始化方案（用 `AuthorityTreeEnum` 生成还是手写 SQL），属架构级改动，不要直接往 `db_pgsql_data_core.sql` 里塞。**
- 前端调用但后端不存在的端点属产品决策范围，不要顺手补实现。清单见 `one-ui-vue/AGENTS.md`；已闭环的是会员域（`/api/v1/sys/account/**`、`/api/zp/v1/sys/account/**`）与招聘菜单残留，剩余待决策项为 `/sys/role/users`、`/sys/ai/tool/delete`、`/ai/chat/completion`、`/oapis/wecom/signature`。
- `tools/database/pgsql/db_pgsql_schema_core.sql` 是含 `DROP TABLE` 的初始化脚本，不是生产增量迁移方案；仅限本地开发使用。

**死配置与死代码**（都是「配了/写了但没人读」，清理时逐条确认再删）

- `platform.jwt.authorization-code-time-to-live` / `device-code-time-to-live`：`JwtAutoConfiguration#config()` 只搬 access/refresh 两个值，这两个 key 声明了但从未被使用（值恰好与 `JwtConfig` 默认相同，所以看不出症状）
- `platform.ai.rag.store.index-prefix`：绑定了、被 `AiUtils` 拷贝一次，之后再无读取，不参与索引名拼接
- `platform.ai.agent.prompts`：绑定了，`applyAgentTool` 只读 `agents`/`skills`
- `app-server/src/main/resources/messages/messages_*.properties`（含 `app_title`）：**从未注册进 `MessageSource` 的 basename**（basename 是 `classpath:i18n/system/label/messages`），是孤儿文件
- `application-native.yml` 里的 `platform.ai.vectorstore.*`：前缀与实际声明的 `platform.ai.rag.store.*` 不一致
- `application-local.yml` / `application-development.yml` 里的 anthropic provider 与 `embabel.agent.platform.*`：无任何实现读取（`AnthropicChatModelFactory`、`AiModelProvider.ANTHROPIC` 均已不存在）
- `AiServiceProvider.CUSTOM`：无实现，且没有任何解析器会返回它
- `AbstractEntityController`：继承 `AbstractController` 但全仓无人使用
- `TenantContext` 的两个 `ThreadLocal` 字段（`tenantId`/`tenantRootInd`）：声明后从未读写，真实存储是 MDC
- `PageRequest#check(List<String>)`：全仓无调用方，且逻辑自相矛盾（`isEmpty` 分支里对刚判空的集合调 `stream()`，非空分支直接 `return false`）
- `BaseEntityRepository` 的第二个类型参数 `K`：mybatis 版接口体内完全未使用，`BaseEntityService<T, K, R>` 的 `K` 同样是幽灵参数
- `MongoCustomProperties`、`platform.rate-limit`：有属性类无自动配置条目
- `commons-webapp` 的 `logback-spring.xml` 没有 include 平台 `base.xml`（直接用 Spring Boot 的 base），且只配了 console——非 dev/local profile 下没有 root appender

**行为与运行时**

- 全局 `INDENT_OUTPUT`：`CoreAutoConfiguration#jsonMapperBuilderCustomizer` 无条件开了 pretty-print，没有 `@Profile`、没有环境开关、yml 里也没有 `spring.jackson.serialization.indent-output` 覆盖。**所有 API 响应都是带缩进的**，列表型端点的体积与序列化 CPU 都有可观开销，上生产前应评估。
- 登录会话没有关闭链路：`UserApiImpl#logout()` 只打日志，`CustomOAuth2AuthorizationService#remove()` 不发 DELETE 事件，`sys_login_session.end_datetime` 永不落库、`last_access_datetime` 只在登录/刷新时更新 → 在线数只能按 `ONLINE_WINDOW`（1 小时）时间窗近似。
- `NettyWebSocketServer.destroy()` 关闭条件写反（以 `isShutdown()` 作为发起关闭的前置条件，且父组条件混入子组状态），事件循环线程组不会优雅关闭。
- 登录主体构造使用的简化构造器将账户状态布尔值置 true：数据库的禁用/锁定状态未完整映射到登录主体校验。
- `CustomLocaleResolver.resolveLocale` 硬编码 `GlobalConstants.DEFAULT_LOCALE`，**完全忽略 `Accept-Language`**；叠加 `MessageSource` 的 `useCodeAsDefaultMessage(true)`，缺翻译静默显示 key 不报错。
- `spring.main.allow-circular-references` 与 `allow-bean-definition-overriding` 同时开启（历史包袱；**请勿新增循环依赖或同名 Bean 定义**）。已经埋雷的例子：app-server 同时扫描 `cc.wdev.platform.base` 与 `cc.wdev.platform.security`，而两个 `JwtAuthenticationConverter` Bean 实现相同，靠覆盖开关决定谁生效。
- `ClientPltController` 属 `*PltController` 却使用 `API_V1_SYS_PREFIX`（`/api/v1/sys/**`），与「控制器与 URL 约定」不符；前端已按现状 sys 路径实现，迁移需双侧同步。
- `DataSourceCustomAutoConfiguration` 的 `@Primary dataSource` 接收 master/slave 两个参数但**直接返回 master**，无自动读写路由。
- `LoginSessionRepository.xml#getPlatformPieChart` 无端点、无消费方；`getCountByPlatform` 的列名已修正为 `client_name`，接入前仍需另测。
- `BaseEntityService#softDeleteBatch` 对裸 `AbstractEntity` 实体是**静默空操作**（不更新字段却仍发 UPDATE），误用不会报错，只会「删不掉」。

**构建与 CI**

- `tools/actions/build-native.yml` 与 `tools/actions/deploy.yml` 引用的 Gradle 路径 `platform-boot-server:app-server` / `platform-boot-server/app-server` 已过期，实际是 `platform-services:app-server`。
- `.github/workflows/build.yml` 只 build 不 test。
- `libs.versions.toml` 的 `jacksonVersion`（3.2.2）被 Spring Boot BOM 遮蔽，实际不生效。
- webapp/console 的 `bootJar` manifest 块里有 `println(it.name)`，每次配置阶段打印全部依赖名，属遗留调试语句。

## 禁止事项

- 不要提交真实密钥/口令；配置只允许占位或 `${ENV_VAR}` 引用
- 不要修改或提交 `application-local.yml`、`application-production.yml`（已 gitignore，属本地/生产私有配置）
- 不要删除 `tools/deploy/binaries/` 下的本地部署产物（目录已 gitignore，仅存 `.gitkeep`）
- 不要使用 `System.out.println` / `printStackTrace`（测试辅助除外）
- 不要在业务代码中写 `Thread.sleep`、空循环等待
- 不要通过 `apply_patch` 之外的方式（如 shell 重定向）改写代码文件
- 不要为了让编译通过去改 `gradle/wrapper/gradle-wrapper.properties` 的发行版
- 不要把 `GlobalConstants` 里的密码类配置写进任何入库文件

## 新增业务功能的推荐步骤

### 完整样例（推荐先照抄一条真实链路再改）

`config` 域是最完整的参考实现（`dict` 同构）：
`system-api/.../config/api/ConfigApi`（`@HttpExchange` + Request/Form/VO/strategy）→ `system-impl` 中 `ConfigApiImpl`（`@Service`）+ `ConfigExchangeController`（HTTP 暴露）→ `ConfigSysController`（extends `AbstractController`，方法级 `@PreAuthorize("hasAnyAuthority('system:config')")`，写操作加 `@OperationLog`）→ `ConfigServiceImpl extends BaseCachingEntityService`（实现 `getCacheKeyGenerator()`，scope 走 `BizTypeApi` 判定，见「分层与包结构」）→ `ConfigRepository`（`@Mapper interface ... extends BaseEntityRepository<ConfigEntity, Long>`，简单 CRUD 无需 XML）。`dict` 域结构相同，可作第二参考。

### 步骤

1. 在 `system-api` 中定义 `XxxApi`（需要跨服务暴露时加 `@HttpExchange`）与 DTO/Form/Request/VO
2. 在 `system-impl` 中实现 `XxxApiImpl`（`@Service`）与 `XxxService` / `XxxServiceImpl`，主表实体继承 `BaseTenantEntity`（关联表继承 `SimpleTenantEntity`），转换用 MapStruct
3. 若需 HTTP 暴露，新增 `XxxExchangeController`——**先与维护者确认鉴权方案**（见「安全」）
4. 新增 `XxxRepository`（`@Mapper interface ... extends BaseEntityRepository<T, Long>`，**注意 import 的是 mybatis 那个**），确有自定义 SQL 才写 XML 并放到 `resources/repository/system/`
5. 新增 `XxxSysController` / `XxxWebController`，**每个端点必须加 `@PreAuthorize` 或 `@Authenticated`**，返回 `R<T>`
6. 涉及用户可见操作时加 `@OperationLog`；列表接口分页并返回 `R<Page<...>>`
7. 需要 i18n 文案时加到 `i18n/system/label/messages*.properties`（key 用 `label__<group>__<value>` 约定），不要在代码里拼中文
8. 在 `commons-webapp` 或 `app-server` 补充测试（优先不依赖外部中间件）
9. 若新增了权限码，**先确认 `sys_authority` 有对应数据**（见「已知问题」——目前没有种子数据）
10. 同步更新本文对应章节

### 新增整个业务域时额外要建的

`system-api` 与 `system-impl` 两侧各建同名包（`cc.wdev.platform.system.<domain>`），`domain/{dto,form,request,vo}` 在 api 侧、`domain/{entity,converter}` 在 impl 侧；若该域需要类型注册表，实现一个 `BaseBizTypeEnum` 即可，启动时 `BizTypeApiImpl#initialize()` 会自动扫到。

## 横切能力导航（按任务查入口）

| 能力 | 配置树 | 代码入口 |
| --- | --- | --- |
| 对象存储 | `platform.storage` | commons-core `storage` + system `storage` 附件域（`AttachmentApiTests` 在 app-server） |
| 定时任务 | 标准 `spring.quartz.*` + `spring.datasource.job` | system `job` 域 + `QuartzCustomAutoConfiguration`（无 `platform.*` 开关） |
| 消息/WebSocket | `platform.message.rabbit/broadcast`、`platform.websocket` | commons-core `message`（Rabbit/WebSocket/SSE/Netty）+ system `message` 域 |
| 审计日志 | `platform.log` | logback 模板 `commons-core/src/main/resources/cc/wdev/logging/logback/`；切面 `commons.core.log.aspect`；`@OperationLog` → RabbitMQ → `sys_operation_log` |
| 国际化 | `i18n/system/{label,validation}/messages*.properties` | `MessageSourceUtils`、`sys_label`/`sys_entity_label`、`CustomLocaleResolver`（**当前忽略请求语言**） |
| 序列化 | — | `commons-core/utils/jackson/CommonModule`（Long→String、日期）+ `CoreAutoConfiguration`（`NON_NULL` + 全局 `INDENT_OUTPUT`） |
| 开放平台 | `platform.oapis.*`、`platform.sms`、`platform.translator`、`platform.face-body` | commons-core `oapis`（微信/钉钉/飞书/Telegram/短信/翻译/人脸），默认全关 |
| 数据源/分库 | `platform.data.datasource.*` | `DataSourceCustomAutoConfiguration`（master/slave/job；`@Primary` 直接返回 master，无自动读写路由） |
| 验证码/IP/敏感词 | `platform.captcha/http/keyword/sensitive` | commons-core `extensions`（注意 `platform.parser` 默认开） |
| 业务类型注册 | — | `BizTypeApi` / `sys_biz_type`，启动时由 `CoreApiImpl` 触发扫描入库 |
| 前端资源 | — | 无独立 Node 工程；静态资源在 `commons-webapp` 与 `app-server` 的 `resources/public` + Thymeleaf `templates/` |

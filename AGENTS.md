# Agents.md

本文件是 **one（后端）** 仓库的长期架构索引与协作约定，供 AI 编码助手直接复用；与前端仓库 `one-ui-vue/AGENTS.md` 配套维护。开工前先读「日常协作规范」「安全红线」「禁止事项」，再按任务定位相关源码。

本文只记录**当前源码事实**与**长期约定**，不记录修复流水账：已闭环的问题直接删除；架构变化时改对应章节，确实未修的留在「已知问题」。**源码与本文冲突时以源码为准。** 文档描述不代表服务已启动、测试已通过或部署已验证。

## 目录

- [0. 日常协作规范](#0-日常协作规范)
- [1. 项目概述与技术栈](#1-项目概述与技术栈)
- [2. 常用命令](#2-常用命令)
- [3. 运行配置（Profile）与能力开关](#3-运行配置profile与能力开关)
- [4. 模块地图与业务域](#4-模块地图与业务域)
- [5. 控制器与 URL 约定](#5-控制器与-url-约定)
- [6. 前后端协同](#6-前后端协同与前端仓库-one-ui-vue)（6.1 契约速查 · 6.2 本地联调 · 6.3 启动验证 · 6.4 调试）
- [7. 代码约定](#7-代码约定)（7.1 分层 · 7.2 实体与软删 · 7.3 响应与异常 · 7.4 安全 · 7.5 数据层 · 7.6 缓存 · 7.7 序列化 · 7.8 i18n · 7.9 日志 · 7.10 测试）
- [8. AI 模块架构与响应模式](#8-ai-模块架构与响应模式)
- [9. 构建与依赖](#9-构建与依赖)
- [10. 已知问题](#10-已知问题)
- [11. 禁止事项](#11-禁止事项)
- [12. 新增业务功能与横切导航](#12-新增业务功能与横切导航)

## 0. 日常协作规范

**开工**

1. 先 `git status --short`，保留用户已有修改与未跟踪文件，不把它们当成自己的产出。
2. 只读本文件与任务涉及的模块、配置、调用方与测试；跨模块调整再沿依赖扩展阅读。
3. 记录稳定的文件路径与类/方法名，不依赖易漂移的行号、类数量、完整依赖版本清单。
4. 明确区分「已实现」「示例/依赖接入」「待验证」；不要把历史缺陷当模板，也不要把本地配置、构建产物、未提交测试当成已提交能力。

**改动顺序（前后端同一任务）**

1. 后端先行：`system-api`（DTO/Form/VO/枚举/`@HttpExchange`）→ `system-impl`（Entity/Converter/Repository/Service/ApiImpl/Controller）→ `system-security`（仅涉及认证鉴权时）。
2. 契约变更同步：先改后端，再改前端 `one-ui-vue`，最后同步两份 `AGENTS.md` 的「契约速查」（两份表逐行一致）。
3. 数据库变更：同步追加到 `tools/database/pgsql/db_pgsql_schema_core.sql`。
4. 改配置键、加端点、调协议时，同步更新本文件对应章节。

**验证顺序**（按影响面从小到大；Windows 用 `.\gradlew.bat`）

1. 编译：`./gradlew :platform-services:app-server:compileJava`（改后端后最快的验证）。
2. 单元测试：优先跑受影响模块；`./gradlew test` **谨慎**（会实跑依赖外部中间件的 SpringBootTest，见 7.10）。
3. 启动：`./gradlew :platform-services:app-server:bootRun`（约 1–2 分钟，8181 监听即就绪）。
4. 接口：`curl -k -X POST https://wdev.cc/api/v1/initialize` 应返回 `code:200`。
5. 前端联调：浏览器访问 `https://wdev.cc/webapp` 验证页面功能。

**交付与维护**

- 报告实际执行的检查与失败/未执行项；未经用户要求不 commit、不 push、不部署。
- 已闭环的问题从「已知问题」删除，不留「已修复」条目；新缺口写进对应章节或「已知问题」。

## 1. 项目概述与技术栈

"one" 是自研基础开发平台（单体架构，预留微服务拆分），提供后台管理端（sys）、平台端（plt）、用户端（web）三类 API，集成 OAuth2 认证授权、多租户、多数据源、缓存、消息（RabbitMQ / WebSocket / SSE）、对象存储、Elasticsearch、Quartz，以及 Spring AI 多厂商模型接入与微信 / 钉钉 / 飞书 / 短信 / 翻译等开放平台能力。

- Java 25（`java-conventions` 同时设 `sourceCompatibility`/`targetCompatibility` 与 toolchain 为 25，编译参数 `-parameters`）
- Gradle 9.8.0（wrapper；依赖版本集中在 `gradle/libs.versions.toml`；构建约定在 `buildSrc`）
- Spring Boot 4.1.1、Spring Security 7.1（Authorization Server + Resource Server）、Spring AI 2.0.1、Spring Cloud（预留）
- 持久化：MyBatis-Plus 3.5.17（主）、JPA/Hibernate（遗留与示例）、PostgreSQL
- 中间件：Redis（Redisson）、RabbitMQ、Elasticsearch、Quartz
- JSON：**序列化走 Jackson 3**（`tools.jackson.databind.*`），注解仍用 `com.fasterxml.jackson.annotation.*`（Jackson 3 刻意保留该注解包，不是泄漏）。classpath 上**同时存在 Jackson 2.21.5**（elasticsearch-java、tika、openai-java、arrow、geoip2、swagger-core 等第三方传递依赖拉入），它不参与 HTTP 消息转换。
- 其他：CosId（雪花 ID）、Hutool、MapStruct、Lombok、springdoc-openapi、JavaCV/FFmpeg、GraalVM Native（实验）、embabel-agent（**仅依赖，无业务接入**）

## 2. 常用命令

Windows 用 `.\gradlew.bat`，Linux/macOS 用 `./gradlew`（不要写成 `./gradlew.bat`，cmd 下会报「'.' 不是内部或外部命令」）。

- 全量构建：`./gradlew clean bootJar`（产出 `platform-services/*/build/libs/*.jar`）
- 构建主服务：`./gradlew :platform-services:app-server:bootJar`
- 编译主服务源码（最快验证）：`./gradlew :platform-services:app-server:compileJava`
- 测试宿主模块测试：`./gradlew :platform-commons:commons-webapp:test`
- 全量测试：`./gradlew test`（**谨慎**，见 7.10）
- 本地启动主服务：`./gradlew :platform-services:app-server:bootRun`（或 IDE 运行 `AppServerApplication`；JDK 25 下非 public 的 main 是合法入口，不据此判断启动失败）
- 初始化本地数据库：`tools/database/bin/pgsql_init.cmd`（Windows）/ `pgsql_init.sh`（Linux）。**警告：脚本链含 `DROP DATABASE` / `DROP TABLE` / `TRUNCATE`，重跑整库清空，仅用于本地**；SQL 在 `tools/database/pgsql/`，新建表必须同步 `db_pgsql_schema_core.sql`。
- CI 产物复制脚本：`tools/scripts/build.sh` / `build.cmd`
- wrapper 发行版本地未缓存且下载卡住时，可用 `~/.gradle/wrapper/dists/` 下已缓存的版本直接调 `gradle.bat`；**不要改 `gradle-wrapper.properties`**。

## 3. 运行配置（Profile）与能力开关

- 默认 profile `local`，**五个应用模块都硬编码 `spring.profiles.active: local`**。
- `application-local.yml`、`application-production.yml` 已 gitignore（本地/生产私有配置，不入库）；`application.yml`、`application-development.yml` 入库；`application-native.yml` 只在 app-server 与 commons-native。
- 端口：app-server `8181`、admin-server `8282`、commons-webapp `8080`；commons-console（`web-application-type: none`）与 commons-native **不监听端口**。
- app-server 显式关闭 Consul。

`platform.*` 能力开关全部定义在 `commons-core-starter` 的 `autoconfigure/**`，全仓没有别处的 `@ConditionalOnProperty`。**判读要区分两层语义**：`@ConditionalOnProperty(matchIfMissing = true)` 只决定自动配置是否装配；properties 类里的 `boolean enabled` 字段默认值往往仍是 `false`。**大量「默认开」的能力其实是 Bean 在、功能关**，须显式 `platform.xxx.enabled=true` 才生效（`cache`/`mail`/`sequence`/`storage`/`template`/`web`/`captcha`/`data.mybatis`/`message.broadcast`/`ai` 属此类）。

| 档位 | 配置键 |
| --- | --- |
| 条件默认装配（`matchIfMissing=true`） | `async`、`cache`、`jwt`、`log`、`mail`、`sequence`、`storage`、`template`、`web`、`data.mybatis`、`captcha`、`http`、`keyword`、`sensitive`、`message.rabbit`、`message.broadcast`、`websocket`、`ai` |
| 无条件装配 | `parser`（`ParseAutoConfiguration` 无 `@ConditionalOnProperty`，`ParseProperties.enabled` 默认 `true`） |
| 需显式 `enabled=true` | `data.core`、`data.jdbc`、`data.datasource`（含 `.master`/`.slave`/`.job`）、`data.jpa`、`data.elasticsearch`、`swagger`、`ip`、`selenium`、全部 `oapis.*`、`sms`、`translator`、`face-body`、`test` |

关键点：

- **不存在 `platform.jdbc`**；jdbc 模板前缀是 `platform.data.jdbc`。
- **`platform.tenancy.enabled` 的实际语义**：`TenantConfig.enabled` 字段默认 `true`，但真正生效的两个 Bean（`MyBatisCustomAutoConfiguration` 的 `META_OBJECT_HANDLER_PREFIX`、`JpaCustomAutoConfiguration`）用**不带 `matchIfMissing` 的 `@ConditionalOnProperty`**，不显式配就不装配租户拦截器。app-server 的 `application-local/development/native.yml` 都显式配 `true`——本地与联调按租户过滤是常态，判断「数量对不上」先想租户作用域。
- **JWT 有效期实际是 60 分钟 / 刷新 14 天**（`platform.jwt.access-token-time-to-live` / `refresh-token-time-to-live`）。`JwtConfig` 自己的 `@Builder.Default`（15 分钟 / 3 天）会被 `JwtAutoConfiguration` 用 `JwtProperties` 无条件覆盖，**看默认值要看 `JwtProperties`**。
- Quartz **没有 `platform.*` 开关**，走标准 `spring.quartz.*`（`QuartzCustomAutoConfiguration` 只判 `@ConditionalOnClass`）；job 域独立数据源 `spring.datasource.job` 属 `platform.data.datasource.job.enabled=true` 的可选项，不开则回退主数据源。
- 自动配置注册表：`commons-core-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（无旧式 `spring.factories`）。

## 4. 模块地图与业务域

`settings.gradle.kts` 共 13 个子项目，无 `projectDir` 重映射：

```
one (Gradle root, group cc.wdev)
├── platform-commons
│   ├── commons-core          平台核心库：R/异常/枚举/工具、数据层抽象（MyBatis/JPA/ES）、缓存、多租户、
│   │                         日志（LogStore/切面）、消息（Rabbit/WebSocket/SSE）、存储、序列、扩展
│   │                         （验证码/IP/敏感词/HTTP/parser）、序列化（CommonModule）、AI 抽象、开放平台 SDK
│   ├── commons-core-starter  commons-core 的自动装配层 + 所有 platform.* 属性类
│   ├── commons-javacv        JavaCV / FFmpeg / Tesseract 原生依赖封装（多平台 classifier）
│   ├── commons-console       Spring Shell 控制台应用（console.jar，无端口）
│   ├── commons-native        GraalVM Native Image 验证应用（native.jar，无端口）
│   └── commons-webapp        开发/测试宿主：JPA、MyBatis-Plus、ES、AI 示例；主要测试宿主
├── platform-modules
│   ├── commons/commons-api       空聚合模块（仅转出 system-api，无源码）
│   ├── commons/commons-starter   平台级 starter：安全默认配置、HttpExchange 客户端代理、MessageSource
│   └── system
│       ├── system-api        系统域 API：全部业务域的 @HttpExchange 接口（XxxApi）+ DTO/Form/Request/VO/BO、枚举、常量
│       ├── system-impl       系统域实现：Controller、XxxApiImpl、Service/Impl、Repository（Mapper）、Entity/Converter、i18n 资源
│       └── system-security   OAuth2 授权服务器 + 资源服务器、自定义认证（密码/OTP/社交以 Converter+Provider）、JWT、验证码过滤器
└── platform-services
    ├── admin-server          Spring Boot Admin 服务端（8282）
    └── app-server            主业务服务（8181）：聚合 commons-starter + system-impl + system-security
```

**同名/易混类**：

- `WebSecurityConfiguration` 全仓 3 个（system-security 生效的那个、admin-server、commons-webapp）。改安全配置先看 import。
- `BaseEntityRepository`（3 个同名，别混用 mybatis/jpa/elasticsearch）、控制器基类 `AbstractController`、死类 `AbstractEntityController` → 详见 7.1。

**业务域**：`system-impl` 下 18 个业务域——`ai`、`catalog`、`commons`（验证码/首页）、`config`、`core`（用户/角色/权限/租户/登录会话）、`dev`、`dict`、`i18n`、`im`、`job`（Quartz）、`log`、`message`、`open`（微信/钉钉/飞书）、`region`、`security`（OAuth2 客户端/授权/AppKey）、`site`（公告/横幅/友链）、`storage`（附件）、`tag`；另有 `configuration` 包（各域 `@Configuration` 基础设施的汇总，不是业务域）。

## 5. 控制器与 URL 约定

| 分类 | URL 前缀 | 说明 |
| --- | --- | --- |
| `*SysController` | `/api/v1/sys/**` | 后台管理端 |
| `*PltController` | `/api/v1/plt/**` | 平台端（运维/开发者） |
| `*WebController` | `/api/v1/web/**` | 用户端 |
| `*ExchangeController` | `/exchange/**` | 内部服务间调用（微服务预留） |
| MCP Server | `/api/mcp` | Spring AI MCP |

- **以各控制器方法上的映射常量为准**：`*WebController` 不一定都在 `/api/v1/web/**`，如 `AiChatWebController` 实际用 `API_V1_PREFIX`（`/api/v1/ai/chat/*`，无 web 段）；`ClientPltController` 走了 sys 前缀（已知偏差，见 10）。
- `*ExchangeController` **不返回 `R<T>`**，直接返回裸类型（见 7.3）；`XxxApi`/`XxxApiImpl` 与 exchange controller 的对应关系见 7.1。

## 6. 前后端协同（与前端仓库 one-ui-vue）

配套前端仓 **`one-ui-vue`**（本地路径 `D:\\Workspace\\github\\one-ui-vue`，独立 Git 仓库；pnpm workspace：`packages/webapp` 桌面管理端、`packages/mobile` 移动端、`packages/mp` 小程序、`packages/commons` 共享层）。其内部结构与任务地图见前端仓的 `AGENTS.md`。协同开发约定：

- 先查 6.1 契约速查表定位两端入口，再分别读两侧源码；只有速查表无法解释时才扩大检索。
- 修改任何对外契约（URL、参数名、响应结构、认证协议）时：**先改后端 → 再改前端 → 最后同步两份 `AGENTS.md` 的契约速查（两份表逐行一致）**。
- 本地联调标准方式是 Nginx + `wdev.cc` 同源反代（见 6.2），**无需改前端 `VITE_APP_SERVER`、vite proxy 或注入租户头**；前端仓库自身没有 mock/proxy。

### 6.1 契约速查（与前端逐行一致）

| 契约 | 后端事实（one） | 前端消费点（one-ui-vue） |
| --- | --- | --- |
| 响应体 | `R{code,message,data}`，成功码 200（`ResponseCodeEnum.SUCCESS`），`data` NON_NULL；Jackson 会把 `isSuccess()` 序列化为只读 `success` 布尔字段（无 timestamp）。**`/exchange/**` 例外，不包 `R`** | `commons/src/types/common.ts` 的 `R` 类；HTTP 层不解业务码，调用方手工判 `code==200` |
| 分页请求 | `PageRequest`：`page` 从 1 起、`size` 默认 10、`sort` 单字段、`order`、`q`；`getPageable()` 内部转 0 基 | `data-table` page 从 1 直发；`data-list` 内部从 0 起、请求时 +1，最终与后端一致 |
| 分页响应 | 原生 Spring Data `Page` JSON（content/pageable/totalElements/last…），无自定义序列化；**但 `CommonModule` 把 Long 序列化成字符串，故 `totalElements` 等是字符串** | `PageResponse` 类型，字段全可选；数值需 `Number()` 转换 |
| 登录 | `POST /oauth/token` 表单：`grant_type=password`、`username`、`password`（前端明文直传）、`scope=openid`、`client_id/client_secret`（Basic 亦可用，客户端存 `sys_client` 表，密钥为 `{noop}<明文>` 且与实体缓存联动）；验证码走 `captcha_key/captcha_value`，仅 password grant 且 `LOGIN_CAPTCHA_ENABLED` 时由 `CaptchaAuthenticationFilter` 校验 | `modules/core/api/commons/auth.ts` + `VITE_APP_OAUTH_*` 注入 |
| 令牌有效期 | 访问令牌 **60 分钟**、刷新令牌 **14 天**（`platform.jwt.*`；`JwtConfig` 自身的 15min/3d 默认值不生效） | 401 拦截器单飞刷新重放 |
| 刷新/登出 | 标准 `grant_type=refresh_token` 走 `/oauth/token`；登出 `POST /api/v1/user/logout`；token 响应为 OAuth2 标准顶层字段 | `refreshApi` |
| 用户资料 | `GET /api/v1/user` 返回 `UserInfoDto`（含 tenantId、roles、authorities 等） | `UserStore` 登录后拉取；`tenantId` 成为租户头来源 |
| 租户协议 | 请求头 `x-tenant-id`（数值优先）/ `x-tenant-code` / `x-tenant-root-ind`（`GlobalConstants`），resolver 兜底按域名，响应头回写 | `http.ts` 拦截器 `headers.set('x-tenant-id', user.tenantId)` |
| 验证码 | `POST /api/v1/captcha/code` 返回 `{key,image}`；另有 `/captcha/mail`、`/captcha/sms` 及各 `/check`（`CaptchaController`） | 两端 captcha 组件与 `captcha.ts` |
| 应用初始化 | `POST /api/v1/initialize`（匿名）返回 `R<InitializeVo>`（登录验证码开关、访问限制、app.title/webSocketServer 等） | `base.ts` → `AppStore.initialize` |
| 工作台/仪表盘 | `GET /api/v1/sys/get-system-info`（`registerCount` 走租户过滤的 `UserApi#getCount`，`onlineCount` = 未结束且 1 小时内有访问的会话去重用户数——该窗口宽于 60 分钟的访问令牌 TTL，是会话式活跃近似，见 `LoginSessionServiceImpl.ONLINE_WINDOW`）、`GET /api/v1/sys/login-platform-line-chart?type=1\|3\|4&date=&goHeavy=`（均 `system:workbench`，`DashboardSysController`） | webapp `modules/core/views/workbench.vue`（着陆页 `/home`），`api/platform.ts` 的 `platformCountsApi`/`loginPlatformLineChartApi` |
| 权限码来源 | 方法级 `hasAnyAuthority('x:y')` 的字符串必须存在于 `sys_authority.code`，否则非 admin 用户一律 403（JWT 权限由 `CustomJwtGrantedAuthoritiesConverter` 查库拼装，不做前缀推导）。admin 经 `*:*:*` 根权限短路放行。**仓库没有任何 `sys_authority` 种子数据**，见「已知问题」 | 前端菜单/路由的 `authorities` 同样按字符串精确匹配 `sys_authority.code`（组级菜单用 `group:xxx`，模块级用 `module:xxx`），缺失即菜单不可见 |
| AI 流式 | `POST /api/v1/ai/chat/stream`，`text/event-stream`，`Flux<String>`；请求体自定义 `SimpleChatRequest`（prompt/conversationId/modelCode/agentCode/kbId/chatType/withRag），非 OpenAI 格式；同前缀还有 `/text`、`/list`、`/details`、`/start`、`/delete` | `utils/ai.ts` `TextStreamChatTransport` 直连 `/api/v1/ai/chat/stream`，不走 axios（需自带 `Authorization` 与 `x-tenant-id`） |
| WebSocket | 两条通道：主服务（8181）servlet 注册 `/ws/message`（`WebSocketHandlerConfiguration` + `SystemWebSocketHandler`，握手走 Spring Security；`DefaultBearerTokenResolver.setAllowUriQueryParameter(true)` 使 query `access_token` 可用）——前端实际使用这条；另有 Netty 独立端口 8686 路径 `/ws`（query 参数名为 `authorization`，`NettyUtils.getAuthorization`） | `store/socket.ts` 连 `${server}/ws/message?access_token=...`，server 优先 initialize 返回的 `app.webSocketServer` |
| 前缀消费现状 | `/api/v1/sys\|plt\|web` 常量齐备；前端目前只调用 sys、web（banner/announcement/address）与无前缀端点，`/api/v1/plt/**` 尚无消费者 | `modules/*/api/*.ts`（注意 `api/platform.ts` 实际也调 `/api/v1/sys/**`） |

### 6.2 本地联调环境（Nginx + wdev.cc）

- Windows hosts 将 `wdev.cc`/子域映射到 127.0.0.1（`# fetch-wdev-hosts` 标记块）；443 为本地证书，curl 加 `-k`。
- Docker 容器 `nginx`（`D:\\Data\\compose\\nginx\\config\\nginx.conf`）按 `server_name wdev.cc *.wdev.cc` 反代：`/webapp`→8081、`/mobile`→8082，`/api`、`/oauth` 等其余→8181，`/ws`→8181（query `token` 映射为 Authorization，与前端 `access_token` 并存）；另有 `/devops/` 运维入口。
- **租户实际按 Host 域名解析**：前端只发 `x-tenant-id`、不发 `x-tenant-root-ind`，而 `TenantContext.handleServletRequest` 条件用「或」，常走域名兜底（见 §10）。换租户**改 Host，不要只改请求头**；IP/localhost 直连报 1003002，租户过期是 1003004（数据状态，非链路故障）。
- vite 端口被占会顺延，nginx upstream 固定 8081/8082——联调前确认这两个端口只有目标 dev server。
- **OAuth2 密钥**：`sys_client.client_secret` 为 `{noop}<明文>`（`{id}` 前缀由 `DelegatingPasswordEncoder` 识别）。env 与库不一致时 `POST /oauth/token` 返回 401 `invalid_client`，**先于用户口令校验**，勿误判为密码错。开发种子库与 webapp `.env.dev` 一致；sit/pro 与 mobile env 指向真实环境、密钥以维护者为准，不要猜改。
- **不要只用 SQL 改 `sys_client`**：`ClientServiceImpl` 走 `BaseCachingEntityService`，SQL 直写不会失效缓存；改完必须删 Redis（db 11）`client:code_<clientId>`，否则登录仍 401。

### 6.3 AI 自主开发基础流程（启动与验证清单）

1. 前置：JDK 25、pnpm、Docker Desktop；`docker start pgsql redis rabbitmq`（Redis 密码 `redis`；PG `root/root`，库 `one_platform`/`one_quartz`；RabbitMQ vhost `one`）。
2. 后端：`./gradlew :platform-services:app-server:bootRun`，8181 监听即就绪（约 1–2 分钟）。
3. 前端：`pnpm run webapp:start`、`pnpm run mobile:start`。
4. 验证：`curl -k -X POST https://wdev.cc/api/v1/initialize` 返回 `code:200`；打开 `https://wdev.cc/webapp/` 应跳登录页（`sys_config` 无 `LOGIN_CAPTCHA_ENABLED` 时无需验证码）。
5. 账号：ROOT 租户 `admin`。**口令为 BCrypt、不可逆推，向维护者索取；不要猜、不要写进文档或入库文件。** 登录前先按 6.2 核对客户端密钥，否则 401 与口令无关。
6. 登录验证必须用真实输入事件（合成 `click()`/`requestSubmit()` 不触发 Vue 提交）。成功后网络序列 `POST /oauth/token` 200 → `GET /api/v1/user` 200 → `/webapp/home`，并出现 WebSocket 连接成功日志；未登录访问受保护页跳 `/login?redirect=...`。
7. 后端重启使旧 JWT 失效，验证前重新取 token。无法真实输入时，可在页面上下文 `fetch('/oauth/token')` 换 token，并同步写 `localStorage` 的 `ACCESS_TOKEN`/`REFRESH_TOKEN` 与持久化 user store（**三处独立，都要更新**）。
8. 停止后 Windows 常残留子进程继续监听端口，用 `netstat -ano` 找 PID 后 `taskkill /F /T`。

### 6.4 调试指南

- **远程调试**：`bootRun --debug-jvm`，或 `JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`，IDEA Remote JVM 连 5005。
- **日志**：`bootRun` 输出到控制台；文件在 `logs/`（`logging.logback.path`）；模板在 `commons-core/src/main/resources/cc/wdev/logging/logback/`（本地/开发 CONSOLE，生产 CONSOLE+FILE+LOKI）。
- **排查**：启动失败优先核对 `application-local.yml` 的 datasource、`platform.cache.redis.*`、`platform.message.rabbit.*`；1003002/1003004、401 `invalid_client`、403 权限码分别见 6.2 与契约表；端口占用用 `netstat -ano | findstr 8181` 找 PID 后 `taskkill /F /PID <pid>`。

## 7. 代码约定（必须遵守）

### 7.1 分层与包结构

每个业务域按固定分层组织，**`domain` 下的类型按模块切分**：

- `system-api` 侧：`api`（`XxxApi` 接口）、`domain/dto|form|request|vo|bo`、`enums`、`strategy`
- `system-impl` 侧：`api`（`XxxApiImpl`）、`controller/{system,plt,webapp,exchange}`、`domain/entity`、`domain/converter`、`repository`、`service` + `service/impl`
- 对象转换一律 MapStruct：`XxxConverter.INSTANCE`，禁止手写 getter/setter 拷贝
- 包名一律 `cc.wdev.platform.*`；模块间只能通过 Gradle project 依赖引用，禁止跨模块直接使用未导出类

**API 接口与 HTTP 暴露**（非铁律，新写前先看邻近域）：

- `XxxApi` 用 Spring `@HttpExchange`，前缀 `MappingConstants.EXCHANGE_PREFIX`（`/exchange`）。当前 10 个：`User`、`Client`、`Captcha`、`Social`、`LoginSession`、`Tenant`、`Message`、`Config`、`Authorization`、`AuthorizationConsent`。
- `XxxApiImpl` 一律 `@Service`，被同进程 controller 与安全模块直接注入。多数在 `api` 包下，`security`/`message` 在 `api/impl` 子包。
- `XxxExchangeController` 数量与 `XxxApi` **不对应**：`DictApi` 无 `@HttpExchange`/无 exchange controller；`TenantExchangeController`、`ClientExchangeController`、`AuthorizationExchangeController` 绕过 `XxxApi` 直接注入 `XxxService`；`AuthorizationConsentExchangeController` 是空壳桩；`DataVGeoApi` 是**出站** HTTP 客户端，与内部 exchange 无关。

**控制器基类**：`AbstractController`（`commons-core` 的 `cc.wdev.platform.commons.web.servlet.controller`）提供 `protected GlobalContext context` 与 `redirect()`/`forward()`；`AbstractEntityController` 继承它但**全仓无人使用，是死类**，不要模仿。

**Repository**：写 `@Mapper interface XxxRepository extends BaseEntityRepository<Entity, Long>`。`BaseEntityRepository` **有三个同名类**（mybatis/jpa/elasticsearch），业务域用 mybatis 那个——它只是 `extends BaseMapper<T>` 的**空标记接口**，第二类型参数 `K` 与 `BaseEntityService<T,K,R>` 的 `K` 都是**幽灵参数**，别以为 ID 类型在编译期被约束。JPA 那个才有真方法，别混用。

**XML Mapper** 不与 `repository` 包同目录，统一放 `system-impl/src/main/resources/repository/system/*.xml`。扫描路径由 `MyBatisCustomAutoConfiguration#mybatisPlusPropertiesCustomizer` 设为 `classpath*:/mapper/**/*.xml` + `classpath*:/repository/**/*.xml`。简单 CRUD 一律用泛型 Mapper，不写 XML。

**业务类型注册表（`BizTypeApi`）** 是跨域横切能力：`CoreApiImpl` 启动时调 `BizTypeApiImpl#initialize()`，扫描全仓 `BaseBizTypeEnum` 实现并 upsert 进 `sys_biz_type`（group/code/scope/labelKey + 枚举 `getConfig()` 的 JSON）。两种用法：**类型注册**（`DictServiceImpl`/`TagServiceImpl`/`AttachmentApiImpl` 用它解析「类型码 → 配置 JSON」，多个 SysController 渲染下拉）；**scope 判定**（仅 `ConfigServiceImpl`：`PLATFORM` → 存 `tenantId=0` 且仅根租户可见，`SYSTEM` → 存当前租户；其缓存键用 `SimpleTenantCacheKeyGenerator`，**PLATFORM 分支键也带当前租户**，同份平台配置按租户各缓存一份，是冗余不是串号）。

### 7.2 实体与软删

实体基类在 `commons-core` 的 MyBatis 体系下（`data/mybatis/domain`；JPA 与 ES 各有同名平行基类，ES 的 `BaseEntity` 无 `version` 且审计人是 `String`）：

- `AbstractEntity`（实现 `IdEntity`）：仅 `id`
- `SimpleEntity`：`id` + `version` + `active` + `createdAt/createdBy`——**没有 `updated_*`、没有 `deleted_*`**
- `SimpleTenantEntity extends SimpleEntity`：加 `tenantId`。关联表、操作日志、AI 会话表用这一支
- `BaseEntity`：`SimpleEntity` 之外再加 `updatedAt/updatedBy` + `deletedAt/deletedBy`
- `BaseTenantEntity extends BaseEntity`：加 `tenantId`。**需要租户隔离的主表用这一支**

软删语义：

- 全仓**没有任何 `@TableLogic`**，也没有 `logic-delete-*` 全局配置，查询不会自动过滤已删数据；删除必须显式走服务层 `softDelete*`。
- `BaseEntityService#softDeleteBatch` 随实体类型变化：`BaseEntity` → 置 `active=DISABLED` + `deletedAt/By`；`SimpleEntity`/`SimpleTenantEntity` → **只置 `active`**；裸 `AbstractEntity` → **字段根本不更新却仍执行一次 UPDATE，是静默空操作**。给非 `BaseEntity`/`SimpleEntity` 的实体用 `softDelete*` 等于没删。
- `EntityService` 同时声明硬删（`deleteById`/`deleteBatchById`/`delete`/`deleteBatch`/`deleteAll`）与软删两套入口，`BaseEntityService` 两套都实现。**默认走软删**（例外见 7.5）。

### 7.3 响应与异常

- `/api/v1/**` 接口返回 `R<T>`（`code`/`message`/`data`），成功用 `R.success(data)`；`message` 默认 `"Success"`。
- 业务错误抛 `ServiceException(ResponseCodeEnum.XXX)`，**不要用 `R.error()` 作业务分支返回值**——它出的是 `code=500/系统错误`，客户端无法区分业务错误。
- 参数校验用 `jakarta.validation`（`@Valid` + 注解），不要手写 null 判断链。
- 分页：请求对象携带 `PageRequest`（`getPageable()`），返回 `R<Page<...>>`；用 `MyBatisPlusUtils` 在 `IPage` 与 `Page` 间转换。
- 多语言文案通过 i18n 提供，不要硬编码中文到响应（见 7.8）。
- 业务 DTO 含凭据字段时用 Jackson `@JsonProperty(access = WRITE_ONLY)`（序列化忽略、反序列化保留）。**不要用 MapStruct `@Mapping(ignore = true)`**——`UserLoginDto.password`、`ClientDto.clientSecret` 这类字段在进程内要被 `CustomUserDetailsService`/`CustomOAuth2ClientService` 读取才能完成登录与客户端注册，converter 级 ignore 会打断认证链路。

### 7.4 安全（红线，改动前必读）

`system-security` 的 `WebSecurityConfiguration`（`cc.wdev.platform.security.configuration`）用 `@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)` 开启方法级安全，共四条链：

| 优先级 | 匹配 | 放行规则 |
| --- | --- | --- |
| `HIGHEST_PRECEDENCE` | 授权服务器 endpoints | `anyRequest().authenticated()` |
| 1 | `/api/mcp/**` | `anyRequest().authenticated()` + `mcpServerApiKey()`（`CustomApiKeyRepository`） |
| 2 | `/api/**` | `GLOBAL_API_EXCLUDE_URLS` 放行 + `anyRequest().permitAll()`；配了 JWT 解码与 `CustomJwtGrantedAuthoritiesConverter`，但**解码不改变放行结果** |
| 3 | 其余（无 matcher 兜底） | `GLOBAL_WEB_EXCLUDE_URLS` 放行 + `anyRequest().permitAll()`，会话策略 `IF_REQUIRED` |

关键事实：

- **`GLOBAL_API_EXCLUDE_URLS` 是空数组**，即整条 `/api/**` 链实际全放行，鉴权**完全依赖方法级注解**；`GLOBAL_WEB_EXCLUDE_URLS` 才是非空的。
- **`/exchange/**` 不匹配链 1、链 2**（matcher 是 `/api/mcp/**`、`/api/**`），落到无 matcher 的兜底链 3，而链 3 同样 `anyRequest().permitAll()`。
- 可用注解：`@PreAuthorize("hasAnyAuthority('x:y')")`、`@Authenticated`（`isAuthenticated()`）、`@Anonymous`（`permitAll()`）、`@Super`（要求 `*:*:*`）。**`@PermitAll` 是 JSR-250 的 `jakarta.annotation.security.PermitAll`**（因 `jsr250Enabled=true` 生效），不要与 `@PreAuthorize` 混挂同一方法。
- `@RateLimiter`（`RateLimitAspect`，Redis/Lua，键 `RateLimiter:<requestURI>:<ip|clientId>:<spelKey>`）、`@OperationLog`（`OperationLogAspect`，是 `record`）**都不是元注解驱动**，各自独立切面。
- 权限串示例 `system:user`/`system:config`/`dev:ai:config:model`；角色前缀 `ROLE_`，数据范围前缀 `DATA_SCOPE_`。
- 认证（密码/OTP/社交）不是 Filter，而是三对 `AuthenticationConverter` + `AuthenticationProvider`（`OAuth2Password*/`OAuth2Otp*/`OAuth2SocialAuthentication*`），注册在 `AuthorizationServerConfiguration#getAuthenticationProviders`；`system-security` 里**只有 `CaptchaAuthenticationFilter` 一个 Filter**，且只对 password grant 生效。

由此：

- **新增任何端点必须显式加 `@PreAuthorize` 或 `@Authenticated`**；只有确需匿名才用 `@Anonymous` / `@PermitAll`。
- 10 个 `*ExchangeController` 都无方法级鉴权注解。**禁止向 exchange 接口新增更敏感的数据**；新增内部接口前先与维护者确认鉴权方案。
- 租户上下文 `TenantContext` 基于 MDC：Servlet 侧 `ServletWebFilter`（响应式 `ReactiveWebFilter`）调 `handleServletRequest` 初始化，解析顺序 `x-tenant-id` → `x-tenant-code` → 域名（`DefaultTenantResolver`），用后清理。**请求头未与 JWT `tid` 做一致性校验**（`SecurityConstants.JWT_KEY_TID` 只被写、读，没有一处比对），且条件判定有缺陷（见 10）。服务层不能信任「请求头 = 当前用户所属租户」；`SecurityUtils#getTid` 仅**未认证**时回退 `TenantContext`，已认证但 JWT 无 `tid` claim 时返回 `null`。
- 密码必须经 `SecurityUtils.encode()` 落库，**返回 `{bcrypt}$2a$…`（`DelegatingPasswordEncoder` + BCrypt），不是裸 BCrypt 串**。

### 7.5 数据层

- **默认走软删**（`BaseEntityService`），下列 9 张**关联表允许物理删除**（保存即「先删后插」重写，读取侧不过滤 `active`，表结构无 `deleted` 列）：`sys_attachment_relation`、`sys_dict_relation`、`sys_tag_relation`、`sys_address_relation`、`sys_region_relation`、`sys_ai_relation`、`sys_catalog_relation`、`sys_entity_relation`、`sys_user_biz_relation`。新增关联表若沿用「保存即重写」需加入清单；否则改 `BaseEntity` + 差量更新 + 读取侧补 `active` 过滤，二者不混用。
- 多租户表继承 `BaseTenantEntity`；跳过租户过滤用 `@InterceptorIgnore(tenantLine = "true")`。
- 关联表用 `SimpleTenantEntity`（无 `updated_*/deleted_*`），但它属于上述物理删除例外。
- MyBatis-Plus 条件构造：`.in(condition, col, collection)` 的集合参数会被提前求值——用 `Arrays.asList(...)`，**不要用 `Arrays.stream(arr).toList()`**（`arr` 为 null 必 NPE）。
- SQL 参数化：用 `#{}` 绑定参数，**禁止 `${}` 字符串拼接入 SQL**。

### 7.6 缓存与性能

- `BaseCachingEntityService` 的失效只按 `byId`/`byCode` 注册；**直接调 mapper/wrapper 写库会完全绕过失效**（详见 6.2 的 `sys_client` 案例）。`getCacheKeyGenerator()` 是 default 方法；租户级用 `SimpleTenantCacheKeyGenerator`（键总拼当前租户）。
- `insert` 后 `setCache` 回填；`save`/`updateById` 只 `deleteCache`（下次读回填）。
- JWT 权限转换（`CustomJwtGrantedAuthoritiesConverter`）**无缓存，每次查库**；两个同名 `JwtAuthenticationConverter` Bean（`PlatformSecurityConfiguration`/`CommonSecurityConfiguration`，实现相同），靠 `allow-bean-definition-overriding` 生效。

### 7.7 序列化

- `CoreAutoConfiguration#jsonMapperBuilderCustomizer` 挂 `CommonModule`，全局 NON_NULL + **全局 `INDENT_OUTPUT`（pretty-print 无条件开）**。
- `long`/`Long`/`BigInteger` → `ToStringSerializer`（**只序列化，不影响反序列化**），故分页 `totalElements` 等是字符串；`Date`/`LocalDateTime` 各有自定义 serializer。

### 7.8 i18n

- 资源：`system-impl/src/main/resources/i18n/system/{label,validation}/messages*.properties`（`messages`/`zh_CN`/`zh_TW`/`en_US` 四版）；key 约定 `label__<group>__<value>`。
- `CustomLocaleResolver` **忽略 `Accept-Language`**；MessageSource `useCodeAsDefaultMessage(true)`，缺翻译时**静默返回 key**（不报错）。
- DB 侧 `sys_label`/`sys_entity_label`/`sys_tenant_lang`，**无 `I18nEntity` 类**。

### 7.9 日志

- 模板在 `commons-core/src/main/resources/cc/wdev/logging/logback/`（base/console/file/loki appender，**无分 profile 模板文件**）。
- `@OperationLog` → `OperationLogAspect` → `LogStore` → RabbitMQ → `sys_operation_log`。

### 7.10 测试

- 三处测试根：`commons-webapp/src/test`（主宿主）、`app-server/src/test`（API 级，`BizTypeApiTests`/`AttachmentApiTests`）、`commons-console`；命名 `*Tests`（JUnit5）。
- 构建排除 `integration` 标签，但**没有任何测试打该标签**，故 `./gradlew test` 会实跑依赖中间件的 `SpringBootTest`；基类 `@Transactional + @Rollback(false)` **会污染本地库**。**新测试优先 mock/H2**。
- CI 只 `build`、不 `test`。

## 8. AI 模块架构与响应模式

### 8.1 双轨体系与供应商分离

- **双轨**：`AiManager` + `ai/factory/**ModelFactory`（Spring AI 生态，产出 `ChatModel`/`ChatClient`，支持记忆/RAG/日志/Tool/Advisor）；`AiServiceManager` + `ai/service/**ModelService`（原生 SDK 同步直连，无 ChatClient/Advisor/VectorStore 面，额外支持 rerank）。**模型实例不缓存**，每次新建；只有 factory 自身单例。
- **两类枚举别混**：`AiServiceProvider`（引擎/传输，关注协议与 SSE 解析）、`AiModelProvider`（厂商/商业身份，含模型清单与能力矩阵，按精确名或 `前缀-` 匹配）。`AiServiceProvider.CUSTOM` 无实现、无解析器返回；枚举成员清单以源码为准，不在本文逐一维护。
- **配置**：`platform.ai.factory.*` / `platform.ai.service.*` 成对提供 `chat-service-provider`（引擎）与 `chat-model-provider`（厂商），**两个键都要配，只配引擎拿不到厂商**；`platform.ai.providers.<id>` 为厂商配置，map key 统一小写，**provider id 是自由文本**（走 `BaseEnum.getEnumByValue(..., default)`，不等于枚举常量）。
- **`AiManager` 三级解析**：1）无参按 `factory.chat-service-provider` 找已注册 factory，找不到直接抛；2）按枚举，**只有这一级**读 `platform.ai.fallback-enabled`（默认 false）；3）按 `ModelConfig`，校验非空且 `factory.supports(config)`，否则无条件回落第 1 级。静态 `getXxxServiceProvider(...)` 对未知值有硬编码默认。
- 接入入口：`commons-webapp` 的 `ChatController`（`/chat/text`、`/chat/stream`，`@Anonymous`，仅测试）；正式 `AiChatWebController`（`@Authenticated`，前缀 `API_V1_PREFIX` 即 `/api/v1/ai/chat/*`，**不含 web 段**）经 `AiChatApiImpl` 按 `AiChatType` 取 ChatClient；业务模型实体经 `AiHelper` 转 `SimpleModelConfig` 复用双轨。

### 8.2 AiResponseType 三种响应模式

由 `AiResponseType`（`TEXT`/`JSON`/`STRICT`，默认 `TEXT`）驱动，实现在 `AiUtils`：

- **`TEXT`**：Prompt 只输出 Markdown 正文；流式 `spec.stream().content()` **原样透传**，不过状态机、不加 START/END，首字延迟最低。
- **`JSON`**（围栏交互卡片）：Prompt 要求正文 + 末尾唯一 json-render 围栏；同步与 TEXT 同分支。流式走 `processStream` 滑动窗口——正文以 `type="text"` 实时推送，尾部卡片积攒完整后一次性推入 json-render 块；`Flux.concat(START, flux, END)` 包装，未闭合卡片在流结束时恢复输出，异常降级 ERROR。
- **`STRICT`**：基于 `UiComponentRegistry` 动态合成 JSON Schema，禁止非 blocks 输出；**并非逐 Token 流式**——同步与流式都走阻塞式 `spec.call().entity(converter, validateSchema)`，流式再将结果按 `UiBlock` 切成 `type="block"` 事件（START/END 包装，校验失败只返回 ERROR）。`UiResponseSchema` 是 `UiComponentRegistry` 的嵌套 record，不是独立类。

### 8.3 AI 周边能力与扩展

- **会话记忆**：`AiManagerImpl#applyBaseAdvisors` 挂 4 个 advisor；默认内存 `SessionService`，app 端被 `SystemAiConfiguration` 以 `CustomSessionRepository`（`sys_ai_session` + `sys_ai_session_event`，TTL 60 天）覆盖；续接校验「本人 + 本租户」，压缩参数见 `AiUtils.getSessionMemoryAdvisor`。
- **长期记忆**：`AutoMemoryToolsAdvisor` + `AutoDreamAdvisor`，目录 `platform.ai.memory.path/{tid}/{uid}`，受 `memory.enabled` 与 path 非空双重门控。
- **Agent 工具**：`agent.enabled` 下注册 `SkillsTool`；skills 与 agents 资源都能解析出 `Resource` 时才注册 `TaskTool`；`CommonTools` 走 Bean + AOT，示例工具在 `commons-webapp`。
- **RAG**：全局 `RetrievalAugmentationAdvisor(allowEmptyContext(false))`；KB 级经 `AiHelper.applyRagAdvisors`（租户 `Filter` + 可选重排）。向量化 `AiVectorServiceImpl` 为提交后异步 + 分批；**重试退避在 `AiKbJob`/`AiKbApi`，不在该 Service**。VectorStore 支持 ES/MariaDB/PgVector（默认 ES）。
- **索引命名**：`AiRagUtils` 统一处理 ES `t{tenantId}-{prefix}-{collection}`、SQL `t{tenantId}_{prefix}_{collection}`（截断 63）；生效名以 store 专属 `index-name`/`table-name` 为准，`rag.store.index-prefix` 绑定后从不读取（死配置）。
- **MCP 与密钥**：`AiMcpServerEntity` 登记表（权限 `dev:ai:config:mcp`），Server 端走 `mcpServerApiKey()` + `CustomApiKeyRepository`；模型密钥落库 `enc:` + AES，读取时无前缀按历史明文放行，更新 `****` 掩码不覆盖旧值。
- **扩展入口**：新增厂商 → `AiModelProvider` 加枚举 + `ai_model` 或 `platform.ai.providers.*`；新增引擎 → 实现 `*ModelFactory`/`*ModelService` 并在对应 `Ai*AutoConfiguration` 注册；新增 UI 组件 → 实现 `UiComponentDefinition` Bean（`propsSchema()` 必须内联，禁止 `$ref`），`UiComponentRegistry.register` 为 synchronized + 缓存失效重建。
- **Embabel**：仅依赖与空测试，零 Java 源码引用。


## 9. 构建与依赖

- 新依赖优先在 `gradle/libs.versions.toml` 声明，不要在 `build.gradle.kts` 裸写坐标。
- **版本强制覆盖全部集中在 `buildSrc/src/main/kotlin/java-conventions.gradle.kts` 一个文件**（全仓唯一含 `resolutionStrategy`/`dependencyManagement` 的地方）：多个 `mavenBom` 导入 + `eachDependency`/`useVersion`/`selectArtifact` 规则 + 全局 `exclude`。**不要散落覆盖。**
- **BOM 导入顺序决定版本胜负**：Spring Boot BOM 先导入，后导入的 Jackson 3/embabel 等会被其约束压住；`libs.versions.toml` 的 `jacksonVersion` 实际不生效，以 Boot BOM 为准。**版本对不上先查这里。**
- 约定插件 4 个链式叠加：`java-conventions` → `java-library-conventions` → `spring-boot-conventions` → `spring-boot-native-conventions`；库模块用 java-library，commons-console/commons-webapp/admin-server/app-server 用 spring-boot，commons-native 用 native。
- 产物名固定：app-server `app.jar`、admin-server `admin.jar`、commons-webapp `webapp.jar`、commons-console `console.jar`、commons-native `native.jar`；**webapp/console 是外壳 jar**（`exclude *.jar` + `Class-Path` manifest）。`bootBuildImage` 仅 app-server/admin-server/commons-native；库模块 Jar `exclude application*.yml`，commons-core 对外能力多为 `compileOnly`。

## 10. 已知问题

当前源码事实，非待办清单；动到相关区域前先读，已闭环的直接删除。

- **安全**：`/exchange/**` 无鉴权（10 个 controller 无注解，部分回传密码哈希/密钥，仅靠 `@JsonProperty(WRITE_ONLY)` 挡序列化）；租户上下文信任请求头、未与 JWT `tid` 校验（`handleServletRequest` 条件用「或」，tenantId 被丢弃且不读 `x-tenant-code`）；`SecurityUtils#getTid` 无 `tid` 时返回 null；操作日志把完整 `Authorization` 写进 `sys_operation_log.request_headers`；`GlobalExceptionHandler` 泄漏内部异常消息。
- **数据/配置**：`RoleServiceImpl`/`TenantServiceImpl` 物理删主表；`sys_authority` 无种子数据（权限码全无、`AuthorityTreeEnum` 死代码、`system:client:chcek` 拼写错）；`sys_catalog_relation.idx`/`sys_entity_relation.relation_index` 列型与实体不符（`VARCHAR(2000)` vs `Integer`）；`db_pgsql_schema_core.sql` 含 `DROP TABLE`（仅本地）。
- **死配置/死代码**：jwt authorization-code/device-code TTL、`ai.rag.store.index-prefix`、`ai.agent.prompts`、app-server orphan `messages_*.properties`、`application-native.yml` 的 `platform.ai.vectorstore.*`、local/development 的 anthropic/embabel 配置；`AiServiceProvider.CUSTOM`、`AbstractEntityController`、`TenantContext` 两个 ThreadLocal、`PageRequest#check(List)`、`BaseEntityRepository` 的 `K` 幽灵参数、`MongoCustomProperties`/`platform.rate-limit`、commons-webapp logback-spring 未 include base.xml。
- **行为/运行时**：全局 `INDENT_OUTPUT`（响应膨胀）；登录会话无关闭链路（logout 只打日志）；`NettyWebSocketServer.destroy()` 条件写反；登录主体构造器把账户状态置 true；`allow-circular-references` + `allow-bean-definition-overriding` 同开；`ClientPltController` 用 sys 前缀；`DataSourceCustomAutoConfiguration` 的 `@Primary` 直接返回 master（无读写路由）；LoginSession pie chart 无消费方；`softDeleteBatch` 对裸 `AbstractEntity` 静默空操作（见 7.2）。
- **构建/CI**：`tools/actions/build-native.yml`/`deploy.yml` 引用过期路径 `platform-boot-server:app-server`（实际 `platform-services:app-server`）；`.github/workflows/build.yml` 只 `build` 不 `test`；`libs.versions.toml` 的 `jacksonVersion` 被 Boot BOM 遮蔽（实际 3.1.5）。
- 前端调用但后端不存在的端点（`/sys/role/users`、`/sys/ai/tool/delete`、`/ai/chat/completion`、`/oapis/wecom/signature`）属产品决策，明细见前端 `AGENTS.md` §11。

## 11. 禁止事项

- 不提交真实密钥/口令；配置只允许占位或 `${ENV_VAR}` 引用
- 不修改/不提交 `application-local.yml`、`application-production.yml`（已 gitignore，属本地/生产私有配置）
- 不删除 `tools/deploy/binaries/` 下的本地部署产物
- 不使用 `System.out.println` / `printStackTrace`（测试辅助除外）
- 不在业务代码中写 `Thread.sleep`、空循环等待
- 不通过 shell 重定向等方式改写代码文件（用 apply_patch 等受控方式）
- 不把 `GlobalConstants` 里的密码类配置写进任何入库文件

## 12. 新增业务功能与横切导航

### 12.1 完整样例（推荐先照抄一条真实链路）

`config` 域是最完整参考（`dict` 同构）：`ConfigApi`（`@HttpExchange` + Request/Form/VO/strategy）→ `ConfigApiImpl`（`@Service`）/`ConfigExchangeController` → `ConfigSysController`（`@PreAuthorize` + 写操作 `@OperationLog`）→ `ConfigServiceImpl extends BaseCachingEntityService`（scope 走 `BizTypeApi`）→ `ConfigRepository`（简单 CRUD 无需 XML）。

### 12.2 步骤

1. 在 `system-api` 定义 `XxxApi`（需跨服务暴露时加 `@HttpExchange`）与 DTO/Form/Request/VO
2. 在 `system-impl` 实现 `XxxApiImpl`（`@Service`）与 `XxxService`/`XxxServiceImpl`；主表继承 `BaseTenantEntity`（关联表继承 `SimpleTenantEntity`），转换用 MapStruct
3. 需 HTTP 暴露时新增 `XxxExchangeController`——**先与维护者确认鉴权方案**（见 7.4）
4. 新增 `XxxRepository`（`@Mapper interface ... extends BaseEntityRepository<T, Long>`，**import 的是 mybatis 那个**），确有自定义 SQL 才写 XML 并放 `resources/repository/system/`
5. 新增 `XxxSysController`/`XxxWebController`，**每个端点必须加 `@PreAuthorize` 或 `@Authenticated`**，返回 `R<T>`
6. 用户可见操作加 `@OperationLog`；列表接口分页并返回 `R<Page<...>>`
7. 需 i18n 文案时加到 `i18n/system/label/messages*.properties`（key 用 `label__<group>__<value>`），不要在代码里拼中文
8. 在 `commons-webapp` 或 `app-server` 补测试（优先不依赖外部中间件）
9. 若新增了权限码，**先确认 `sys_authority` 有对应数据**（见 10——目前没有种子数据）
10. 同步更新本文对应章节

### 12.3 新增整个业务域时额外要建的

`system-api` 与 `system-impl` 两侧各建同名包（`cc.wdev.platform.system.<domain>`），`domain/{dto,form,request,vo}` 在 api 侧、`domain/{entity,converter}` 在 impl 侧；若该域需要类型注册表，实现一个 `BaseBizTypeEnum` 即可，启动时 `BizTypeApiImpl#initialize()` 会自动扫到。

### 12.4 横切能力导航（按任务查入口）

| 能力 | 配置树 | 代码入口 |
| --- | --- | --- |
| 对象存储 | `platform.storage` | commons-core `storage` + system `storage` 附件域 |
| 定时任务 | 标准 `spring.quartz.*` + `spring.datasource.job` | system `job` 域 + `QuartzCustomAutoConfiguration`（无 `platform.*` 开关） |
| 消息/WebSocket | `platform.message.rabbit/broadcast`、`platform.websocket` | commons-core `message`（Rabbit/WebSocket/SSE/Netty）+ system `message` 域 |
| 审计日志 | `platform.log` | logback 模板 `commons-core/src/main/resources/cc/wdev/logging/logback/`；切面 `commons.core.log.aspect`；`@OperationLog` → RabbitMQ → `sys_operation_log` |
| 国际化 | `i18n/system/{label,validation}/messages*.properties` | `MessageSourceUtils`、`sys_label`/`sys_entity_label`、`CustomLocaleResolver`（当前忽略请求语言） |
| 序列化 | — | `commons-core/utils/jackson/CommonModule`（Long→String、日期）+ `CoreAutoConfiguration`（`NON_NULL` + 全局 `INDENT_OUTPUT`） |
| 开放平台 | `platform.oapis.*`、`platform.sms`、`platform.translator`、`platform.face-body` | commons-core `oapis`（微信/钉钉/飞书/Telegram/短信/翻译/人脸），默认全关 |
| 数据源/分库 | `platform.data.datasource.*` | `DataSourceCustomAutoConfiguration`（master/slave/job；`@Primary` 直接返回 master，无自动读写路由） |
| 验证码/IP/敏感词 | `platform.captcha/http/keyword/sensitive` | commons-core `extensions`（注意 `platform.parser` 默认开） |
| 业务类型注册 | — | `BizTypeApi` / `sys_biz_type`，启动时由 `CoreApiImpl` 触发扫描入库 |
| 前端资源 | — | 无独立 Node 工程；静态资源在 `commons-webapp` 与 `app-server` 的 `resources/public` + Thymeleaf `templates/` |

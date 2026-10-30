# 身份切换（Role Type Switching）方案

> 目标：桌面端与移动端都提供「切换身份」，让用户在自己拥有的角色类型之间切换。
> 普通会员（`MEMBER`）不显示该功能。
> 状态：待确认关键决策后实施。

## 1. 现状核实（源码与数据事实）

### 1.1 `RoleTypeEnum` 实际是 4 个值

`platform-modules/system/system-api/.../commons/enums/RoleTypeEnum.java`：

| 枚举 | bizType/code | groupType | dataScope | 说明 |
| --- | --- | --- | --- | --- |
| `PLATFORM_ADMINISTRATOR` | 同值 | `PLATFORM` | `ALL` | 平台管理员 |
| `SYSTEM_ADMINISTRATOR` | 同值 | `SYSTEM` | `ALL` | 系统管理员 |
| `USER` | 同值 | `SYSTEM` | `MY` | 普通用户 |
| `MEMBER` | 同值 | `MEMBER` | `MY` | 会员 |

> 你提到的是三个（平台管理员/系统管理员/普通用户），源码里还有 `MEMBER`。本方案按「`MEMBER` 不提供切换」处理，与你的要求一致。

### 1.2 权限是「实时查库」的，不是写在 JWT 里

- `CustomTokenCustomizer` 只往 access token 写 `uid`/`tid`/`username` 三个 claim，**不含权限**。
- 每次请求由 `CustomJwtGrantedAuthoritiesConverter#convert` 调 `UserApi#getUserAuthority(uid)` 实时取权限。
- `AuthorityServiceImpl#findByUserId` 的口径：**角色权限 ∩ 租户权限**，结果按 `UserAuthorityCacheKeyGenerator.keyByUserId(userId)` 缓存（Redis db 11）。
- `UserApiImpl#getUserAuthority` 把用户**全部角色**合并成一个扁平集合，再叠加 `ROLE_<bizType>`、`DATA_SCOPE_<scope>`。

结论：**切换身份不需要重新登录、不需要重发 token**，只要改变「参与合并的角色集合」即可立即生效。

### 1.3 数据现状（本地库）

`sys_role.biz_type` 存的就是角色类型码；`admin` 同时绑定了 4 种类型：

```
1002001 admin  PLATFORM_ADMINISTRATOR
1002001 admin  SYSTEM_ADMINISTRATOR
1002001 admin  USER
1002001 admin  MEMBER
```

`UserInfoDto` 已返回 `roles`（角色 code 列表）与 `authorities`，前端 `User` 类型也有对应字段——**前端已具备渲染「我拥有哪些身份」所需的数据**。

## 2. 核心设计

### 2.1 切换的本质

「当前身份」= 一个持久化在**服务端会话态**上的角色类型码，参与权限计算时，只取**属于该类型**的角色。

三级优先级（从高到低）：
1. 服务端记录的 `currentRoleType`（用户切换后写入）
2. 请求头 `x-role-type`（移动端/多标签页隔离用，可选）
3. 未设置 → 全部角色合并（保持现状，向后兼容）

### 2.2 安全红线（必须满足）

- **只能切换到本人在该租户下真实拥有的类型**，否则 403。禁止客户端指定任意类型就生效。
- **切换后权限必须真实收窄**：切到 `USER` 后，原来能访问的管理端接口必须 403。这是区分「真功能」和「UI 装饰」的唯一标准。
- 切换影响缓存口径，必须处理缓存键或失效，否则切换后仍命中旧权限。

### 2.3 「会员不显示切换」的实现口径

判定条件：**用户拥有的角色类型集合中，除 `MEMBER` 外至少还有 2 个**，才显示切换入口。
- 只有 `MEMBER` → 不显示（纯会员）
- `MEMBER` + 1 个其他 → 只有一个可切换目标，也不显示（切了没有意义）
- 3 个管理员类型都有 → 显示

## 3. 关键决策点（需要你拍板）

### 决策 A：身份作用域

| 方案 | 含义 | 代价 |
| --- | --- | --- |
| **A1 服务端全局生效**（推荐） | 切换写入 Redis（按 userId 或 sessionId），后续所有请求按新身份算权限 | 需改动权限计算链路与缓存口径；同账号多设备会互相影响 |
| A2 仅请求头生效 | 前端每个请求带 `x-role-type`，后端按该头计算 | 不改会话；但**可被客户端伪造**，必须校验归属，且所有前端请求都要改造 |
| A3 仅前端展示层 | 只切换菜单可见性，后端权限不变 | 最省事，但等于没做——切换后照样能调管理接口，不满足「功能完整可用」 |

> 我推荐 **A1**。理由：后端已有 `TenantContext` 这类基于 MDC 的上下文机制，加一个 `RoleTypeContext` 成本可控；且能真正做到权限收窄。A3 不可接受。

### 决策 B：切换后是否需要重新登录

由于权限实时查库、token 里没有权限，**技术上不需要重新登录**。
但需注意：前端 `UserStore` 里缓存的 `authorities` 是登录时拉的，切换后必须重新拉 `/api/v1/user` 覆盖，否则路由守卫仍按旧权限放行（虽然接口会 403，但体验割裂）。

### 决策 C：多身份并存时的默认身份

用户登录后若拥有多个类型，默认用哪个？
- **C1** 用「最高权限」（PLATFORM > SYSTEM > USER > MEMBER）——推荐，符合直觉
- C2 用上次切换的选择（持久化到用户配置）
- C3 用第一个角色

> 推荐 C1 作为默认，C2 作为「记住上次选择」的增强。两者可以并存：没选过就按 C1，选过就用 C2 的记录。

## 4. 实施清单（按决策 A1 + C1 设计）

### 4.1 后端

1. `system-api`
   - 新增 `RoleTypeSwitchRequest`（`roleType`，带白名单校验）
   - 新增 `RoleTypeVo`（`current`、`available[]`，用于前端渲染）
   - `UserApi` 增加 `switchRoleType` / `getCurrentRoleType` / `getAvailableRoleTypes`
2. `system-impl`
   - `RoleTypeContext`（MDC，仿 `TenantContext`）持有当前类型
   - `UserRoleService` 增加「按类型筛角色 ID」方法
   - `AuthorityServiceImpl#findByUserId` 按当前类型过滤角色；**缓存键加入 roleType**，或切换时主动失效该用户缓存
   - `UserApiImpl#getUserAuthority` 按当前类型合并角色
   - `UserWebController` 新增 `POST /api/v1/user/role-type` 与 `GET /api/v1/user/role-type`
   - 切换时校验归属：类型不在本人角色集合内 → `ServiceException`（403 语义）
3. 鉴权
   - 切换端点本身要求 `@Authenticated`
   - 在 `WebSecurityConfiguration` 或过滤器里把请求头/MDC 的 roleType 落到 `RoleTypeContext`

> 注意：`GlobalExceptionHandler` 目前把 `AuthorizationDeniedException` 兜底成 `500 系统错误`（上一轮测试发现）。切换后越权会更常见，建议顺带补一个 403 分支，否则用户切到 `USER` 后满屏「系统错误」。

### 4.2 前端（commons + webapp + mobile）

1. `commons`
   - `User` 增加 `currentRoleType` / `roleTypes`
   - API：`switchRoleTypeApi` / `getRoleTypeApi`
   - `computed canSwitchRoleType`：可用类型（排除 MEMBER）≥ 2
   - 切换成功后：调 `getUserInfo()` 覆盖 `user`，并按新权限重算菜单
2. `webapp`：`AvatarDropdown` 增加「切换身份」子菜单（`el-dropdown-item` + 当前项标记），切换后 `router.replace` 到当前身份可访问的着陆页（复用 `getAccessibleMenuPath`）
3. `mobile`：`me.vue` 增加身份单元格（`van-cell` + `van-action-sheet` 选择）
4. i18n 三语

### 4.3 测试

- 接口：切换前后同一接口的权限差异（切到 USER 访问 `sys/user/list` 应 403）
- 越权：切换到未拥有的类型应被拒
- 缓存：切换后立刻请求，确认不命中旧权限
- 页面：桌面 + 移动各验证「有切换 / 无切换」两种账号
- 回归：lint + 构建 + 已提交功能的冒烟

## 5. 风险与边界

| 风险 | 说明 | 应对 |
| --- | --- | --- |
| 缓存口径 | 权限缓存按 userId，切换后若不失效会命中旧值 | 缓存键加 roleType，或切换时 `deleteCacheByUserIds` |
| 同账号多端互相影响 | A1 是服务端全局态 | 若需隔离，用 sessionId 维度或退回 A2 |
| 根账号 `*:*:*` | 顶层租户 admin 有 `*:*:*` 短路 | 切换后**不应**再短路，否则切到 USER 仍全放行；需明确 `*:*:*` 是否受身份约束 |
| 前端路由守卫 | 只按 `user.authorities` 判断 | 切换后必须重拉 user，否则守卫放行但接口 403 |

> 其中「根账号 `*:*:*`」是必须先定的问题：当前 `getUserAuthority` 对顶层租户名为 `admin` 的用户无条件加 `*:*:*`。若不处理，admin 切到普通用户后依然畅通无阻，切换形同虚设。

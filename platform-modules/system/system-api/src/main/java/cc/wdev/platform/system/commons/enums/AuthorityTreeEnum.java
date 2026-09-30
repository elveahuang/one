package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.system.commons.domain.AuthorityNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static cc.wdev.platform.system.commons.constants.SystemSecurityConstants.*;
import static cc.wdev.platform.system.commons.domain.AuthorityNode.*;

/**
 * 权限类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AuthorityTreeEnum implements BaseAuthorityNodeEnum {

    SYSTEM("SYSTEM", createNodes(
        // 主页
        createSystemModule("module:home", "首页", createNodes(
            createSystemResource("system:workbench", "工作台", ALL_ROLE_TYPE, 111, 0)
        ), ALL_ROLE_TYPE, 111, 0),

        // 系统管理
        createSystemModule("module:system", "系统管理", createNodes(
            // 系统管理 -> 工作台
            createSystemResource("system:workbench", "工作台", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 111, 1),

            // 系统管理 -> 站点管理
            createSystemGroup("group:system:site", "站点管理", createNodes(
                createSystemResource("system:announcement", "公告管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 111, 1),
                createSystemResource("system:banner", "公告管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),
                createSystemResource("system:link", "友情链接", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 333, 1),
                createSystemResource("system:keyword", "关键字管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 444, 1)
            ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),

            // 系统管理 -> 组织架构
            createSystemGroup("group:system:organization", "组织架构", createNodes(
                createSystemResource("system:organization", "组织架构", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 11, 1),
                createSystemResource("system:user", "用户管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),
                createSystemResource("system:group", "群组管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 333, 1),
                createSystemResource("system:position", "岗位管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 444, 1),
                createSystemResource("system:level", "职级管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 555, 1)
            ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 333, 1),

            // 系统管理 -> 系统设置
            createSystemGroup("group:system:setting", "系统设置", createNodes(
                createSystemResource("system:base", "基础信息", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 111, 1),
                createSystemResource("system:config", "参数管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),
                createSystemResource("system:role", "角色管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 333, 1),
                createSystemResource("system:authority", "权限管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 444, 1),
                createSystemResource("system:dict", "字典管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 555, 1),
                createSystemResource("system:tag", "标签管理", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 666, 1),
                createSystemResource("system:message", "消息管理", createNodes(
                    createSystemPermission("system:message:template", "消息模版", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 111, 1),
                    createSystemPermission("system:message:list", "消息列表", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),
                    createSystemPermission("system:message:log", "消息日志", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 333, 1)
                ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 777, 1)
            ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 444, 1),

            // 系统管理 -> 系统监控
            createSystemGroup("group:system:monitor", "系统监控", createNodes(
                createSystemResource("system:session", "用户会话", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 111, 1),
                createSystemResource("system:log:operation", "操作日志", ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1)
            ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 555, 1)
        ), ONLY_SYSTEM_ADMIN_ROLE_TYPE, 222, 1),

        // 平台管理
        createPlatformModule("module:platform", "平台管理", createNodes(
            // 平台管理 -> 工作台
            createPlatformResource("platform:workbench", "工作台", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 平台管理 -> 数据
            createSystemGroup("group:platform:data", "数据", createNodes(
                createSystemResource("platform:data:dashboard", "仪表盘", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createSystemResource("platform:data:report", "报表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 平台管理 -> 租户管理
            createPlatformGroup("group:platform:tenant", "租户管理", createNodes(
                createPlatformResource("platform:tenant", "租户管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("platform:tenant:package", "租户套餐", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("platform:tenant:order", "租户订单", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 0),
                createPlatformResource("platform:tenant:log", "租户日志", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),

            // 平台管理 -> 会员管理
            createPlatformGroup("group:platform:member", "会员管理", createNodes(
                createPlatformResource("platform:member", "会员管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 0),
                createPlatformResource("platform:member:package", "会员套餐", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 0),
                createPlatformResource("platform:member:order", "会员订单", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 0),
                createPlatformResource("platform:member:log", "会员日志", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 0),

            // 平台管理 -> 平台设置
            createPlatformGroup("group:platform:setting", "平台设置", createNodes(
                createPlatformResource("platform:base", "基础信息", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("platform:config", "参数管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("platform:authority", "权限管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1),
                createPlatformResource("platform:catalog", "分类管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),
                createPlatformResource("platform:region", "地区管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 1),
                createPlatformResource("platform:application", "应用管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 666, 1),
                createPlatformResource("platform:message", "消息管理", createNodes(
                    createPlatformResource("platform:message:template", "消息模版", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                    createPlatformResource("platform:message:list", "消息列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                    createPlatformResource("platform:message:log", "消息日志", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1)
                ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 777, 1),
                createPlatformResource("platform:appkey", "AppKey管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 888, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),

            // 平台管理 -> 平台监控
            createPlatformGroup("group:platform:monitor", "平台监控", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1)
        ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 1),

        // 开放平台
        createPlatformModule("module:open", "开放平台", createNodes(
            // 开放平台 -> 工作台
            createPlatformResource("open:workbench", "工作台", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 开放平台 -> 微信公众号
            createPlatformGroup("group:open:wechat:mp", "微信公众号", createNodes(
                createPlatformResource("open:wechat:mp:list", "微信公众号列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 开放平台 -> 微信小程序
            createPlatformGroup("group:open:wechat:ma", "微信小程序", createNodes(
                createPlatformResource("open:wechat:ma:list", "微信小程序列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),

            // 开放平台 -> 企业微信
            createPlatformGroup("group:open:wework:cp", "企业微信", createNodes(
                createPlatformResource("open:wework:cp", "企业微信网页应用", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 0),

            // 开放平台 -> 支付宝小程序
            createPlatformGroup("group:open:alipay:ma", "支付宝小程序", createNodes(
                createPlatformResource("open:alipay:ma:list", "支付宝小程序列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 0),

            // 开放平台 -> 抖音小程序
            createPlatformGroup("group:open:douyin:ma", "抖音小程序", createNodes(
                createPlatformResource("open:douyin:ma:list", "抖音小程序列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 0),

            // 开放平台 -> 快手小程序
            createPlatformGroup("group:open:ks:ma", "快手小程序", createNodes(
                createPlatformResource("open:ks:ma:list", "快手小程序列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 666, 0),

            // 开放平台 -> 小红书小程序
            createPlatformGroup("group:open:xhs:ma", "小红书小程序", createNodes(
                createPlatformResource("open:xhs:ma:list", "小红书小程序列表", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 0)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 777, 0)
        ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),

        // 开发管理
        createPlatformModule("module:dev", "开发管理", createNodes(
            // 开放平台 -> 工作台
            createPlatformResource("dev:workbench", "工作台", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 开发管理 -> 开发工具
            createPlatformGroup("group:dev:tools", "开发工具", createNodes(
                createPlatformResource("dev:tools:i18n", "多语言文本", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("dev:tools:generator", "代码生成器", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("dev:tools:docs", "接口文档", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1),
                createPlatformResource("dev:tools:test", "测试工具", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),
                createPlatformResource("dev:tools:showcase", "技术演示", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),

            // 开发管理 -> 网关管理
            createPlatformGroup("group:dev:gateway", "网关管理", createNodes(
                createPlatformResource("dev:gateway:rule", "限流规则", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("dev:gateway:limit", "限流名单", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("dev:gateway:router", "网关路由", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),

            // 开发管理 -> 平台监控
            createPlatformGroup("group:dev:monitor", "平台监控", createNodes(
                createPlatformResource("dev:monitor:system", "系统监控", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("dev:monitor:job", "定时任务", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("dev:monitor:cache", "缓存监控", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1),
                createPlatformResource("dev:monitor:application", "应用监控", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),
                createPlatformResource("dev:monitor:log", "日志中心", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 1),
                createPlatformResource("dev:monitor:config", "配置中心", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 666, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1),

            // 开发管理 -> AI
            createPlatformGroup("group:dev:ai", "AI", createNodes(
                createPlatformResource("dev:ai:config:agent", "智能体管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 111, 1),
                createPlatformResource("dev:ai:config:model", "模型管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 222, 1),
                createPlatformResource("dev:ai:config:mcp", "MCP服务管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 333, 1),
                createPlatformResource("dev:ai:config:tool", "工具管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1),
                createPlatformResource("dev:ai:config:kb", "知识库管理", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 555, 1),
                createPlatformResource("dev:ai:chat:agent", "普通对话", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 666, 1),
                createPlatformResource("dev:ai:chat:model", "智能体对话", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 777, 1),
                createPlatformResource("dev:ai:chat:kb", "知识库对话", ONLY_PLATFORM_ADMIN_ROLE_TYPE, 888, 1)
            ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 444, 1)
        ), ONLY_PLATFORM_ADMIN_ROLE_TYPE, 999, 1)
    ));

    private final String value;
    private final AuthorityNode[] nodes;
}

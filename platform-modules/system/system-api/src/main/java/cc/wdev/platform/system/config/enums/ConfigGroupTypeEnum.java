package cc.wdev.platform.system.config.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统配置分组类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ConfigGroupTypeEnum implements BaseEnum<String> {
    DEV("DEV", "开发专用"),
    BASE("BASE", "基本信息"),
    CONFIG("CONFIG", "基本配置"),
    ACCESS("ACCESS", "访问限制"),
    AGREEMENT("AGREEMENT", "协议管理"),
    PAGE("PAGE", "页面管理"),
    MAIL("MAIL", "邮件配置"),
    SMS("SMS", "手机短信"),
    WEIXIN_CP("WEIXIN_CP", "微信-公众号配置"),
    WEIXIN_MA("WEIXIN_MA", "微信-小程序配置"),
    WEIXIN_MP("WEIXIN_MP", "微信-企业微信配置"),
    LOCATION("LOCATION", "地图服务");

    private final String value;
    private final String description;
}

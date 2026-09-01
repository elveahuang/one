package cc.wdev.platform.system.config.enums;

import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统配置类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum ConfigBizTypeEnum implements BaseConfigBizTypeEnum {
    LOGIN_CAPTCHA_ENABLED("LOGIN_CAPTCHA_ENABLED", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.CONFIG.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "是否启用登录验证码"),
    // 访问限制
    ACCESS_LIMIT_ENABLED("ACCESS_LIMIT_ENABLED", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.ACCESS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "是否开启访问限制"),
    ACCESS_LIMIT_COUNTRY("ACCESS_LIMIT_COUNTRY", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.ACCESS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "访问限制国家"),
    ACCESS_LIMIT_TYPE("ACCESS_LIMIT_TYPE", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.ACCESS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "访问限制类型"),
    ACCESS_LIMIT_MESSAGE("ACCESS_LIMIT_MESSAGE", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.ACCESS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "访问限制提示消息"),
    ACCOUNT_INVITE_COUNT_MAX("ACCOUNT_INVITE_COUNT_MAX", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.ACCESS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "账号邀请上限"),
    // 应用基本信息
    APP_TITLE("APP_TITLE", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.BASE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "站点标题"),
    APP_COPYRIGHT("APP_COPYRIGHT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.BASE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "站点版权"),
    APP_LOGO("APP_LOGO", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.BASE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "站点logo"),
    APP_MOBILE_DOMAIN("APP_MOBILE_DOMAIN", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.BASE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "移动端域名"),
    APP_WEB_SOCKET_SERVER("APP_WEB_SOCKET_SERVER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.BASE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "WebSocket"),
    // 应用页面
    APP_ABOUT("APP_ABOUT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.PAGE.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "站点关于"),
    APP_CONTACT("APP_CONTACT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.PAGE.getValue(), ConfigContentTypeEnum.PAGE.getCode(), "联系我们"),
    // 应用协议
    APP_AGREEMENT_MEMBER("APP_AGREEMENT_MEMBER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.AGREEMENT.getValue(), ConfigContentTypeEnum.PAGE.getCode(), "会员协议（针对付费用户）"),
    APP_AGREEMENT_USER("APP_AGREEMENT_USER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.AGREEMENT.getValue(), ConfigContentTypeEnum.PAGE.getCode(), "用户协议（针对注册用户）"),
    APP_AGREEMENT_PRIVACY_POLICY("APP_AGREEMENT_PRIVACY_POLICY", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.AGREEMENT.getValue(), ConfigContentTypeEnum.PAGE.getCode(), "隐私政策"),
    // 邮件服务器
    MAIL_SERVER_ENABLED("MAIL_SERVER_ENABLED", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.BOOL.getCode(), "邮件服务器是否开启安全协议"),
    MAIL_SERVER_AUTH("MAIL_SERVER_AUTH", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.BOOL.getCode(), "邮件服务器是否开启安全协议"),
    MAIL_SERVER_SSL("MAIL_SERVER_SSL", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.BOOL.getCode(), "邮件服务器是否开启安全协议"),
    MAIL_SERVER_PROTOCOL("MAIL_SERVER_PROTOCOL", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器协议类型"),
    MAIL_SERVER_HOST("MAIL_SERVER_HOST", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器主机"),
    MAIL_SERVER_PORT("MAIL_SERVER_PORT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器端口"),
    MAIL_SERVER_USER("MAIL_SERVER_USER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器用户"),
    MAIL_SERVER_PASS("MAIL_SERVER_PASS", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.PASSWORD.getCode(), "邮件服务器密码"),
    MAIL_SERVER_FROM("MAIL_SERVER_FROM", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器发件人"),
    MAIL_SERVER_NAME("MAIL_SERVER_NAME", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.MAIL.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "邮件服务器名称"),
    // 短信服务供应商
    SMS_PROVIDER("SMS_PROVIDER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "短信服务供应商"),
    // 阿里云短信服务器
    SMS_ALIYUN_ENDPOINT("SMS_ALIYUN_ENDPOINT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "阿里云短信服务Endpoint"),
    SMS_ALIYUN_ACCESS_KEY_ID("SMS_ALIYUN_ACCESS_KEY_ID", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "阿里云短信服务AccessKeyId"),
    SMS_ALIYUN_ACCESS_KEY_SECRET("SMS_ALIYUN_ACCESS_KEY_SECRET", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "阿里云短信服务AccessKeySecret"),
    SMS_ALIYUN_SIGN_NAME("SMS_ALIYUN_SIGN_NAME", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "阿里云短信服务签名名称"),
    // 腾讯云短信服务器
    SMS_TENCENT_ENDPOINT("SMS_TENCENT_ENDPOINT", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务Endpoint"),
    SMS_TENCENT_REGION("SMS_TENCENT_REGION", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务地域"),
    SMS_TENCENT_APP_ID("SMS_TENCENT_APP_ID", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务AppId"),
    SMS_TENCENT_SECRET_ID("SMS_TENCENT_SECRET_ID", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务SecretId"),
    SMS_TENCENT_SECRET_KEY("SMS_TENCENT_SECRET_KEY", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务SecretKey"),
    SMS_TENCENT_SIGN_NAME("SMS_TENCENT_SIGN_NAME", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.SMS.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "腾讯云短信服务签名名称"),
    // 开发测试专用
    DEV_PASS_CAPTCHA("DEV_PASS_CAPTCHA", BizScopeTypeEnum.PLATFORM.getCode(), ConfigGroupTypeEnum.DEV.getValue(), ConfigContentTypeEnum.BOOL.getCode(), "是否跳过验证码"),
    // 地图服务
    LOCATION_PROVIDER("LOCATION_PROVIDER", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.LOCATION.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "地图服务供应商"),
    LOCATION_TIANDITU_TOKEN("LOCATION_TIANDITU_TOKEN", BizScopeTypeEnum.SYSTEM.getCode(), ConfigGroupTypeEnum.LOCATION.getValue(), ConfigContentTypeEnum.TEXT.getCode(), "天地图Token");

    private final String value;
    private final String scope;
    private final String groupType;
    private final String contentType;
    private final String description;

}

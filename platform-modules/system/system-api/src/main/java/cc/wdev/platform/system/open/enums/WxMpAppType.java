package cc.wdev.platform.system.open.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信公众号类型
 * <p>
 * WeChat Official Account 微信公众号
 * Subscription Account 订阅号
 * Service Account 服务号
 * Enterprise Account 企业号
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum WxMpAppType implements BaseEnum<Integer> {
    SUBSCRIPTION_ACCOUNT(1, "SUBSCRIPTION_ACCOUNT", "公众号"),
    SERVICE_ACCOUNT(2, "SERVICE_ACCOUNT", "服务号");

    private final Integer value;
    private final String code;
    private final String description;
}

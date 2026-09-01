package cc.wdev.platform.commons.utils.platform;

import cc.wdev.platform.commons.utils.StringUtils;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.Builder;

/**
 * @author elvea
 */
@Builder
public class Platform {

    private String ua;

    private UserAgent uaObject;

    public Platform parse() {
        this.uaObject = UserAgentUtil.parse(this.ua);
        return this;
    }

    public boolean isMobile() {
        return uaObject != null && uaObject.isMobile();
    }

    public boolean isWeChat() {
        return StringUtils.isNotEmpty(ua) && ua.contains("WeChat");
    }

    public boolean isDingTalk() {
        return StringUtils.isNotEmpty(ua) && ua.contains("DingTalk");
    }

    public boolean isLark() {
        return StringUtils.isNotEmpty(ua) && ua.contains("Lark");
    }

}

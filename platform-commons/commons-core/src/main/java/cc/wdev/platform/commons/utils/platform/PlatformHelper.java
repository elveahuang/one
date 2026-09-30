package cc.wdev.platform.commons.utils.platform;

import cc.wdev.platform.commons.utils.WebServletUtils;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author elvea
 */
public abstract class PlatformHelper {

    public static Platform fromServletRequest() {
        return fromServletRequest(WebServletUtils.getRequest());
    }

    public static Platform fromServletRequest(HttpServletRequest request) {
        return fromUserAgent(WebServletUtils.getUserAgent(request));
    }

    public static Platform fromUserAgent(String ua) {
        return Platform.builder().ua(ua).uaObject(UserAgentUtil.parse(ua)).build();
    }

}

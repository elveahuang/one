package cc.wdev.platform.commons.utils.platform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
public class PlatformTests {

    /**
     * 飞书
     */
    @Test
    public void larkTest() {
        // 苹果桌面飞书客户端
        String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_5_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.5414.128 Safari/537.36 Lark/6.9.4 LarkLocale/zh_CN Electron/Native WebApp/workplace";
        Assertions.assertTrue(PlatformHelper.fromUserAgent(ua).isLark());
        Assertions.assertFalse(PlatformHelper.fromUserAgent(ua).isWeChat());

        // 安卓手机飞书客户端
        ua = "Mozilla/5.0 (Linux; Android 13; 22081212C Build/TKQ1.220829.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/88.0.4324.181 Mobile Safari/537.36 Lark/6.9.9 LarkLocale/zh_CN ChannelName/Feishu TTWebView/0881130046427";
        Assertions.assertTrue(PlatformHelper.fromUserAgent(ua).isLark());
        Assertions.assertFalse(PlatformHelper.fromUserAgent(ua).isWeChat());
    }

    /**
     * 企业微信
     */
    @Test
    public void weWorkTest() {
        String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.82";
        Assertions.assertTrue(PlatformHelper.fromUserAgent(ua).isLark());
        Assertions.assertFalse(PlatformHelper.fromUserAgent(ua).isWeChat());
    }

}

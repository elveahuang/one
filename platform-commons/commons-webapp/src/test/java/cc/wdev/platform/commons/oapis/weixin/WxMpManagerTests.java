package cc.wdev.platform.commons.oapis.weixin;

import cc.wdev.platform.commons.oapis.weixin.service.WxMpManager;
import cc.wdev.webapp.BaseTests;
import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class WxMpManagerTests extends BaseTests {

    @Autowired(required = false)
    WxMpManager wxMpManager;

    @Test
    public void baseTest() throws WxErrorException {
        Assertions.assertNotNull(wxMpManager);
        String accessToken = wxMpManager.getService().getAccessToken();
        Assertions.assertNotNull(accessToken);
        String jsapiTicket = wxMpManager.getService().getJsapiTicket();
        Assertions.assertNotNull(jsapiTicket);
    }

}

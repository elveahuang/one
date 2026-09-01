package cc.wdev.platform.commons.oapis.weixin;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class WxCpManagerTests extends BaseTests {

    @Autowired(required = false)
    WxCpManager wxCpManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(wxCpManager);
        String accessToken = wxCpManager.getService().getAccessToken();
        Assertions.assertNotNull(accessToken);
        String jsapiTicket = wxCpManager.getService().getJsapiTicket();
        Assertions.assertNotNull(jsapiTicket);
    }

}

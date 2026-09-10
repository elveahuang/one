package cc.wdev.platform.commons.oapis.weixin;

import cc.wdev.platform.commons.oapis.weixin.service.WxMaManager;
import cc.wdev.webapp.BaseTests;
import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class WxMaManagerTests extends BaseTests {

    @Autowired(required = false)
    WxMaManager wxMaManager;

    @Test
    public void baseTest() throws WxErrorException {
        Assertions.assertNotNull(wxMaManager);
        String accessToken = wxMaManager.getService().getAccessToken();
        Assertions.assertNotNull(accessToken);
    }

}

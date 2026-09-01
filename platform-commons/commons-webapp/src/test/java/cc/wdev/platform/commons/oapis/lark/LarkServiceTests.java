package cc.wdev.platform.commons.oapis.lark;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.oapis.lark.service.LarkService;
import com.lark.oapi.service.contact.v3.model.ListDepartmentReq;
import com.lark.oapi.service.contact.v3.model.ListDepartmentResp;
import com.lark.oapi.service.contact.v3.model.ListUserReq;
import com.lark.oapi.service.contact.v3.model.ListUserResp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class LarkServiceTests extends BaseTests {

    @Autowired(required = false)
    LarkService larkService;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(larkService);
        String tenantAccessToken = larkService.getTenantAccessToken();
        Assertions.assertNotNull(tenantAccessToken);
        String appAccessToken = larkService.getAppAccessToken();
        Assertions.assertNotNull(appAccessToken);
        String jsapiTicket = larkService.getJsapiTicket();
        Assertions.assertNotNull(jsapiTicket);
    }

    @Test
    public void contactBaseTest() throws Exception {
        ListDepartmentReq dptReq = ListDepartmentReq.newBuilder().parentDepartmentId("0").fetchChild(true).build();
        ListDepartmentResp dptResp = larkService.getClient().contact().department().list(dptReq);
        Assertions.assertTrue(dptResp.success());

        ListUserReq usrReq = ListUserReq.newBuilder().departmentId("0").build();
        ListUserResp usrResp = larkService.getClient().contact().user().list(usrReq);
        Assertions.assertTrue(usrResp.success());
    }

}

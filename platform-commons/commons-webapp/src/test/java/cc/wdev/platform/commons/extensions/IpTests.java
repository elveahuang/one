package cc.wdev.platform.commons.extensions;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.extensions.ip.GlobalIpManager;
import cc.wdev.platform.commons.extensions.ip.Ip;
import cc.wdev.platform.commons.extensions.ip.ip2region.Ip2Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
public class IpTests extends BaseTests {

    @Test
    public void testIp2Region() throws Exception {
        Ip2Region ip2Region = GlobalIpManager.getIp2Region();
        Ip ip = ip2Region.search("61.144.56.100");
        Assertions.assertNotNull(ip);
        Assertions.assertNotNull(ip2Region);
    }

}

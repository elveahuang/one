package cc.wdev.platform.commons.oapis.sms;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.autoconfigure.core.properties.TestProperties;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class SmsSenderTests extends BaseTests {

    @Autowired
    private TestProperties properties;

    @Autowired
    private SmsFactory smsFactory;

    @Test
    public void base() throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", "123456");
        SmsBody body = SmsBody.builder()
            .mobileNumber(this.properties.getMobileNumber())
            .params(params).build();
        this.smsFactory.getAliyunSmsSender().send(body);
    }

}

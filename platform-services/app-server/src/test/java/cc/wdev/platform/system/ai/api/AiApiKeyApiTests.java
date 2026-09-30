package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.BaseTests;
import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.enums.AiApiKeyBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class AiApiKeyApiTests extends BaseTests {

    @Autowired
    private AiApiKeyApi aiApiKeyApi;

    @Test
    public void baseTest() {
        AiApiKeyRequest request = AiApiKeyRequest.builder()
            .bizType(AiApiKeyBizTypeEnum.MCP_API_KEY.getValue())
            .appName("test")
            .build();
        AiApiKeySimpleVo vo = this.aiApiKeyApi.generate(request);
        Assertions.assertNotNull(vo);

        log.info("API Key ID - {}", vo.getAppId());
        log.info("API Key Secret - {}", vo.getAppSecret());
    }

}

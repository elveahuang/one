package cc.wdev.platform.commons.ai.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleRerankRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleRerankResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author elvea
 */
public class RerankServiceTests extends BaseTests {

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void dashScopeTest() {
        RerankModelService service = this.aiServiceManager.getRerankModelService(AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
        Assertions.assertNotNull(service);

        List<String> documents = Lists.newArrayList(
            "重排序模型广泛应用于搜索引擎和推荐系统，按相关性对候选文本进行排序",
            "量子计算是计算科学的前沿领域",
            "预训练语言模型的发展为重排序模型带来了新的进展"
        );

        SimpleRerankRequest request = SimpleRerankRequest.builder()
            .documents(documents)
            .build();
        SimpleRerankResponse<?> response = service.call(request);
        Assertions.assertNotNull(response);
    }

}

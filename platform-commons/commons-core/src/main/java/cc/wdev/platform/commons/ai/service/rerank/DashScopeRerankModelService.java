package cc.wdev.platform.commons.ai.service.rerank;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelRerankConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleRerankRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleRerankResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.ModelService;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.rerank.TextReRank;
import com.alibaba.dashscope.rerank.TextReRankParam;
import com.alibaba.dashscope.rerank.TextReRankResult;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildRerankModelConfig;

/**
 * 阿里云 DashScope 文档重排服务
 *
 * @author elvea
 */
public class DashScopeRerankModelService extends AbstractRerankModelService {

    public DashScopeRerankModelService(ModelCommonsConfig commonsConfig, ModelRerankConfig modelConfig) {
        super(buildRerankModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see RerankModelService#call(SimpleRerankRequest)
     */
    @Override
    public SimpleRerankResponse<TextReRankResult> call(SimpleRerankRequest request) {
        SimpleRerankResponse<TextReRankResult> response = new SimpleRerankResponse<>();
        try {
            TextReRankParam param = TextReRankParam.builder()
                .apiKey(this.config.getApiKey())
                .model(this.config.getName())
                .query(request.getQuery())
                .documents(request.getDocuments())
                .returnDocuments(false)
                .build();
            if (request.getTopN() != null && request.getTopN() > 0) {
                param.setTopN(request.getTopN());
            }

            TextReRank textReRank = new TextReRank();
            TextReRankResult result = textReRank.call(param);

            response.setRequestId(result.getRequestId());
            response.setResult(result);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

}

package cc.wdev.platform.commons.ai.service.rerank;

import cc.wdev.platform.commons.ai.domain.request.SimpleRerankRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleRerankResponse;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.service.ModelService;

/**
 * 文档重排模型服务
 *
 * @author elvea
 */
public interface RerankModelService extends ModelService {

    /**
     * @see ModelService#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.RERANK;
    }

    /**
     * 执行文档重排
     */
    SimpleRerankResponse<?> call(SimpleRerankRequest request);

}

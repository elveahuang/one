package cc.wdev.platform.commons.ai.service.rerank;

import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.service.ModelService;

/**
 * 重排模型服务抽象基类
 *
 * @author elvea
 */
public abstract class AbstractRerankModelService implements RerankModelService {

    protected final ModelConfig config;

    public AbstractRerankModelService(ModelConfig config) {
        this.config = config;
    }

    /**
     * @see ModelService#getModelConfig()
     */
    @Override
    public ModelConfig getModelConfig() {
        return this.config;
    }

}

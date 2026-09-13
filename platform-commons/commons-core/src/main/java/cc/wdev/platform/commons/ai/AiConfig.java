package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.config.*;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConfig implements Serializable {

    /**
     * 是否启用回退到默认模型
     * 默认不启用，启用后会优先使用当前配置的模型，如果当前模型不存在，则会回退到默认模型
     */
    @Builder.Default
    private boolean fallbackEnabled = false;

    /**
     * 模型供应商配置
     */
    @Builder.Default
    private Map<String, ModelProviderConfig> providers = Maps.newHashMap();

    /**
     * 厂商服务配置
     */
    @Builder.Default
    private ServiceProviderConfig service = new ServiceProviderConfig();

    /**
     * 标准服务配置
     */
    @Builder.Default
    private ServiceProviderConfig factory = new ServiceProviderConfig();

    /**
     * 知识检索配置
     */
    @Builder.Default
    private RagConfig rag = RagConfig.builder().build();

    /**
     * 智能体配置
     */
    @Builder.Default
    private AgentConfig agent = AgentConfig.builder().build();

    /**
     * 长期记忆配置
     */
    @Builder.Default
    private MemoryConfig memory = MemoryConfig.builder().build();

}

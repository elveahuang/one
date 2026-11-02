package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.*;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = AiProperties.PREFIX)
public class AiProperties {

    public static final String PREFIX = "platform.ai";

    private boolean enabled = false;

    /**
     * 是否启用回退到默认模型
     * 默认不启用，启用后会优先使用当前配置的模型，如果当前模型不存在，则会回退到默认模型
     */
    private boolean fallbackEnabled = false;

    /**
     * 模型供应商配置
     */
    private Map<String, ModelProviderConfig> providers = Maps.newHashMap();

    @NestedConfigurationProperty
    private ServiceProviderConfig service = ServiceProviderConfig.builder().build();

    @NestedConfigurationProperty
    private ServiceProviderConfig factory = ServiceProviderConfig.builder().build();

    @NestedConfigurationProperty
    private RagConfig rag = RagConfig.builder().build();

    @NestedConfigurationProperty
    private AgentConfig agent = AgentConfig.builder().build();

    @NestedConfigurationProperty
    private MemoryConfig memory = MemoryConfig.builder().build();

    /**
     * 会员端（MEMBER）可用模型编码白名单，仅约束 chatType=CHAT 的直连模型对话；
     * 为空表示不限制（保持既有行为）。智能体/知识库绑定的模型由管理端配置，不受此白名单约束。
     */
    private List<String> memberAllowedModels = new ArrayList<>();

}

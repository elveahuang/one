package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.*;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = AiProperties.PREFIX)
public class AiProperties {

    public static final String PREFIX = "platform.ai";

    public static final String PROVIDER_PREFIX = PREFIX + ".providers";

    public static final String PROVIDER_DEEPSEEK = PROVIDER_PREFIX + ".deepseek";

    public static final String PROVIDER_OPENAI = PROVIDER_PREFIX + ".openai";

    public static final String PROVIDER_ANTHROPIC = PROVIDER_PREFIX + ".anthropic";

    public static final String PROVIDER_TENCENT = PROVIDER_PREFIX + ".tencent";

    public static final String PROVIDER_ALIYUN = PROVIDER_PREFIX + ".aliyun";

    public static final String PROVIDER_ORCA_ROUTER = PROVIDER_PREFIX + ".orcarouter";

    public static final String RAG_PREFIX = PREFIX + ".rag";

    public static final String VECTOR_STORE_PREFIX = PREFIX + ".vectorstore";

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
    private ServiceProviderConfig service = new ServiceProviderConfig();

    @NestedConfigurationProperty
    private ServiceProviderConfig factory = new ServiceProviderConfig();

    @NestedConfigurationProperty
    private VectorStoreConfig vectorstore = VectorStoreConfig.builder().build();

    @NestedConfigurationProperty
    private RagConfig rag = RagConfig.builder().build();

    @NestedConfigurationProperty
    private AgentConfig agent = AgentConfig.builder().build();

    @NestedConfigurationProperty
    private MemoryConfig memory = MemoryConfig.builder().build();

}

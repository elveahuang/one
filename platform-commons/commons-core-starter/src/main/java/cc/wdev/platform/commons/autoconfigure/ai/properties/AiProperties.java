package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

    public static final String PROVIDER_TENCENT = PROVIDER_PREFIX + ".tencent";

    public static final String PROVIDER_ALIYUN = PROVIDER_PREFIX + ".aliyun";

    public static final String PROVIDER_ORCA_ROUTER = PROVIDER_PREFIX + ".orcarouter";

    public static final String RAG_PREFIX = PREFIX + ".rag";

    public static final String VECTOR_STORE_PREFIX = PREFIX + ".vectorstore";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private ServiceProviderConfig service = new ServiceProviderConfig();

    @NestedConfigurationProperty
    private ServiceProviderConfig factory = new ServiceProviderConfig();

    @NestedConfigurationProperty
    private VectorStoreConfig vectorstore = VectorStoreConfig.builder().build();

    @NestedConfigurationProperty
    private SplittingConfig splitting = SplittingConfig.builder().build();

    @NestedConfigurationProperty
    private RetrievalConfig retrieval = RetrievalConfig.builder().build();

    @NestedConfigurationProperty
    private VectorizationConfig vectorization = VectorizationConfig.builder().build();

    @NestedConfigurationProperty
    private WorkSpaceConfig workspace = WorkSpaceConfig.builder().build();

    @NestedConfigurationProperty
    private SkillConfig skill = SkillConfig.builder().build();

}

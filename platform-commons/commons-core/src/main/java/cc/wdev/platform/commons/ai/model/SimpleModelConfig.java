package cc.wdev.platform.commons.ai.model;

import cc.wdev.platform.commons.ai.AiConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleModelConfig implements ModelConfig {
    /**
     *
     */
    private String name;
    /**
     *
     */
    private String apiKey;
    /**
     *
     */
    private String workspaceId;
    /**
     *
     */
    private String baseUrl;
    /**
     *
     */
    private String modelType;
    /**
     * 模型提供商
     */
    private String modelProvider;
    /**
     * 服务提供商
     */
    private String serviceProvider;
    /**
     * 请求头
     */
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    /**
     * 默认超时时间
     */
    @Builder.Default
    private Duration timeout = AiConstants.DEFAULT_TIMEOUT;
    /**
     * 重试次数
     */
    @Builder.Default
    private int maxRetries = AiConstants.DEFAULT_MAX_RETRIES;
    /**
     * 代理
     */
    private Proxy proxy;
    /**
     * 参数
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();
    /**
     * 是否启用思考模式
     */
    @Builder.Default
    private Boolean returnThinking = false;
    /**
     * 是否启用搜索引擎
     */
    @Builder.Default
    private Boolean enableWebSearch = false;
}

package cc.wdev.platform.commons.ai.model;

import java.net.Proxy;
import java.time.Duration;
import java.util.Map;

/**
 * @author elvea
 */
public interface ModelConfig {

    /**
     * 获取模型名称
     */
    String getName();

    /**
     * 获取 API Key
     */
    String getApiKey();

    /**
     * 获取 Workspace ID
     */
    String getWorkspaceId();

    /**
     * 获取 API Base URL
     */
    String getBaseUrl();

    /**
     * 获取模型提供商
     */
    String getModelProvider();

    /**
     * 获取服务提供商
     */
    String getServiceProvider();

    /**
     * 获取模型类型
     */
    String getModelType();

    /**
     * 获取请求头参数
     */
    Map<String, String> getHeaders();

    /**
     * 获取超时时间
     */
    Duration getTimeout();

    /**
     * 获取重试次数
     */
    int getMaxRetries();

    /**
     * 获取代理
     */
    Proxy getProxy();

    /**
     * 获取扩展参数
     */
    Map<String, Object> getVariables();

    /**
     * 是否启用深度思考模式
     */
    default Boolean getReturnThinking() {
        return false;
    }

    /**
     * 是否启用联网搜索
     */
    default Boolean getEnableWebSearch() {
        return false;
    }

    /**
     * 是否启用内部工具执行
     */
    default Boolean getInternalToolExecutionEnabled() {
        return true;
    }

}

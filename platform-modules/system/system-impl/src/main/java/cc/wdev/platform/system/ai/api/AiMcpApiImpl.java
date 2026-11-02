package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.domain.DeleteRequest;
import cc.wdev.platform.commons.domain.GetRequest;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import cc.wdev.platform.system.ai.service.AiMcpServerService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.spec.McpClientTransport;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP服务 API 实现。
 *
 * <p>按 {@code sys_ai_mcp_server} 登记的配置**编程式**建立 MCP 连接（yml 静态配置无法覆盖库里动态增删），
 * 并把连接缓存为 {@link McpSyncClient}，供 Agent 对话时挂载外部工具。</p>
 *
 * <p>支持的传输（由 {@code protocol} 字段决定，大小写不敏感）：</p>
 * <ul>
 *   <li>{@code STREAMABLE} / {@code STREAMABLE_HTTP}（默认）：Streamable HTTP</li>
 *   <li>{@code STDIO}：本地子进程，需配 {@code command} / {@code args} / {@code env}</li>
 * </ul>
 *
 * <p>历史 {@code SSE} 传输已在 MCP 规范中废弃，按最新规范统一走 Streamable HTTP。</p>
 *
 * <p>注意：连接失败只记录日志并返回空工具，**不能让 MCP 故障拖垮正常对话**。</p>
 *
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiMcpApiImpl implements AiMcpApi {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private static final String PROTOCOL_STDIO = "STDIO";

    private final AiMcpServerService aiMcpServerService;

    /**
     * 已建立的连接缓存：key 含地址与传输指纹，配置变更后会重建
     */
    private final Map<String, McpSyncClient> clients = new ConcurrentHashMap<>();

    /**
     * @see AiMcpApi#findAiMcpServers(AiMcpServerSearchRequest)
     */
    @Override
    public Page<AiMcpServerVo> findAiMcpServers(AiMcpServerSearchRequest request) {
        return this.aiMcpServerService.findByPage(request);
    }

    /**
     * @see AiMcpApi#getAiMcpServer(GetRequest)
     */
    @Override
    public AiMcpServerVo getAiMcpServer(GetRequest request) {
        if (request == null) {
            return new AiMcpServerVo();
        }
        if (request.getId() != null) {
            return this.aiMcpServerService.getAiMcpServer(request.getId());
        }
        if (StringUtils.isNotEmpty(request.getCode())) {
            return this.aiMcpServerService.getAiMcpServerByCode(request.getCode());
        }
        return new AiMcpServerVo();
    }

    /**
     * @see AiMcpApi#saveAiMcpServer(AiMcpServerSaveRequest)
     */
    @Override
    public void saveAiMcpServer(AiMcpServerSaveRequest request) {
        if (request == null) {
            return;
        }
        // 更新场景先记下旧 code：code 可能被改写，旧连接需要一并清理
        String previousCode = null;
        if (request.getId() != null && request.getId() > 0) {
            AiMcpServerVo existing = this.aiMcpServerService.getAiMcpServer(request.getId());
            if (existing != null) {
                previousCode = existing.getCode();
            }
        }
        this.aiMcpServerService.saveAiMcpServer(request);
        // 配置可能已变更（url/headers/env/command 等），清掉旧连接让下次对话按新配置重建
        this.evict(previousCode);
        this.evict(request.getCode());
    }

    /**
     * @see AiMcpApi#deleteAiMcpServer(DeleteRequest)
     */
    @Override
    public void deleteAiMcpServer(DeleteRequest request) {
        if (request == null || request.getIds() == null || request.getIds().length == 0) {
            return;
        }
        List<Long> ids = Arrays.asList(request.getIds());
        // 先按 code 清理已建立的 MCP 连接，避免删除后连接与子进程泄漏
        ids.stream()
            .map(this.aiMcpServerService::getAiMcpServer)
            .filter(Objects::nonNull)
            .map(AiMcpServerVo::getCode)
            .filter(StringUtils::isNotEmpty)
            .forEach(this::evict);
        this.aiMcpServerService.deleteAiMcpServer(ids);
    }

    /**
     * @see AiMcpApi#buildToolCallbackProvider(List)
     */
    @Override
    public ToolCallbackProvider buildToolCallbackProvider(List<Long> mcpServerIds) {
        if (CollectionUtils.isEmpty(mcpServerIds)) {
            return null;
        }
        List<McpSyncClient> available = new ArrayList<>(mcpServerIds.size());
        for (Long mcpServerId : mcpServerIds) {
            AiMcpServerVo server = this.aiMcpServerService.getAiMcpServer(mcpServerId);
            if (server == null || StringUtils.isEmpty(server.getCode())) {
                continue;
            }
            McpSyncClient client = this.getOrCreate(server);
            if (client != null) {
                available.add(client);
            }
        }
        if (available.isEmpty()) {
            return null;
        }
        return SyncMcpToolCallbackProvider.builder()
            .mcpClients(available)
            .build();
    }

    /**
     * 关闭全部连接（容器销毁时调用）。
     */
    @PreDestroy
    public void closeAll() {
        this.clients.values().forEach(this::closeQuietly);
        this.clients.clear();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 私有辅助
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 获取或创建到指定 MCP Server 的连接。
     */
    private McpSyncClient getOrCreate(AiMcpServerVo server) {
        String cacheKey = this.cacheKey(server);
        McpSyncClient cached = this.clients.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        synchronized (this.clients) {
            cached = this.clients.get(cacheKey);
            if (cached != null) {
                return cached;
            }
            try {
                McpSyncClient client = McpClient.sync(this.resolveTransport(server))
                    .requestTimeout(REQUEST_TIMEOUT)
                    .build();
                client.initialize();
                this.clients.put(cacheKey, client);
                log.info("MCP client connected. code - [{}], protocol - [{}].", server.getCode(), server.getProtocol());
                return client;
            } catch (Exception e) {
                // 单个 MCP Server 不可用不能影响主流程
                log.warn("MCP client connect failed. code - [{}], url - [{}].", server.getCode(), server.getUrl(), e);
                return null;
            }
        }
    }

    /**
     * 关闭并移除指定 Server 的连接（配置变更或删除时调用）。
     */
    private void evict(String code) {
        if (StringUtils.isEmpty(code)) {
            return;
        }
        this.clients.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(code + "|")) {
                this.closeQuietly(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private void closeQuietly(McpSyncClient client) {
        try {
            client.closeGracefully();
        } catch (Exception e) {
            log.debug("MCP client close failed.", e);
        }
    }

    /**
     * 缓存键包含地址、传输与鉴权/环境配置摘要。
     * <p>必须覆盖 headers/env：改鉴权头或环境变量是最常见的配置变更，若不进键会复用旧连接、改了等于没改。
     * 配置变更后旧键的连接由 {@link #evict(String)} 清理。</p>
     */
    private String cacheKey(AiMcpServerVo server) {
        return server.getCode() + "|" + StringUtils.nvl(server.getProtocol()) + "|"
            + StringUtils.nvl(server.getUrl()) + "|" + StringUtils.nvl(server.getCommand())
            + "|" + StringUtils.nvl(server.getHeaders()) + "|" + StringUtils.nvl(server.getEnv())
            + "|" + StringUtils.nvl(server.getArgs());
    }

    private McpClientTransport resolveTransport(AiMcpServerVo server) {
        String protocol = StringUtils.nvl(server.getProtocol()).trim().toUpperCase();
        if (PROTOCOL_STDIO.equals(protocol)) {
            return this.buildStdioTransport(server);
        }
        // SSE 传输已被 MCP 规范废弃，所有 HTTP 型传输统一走 Streamable HTTP
        HttpClientStreamableHttpTransport.Builder builder = HttpClientStreamableHttpTransport.builder(server.getUrl());
        Map<String, String> headers = this.parseMap(server.getHeaders());
        if (!headers.isEmpty()) {
            builder.addHttpRequestCustomizer((request, method, uri, body, context) -> this.applyHeaders(request, headers));
        }
        return builder.build();
    }

    /**
     * 把登记的请求头写入 MCP HTTP 请求（鉴权等）
     */
    private void applyHeaders(java.net.http.HttpRequest.Builder request, Map<String, String> headers) {
        headers.forEach(request::header);
    }

    private McpClientTransport buildStdioTransport(AiMcpServerVo server) {
        ServerParameters.Builder builder = ServerParameters.builder(server.getCommand());
        List<String> args = this.parseArgs(server.getArgs());
        if (!args.isEmpty()) {
            builder.args(args);
        }
        Map<String, String> env = this.parseMap(server.getEnv());
        if (!env.isEmpty()) {
            builder.env(env);
        }
        return new StdioClientTransport(builder.build(), McpJsonDefaults.getMapper());
    }

    private Map<String, String> parseMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return Map.of();
        }
        try {
            Map<String, Object> raw = JacksonUtils.toMap(json);
            if (raw == null || raw.isEmpty()) {
                return Map.of();
            }
            Map<String, String> result = new HashMap<>(raw.size());
            raw.forEach((key, value) -> {
                if (value != null) {
                    result.put(key, String.valueOf(value));
                }
            });
            return result;
        } catch (Exception e) {
            log.warn("MCP server config is not a valid JSON object, ignored. value - [{}].", json);
            return Map.of();
        }
    }

    /**
     * args 约定为 JSON 数组；也兼容逗号分隔的简写
     */
    private List<String> parseArgs(String json) {
        if (StringUtils.isEmpty(json)) {
            return List.of();
        }
        String trimmed = json.trim();
        try {
            if (trimmed.startsWith("[")) {
                Map<String, Object> wrapper = JacksonUtils.toMap("{\"v\":" + trimmed + "}");
                if (wrapper == null || !(wrapper.get("v") instanceof List<?> raw) || raw.isEmpty()) {
                    return List.of();
                }
                return raw.stream().filter(Objects::nonNull).map(String::valueOf).toList();
            }
            return List.of(trimmed.split(","));
        } catch (Exception e) {
            log.warn("MCP server args is not a valid JSON array, ignored. value - [{}].", json);
            return List.of();
        }
    }

}

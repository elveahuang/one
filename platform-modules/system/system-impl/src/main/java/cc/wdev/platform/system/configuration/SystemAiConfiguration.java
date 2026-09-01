package cc.wdev.platform.system.configuration;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.system.ai.api.AiApiKeyApi;
import cc.wdev.platform.system.ai.service.AiChatMemoryService;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import cc.wdev.platform.system.ai.support.CustomApiKeyRepository;
import cc.wdev.platform.system.ai.support.CustomChatMemoryRepository;
import cc.wdev.platform.system.ai.support.CustomSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.SessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

/**
 * @author elvea
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableScheduling
public class SystemAiConfiguration {

    public SystemAiConfiguration() {
        log.info("SystemAiConfiguration is enabled");
    }

    // ------------------------------------------------------------------------------
    // MCP Security
    // ------------------------------------------------------------------------------

    @Bean
    public ApiKeyEntityRepository<ApiKeyEntity> apiKeyEntityRepository(AiApiKeyApi aiApiKeyApi) {
        return new CustomApiKeyRepository(aiApiKeyApi);
    }

    // ------------------------------------------------------------------------------
    // Spring AI Session
    // ------------------------------------------------------------------------------

    @Bean
    public SessionService sessionService(AiSessionService aiSessionService,
                                         AiSessionEventService aiSessionEventService) {
        return DefaultSessionService.builder()
            .sessionRepository(new CustomSessionRepository(aiSessionService, aiSessionEventService))
            .defaultTimeToLive(Duration.ofDays(60))
            .build();
    }

    // ------------------------------------------------------------------------------
    // Spring AI Chat Memory
    // ------------------------------------------------------------------------------

    @Bean
    public ChatMemoryRepository chatMemoryRepository(AiChatMemoryService aiChatMemoryService) {
        return new CustomChatMemoryRepository(aiChatMemoryService);
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(AiConstants.MAX_MEMORY_MESSAGE_COUNT)
            .build();
    }

}

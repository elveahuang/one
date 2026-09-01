package cc.wdev.webapp.configuration;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.webapp.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;
import org.springaicommunity.mcp.security.server.apikey.memory.ApiKeyEntityImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static cc.wdev.platform.commons.constants.SecurityConstants.*;
import static org.springaicommunity.mcp.security.server.config.McpApiKeyConfigurer.mcpServerApiKey;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration {

    private final ApiKeyEntityRepository<ApiKeyEntityImpl> apiKeyRepository;

    @Bean
    @Order(1)
    SecurityFilterChain apiMcpSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating apiMcpSecurityFilterChain for webapp...");

        http.securityMatcher(API_MCP_REQUEST_PATH)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .with(mcpServerApiKey(), (apiKey) -> apiKey.apiKeyRepository(this.apiKeyRepository));
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating apiSecurityFilterChain for webapp...");

        http.securityMatcher(API_REQUEST_PATH)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(SecurityConstants.GLOBAL_WEB_EXCLUDE_URLS).permitAll()
                .requestMatchers(SystemConstants.WEB_EXCLUDE_URLS).permitAll()
                .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating defaultSecurityFilterChain for App Server");
        log.info("defaultSecurityFilterChain Whitelist path : [{}]", JacksonUtils.toJson(GLOBAL_WEB_EXCLUDE_URLS));

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(SecurityConstants.GLOBAL_WEB_EXCLUDE_URLS).permitAll()
                .requestMatchers(SystemConstants.WEB_EXCLUDE_URLS).permitAll()
                .anyRequest().permitAll());
        return http.build();
    }

}

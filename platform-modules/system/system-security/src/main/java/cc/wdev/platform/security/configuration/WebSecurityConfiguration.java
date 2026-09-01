package cc.wdev.platform.security.configuration;

import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.security.core.service.CustomApiKeyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static cc.wdev.platform.commons.constants.SecurityConstants.*;
import static org.springaicommunity.mcp.security.server.config.McpApiKeyConfigurer.mcpServerApiKey;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration {

    private final GlobalContext context;

    private final JwtDecoder jwtDecoder;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    private final CustomApiKeyService customApiKeyService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(this.context.getDebug().isEnabled());
    }

    /**
     * 接口安全设置
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiMcpSecurityFilterChain(HttpSecurity http) {
        log.info("Creating apiMcpSecurityFilterChain for App Server...");

        http.securityMatcher(API_MCP_REQUEST_PATH)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .with(mcpServerApiKey(), (apiKey) -> apiKey.apiKeyRepository(customApiKeyService));
        return http.build();
    }

    /**
     * 接口安全设置
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating apiSecurityFilterChain for App Server...");
        log.info("Whitelist [api] : [{}].", JacksonUtils.toJson(GLOBAL_WEB_EXCLUDE_URLS));

        http.securityMatcher(API_REQUEST_PATH)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(GLOBAL_API_EXCLUDE_URLS).permitAll()
                .anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer((resourceServerConfigurer) -> resourceServerConfigurer.jwt(jwtConfigurer -> {
                jwtConfigurer.decoder(jwtDecoder);
                jwtConfigurer.jwtAuthenticationConverter(this.jwtAuthenticationConverter);
            }));
        return http.build();
    }

    /**
     * 默认安全设置
     */
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating defaultSecurityFilterChain for App Server...");
        log.info("Whitelist [default] : [{}].", JacksonUtils.toJson(GLOBAL_WEB_EXCLUDE_URLS));

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(GLOBAL_WEB_EXCLUDE_URLS).permitAll()
                .anyRequest().permitAll())
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .oauth2ResourceServer((resourceServerConfigurer) -> resourceServerConfigurer.jwt(jwtConfigurer -> {
                jwtConfigurer.decoder(jwtDecoder);
                jwtConfigurer.jwtAuthenticationConverter(this.jwtAuthenticationConverter);
            }));
        return http.build();
    }

}

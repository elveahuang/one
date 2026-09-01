package cc.wdev.platform.admin.configuration;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author elvea
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration {

    private final AdminServerProperties adminServer;

    public WebSecurityConfiguration(AdminServerProperties adminServer) {
        log.info("Admin Server [{}] start.", adminServer.getContextPath());
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

        http.headers((header) -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(this.adminServer.path("/actuator/info")).permitAll()
                .requestMatchers(this.adminServer.path("/actuator/health")).permitAll()
                .requestMatchers(this.adminServer.path("/assets/**")).permitAll()
                .requestMatchers(this.adminServer.path("/login")).permitAll()
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                .anyRequest().authenticated())
            .formLogin((formLogin) ->
                formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler))
            .logout((logout) ->
                logout.logoutUrl(this.adminServer.path("/logout")))
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}

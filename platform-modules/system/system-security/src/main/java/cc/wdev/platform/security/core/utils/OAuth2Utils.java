package cc.wdev.platform.security.core.utils;

import cc.wdev.platform.commons.security.domain.User;
import cc.wdev.platform.commons.security.jackson.LongMixin;
import cc.wdev.platform.commons.security.jackson.UserMixin;
import cc.wdev.platform.commons.utils.jackson.CommonModule;
import cc.wdev.platform.system.security.domain.dto.ClientDto;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
public abstract class OAuth2Utils {

    public static ObjectMapper objectMapper;

    static {
        ClassLoader loader = OAuth2Utils.class.getClassLoader();

        BasicPolymorphicTypeValidator.Builder builder = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType(Long.class)
            .allowIfSubType(User.class);

        objectMapper = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(loader, builder))
            .addModule(new CommonModule())
            .addMixIn(Long.class, LongMixin.class)
            .addMixIn(User.class, UserMixin.class)
            .build();
    }

    public static ClientDto toRegisteredClientDto(RegisteredClient client) {
        List<String> clientAuthenticationMethods = new ArrayList<>(client.getClientAuthenticationMethods().size());
        client.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
            clientAuthenticationMethods.add(clientAuthenticationMethod.getValue())
        );
        List<String> authorizationGrantTypes = new ArrayList<>(client.getAuthorizationGrantTypes().size());
        client.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
            authorizationGrantTypes.add(authorizationGrantType.getValue())
        );
        ClientDto entity = new ClientDto();
        entity.setClientId(client.getClientId());
        entity.setClientName(client.getClientName());
        entity.setClientSecret(client.getClientSecret());
        entity.setClientIdIssuedAt(null);
        entity.setClientSecretExpiresAt(null);
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(client.getRedirectUris()));
        entity.setPostLogoutRedirectUris(StringUtils.collectionToCommaDelimitedString(client.getPostLogoutRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(client.getScopes()));
        entity.setClientSettings(writeMap(client.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(client.getTokenSettings().getSettings()));
        return entity;
    }

    public static RegisteredClient toRegisteredClient(ClientDto client) {
        return toRegisteredClient(client, TokenSettings.builder().build());
    }

    public static RegisteredClient toRegisteredClient(ClientDto client, TokenSettings tokenSettings) {
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(client.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(client.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(client.getRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(client.getScopes());

        RegisteredClient.Builder builder = RegisteredClient.withId(String.valueOf(client.getClientId()))
            .clientId(client.getClientId())
            .clientName(client.getClientName())
            .clientSecret(client.getClientSecret())
            .clientAuthenticationMethods(authenticationMethods ->
                clientAuthenticationMethods.forEach(authenticationMethod ->
                    authenticationMethods.add(
                        OAuth2Utils.resolveClientAuthenticationMethod(authenticationMethod)
                    )
                )
            )
            .authorizationGrantTypes((grantTypes) ->
                authorizationGrantTypes.forEach(grantType ->
                    grantTypes.add(
                        OAuth2Utils.resolveAuthorizationGrantType(grantType)
                    )
                )
            )
            .redirectUris((uris) -> uris.addAll(redirectUris))
            .tokenSettings(tokenSettings)
            .scopes((scopes) -> scopes.addAll(clientScopes));
        if (client.getClientIdIssuedAt() != null) {
            builder.clientIdIssuedAt(client.getClientIdIssuedAt().toInstant(ZoneOffset.UTC));
        }
        if (client.getClientSecretExpiresAt() != null) {
            builder.clientSecretExpiresAt(client.getClientSecretExpiresAt().toInstant(ZoneOffset.UTC));
        }
        return builder.build();
    }

    public static String writeMap(Map<String, Object> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static Map<String, Object> parseMap(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }

    public static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
    }

}

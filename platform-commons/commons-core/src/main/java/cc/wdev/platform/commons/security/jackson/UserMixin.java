package cc.wdev.platform.commons.security.jackson;

import cc.wdev.platform.commons.security.domain.User;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.core.GrantedAuthority;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;

/**
 * @author elvea
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonDeserialize(using = UserDeserializer.class)
public abstract class UserMixin {
}

class UserDeserializer extends ValueDeserializer<User> {

    private static final TypeReference<Set<GrantedAuthority>> GRANTED_AUTHORITY_SET = new TypeReference<>() {
    };

    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode root = context.readTree(parser);
        return deserialize(parser, context, root);
    }

    private User deserialize(JsonParser parser, DeserializationContext context, JsonNode root) {
        Long uid = JacksonUtils.findLongValue(root, "id");
        Long tid = JacksonUtils.findLongValue(root, "tid");
        String username = JacksonUtils.findStringValue(root, "username");
        String password = JacksonUtils.findStringValue(root, "password");
        boolean enabled = JacksonUtils.findBooleanValue(root, "enabled");
        Set<GrantedAuthority> authorities = JacksonUtils.findValue(root, "authorities", GRANTED_AUTHORITY_SET, context);
        boolean accountNonExpired = JacksonUtils.findBooleanValue(root, "accountNonExpired");
        boolean credentialsNonExpired = JacksonUtils.findBooleanValue(root, "credentialsNonExpired");
        boolean accountNonLocked = JacksonUtils.findBooleanValue(root, "accountNonLocked");
        return new User(tid, uid, username, password, authorities, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired);
    }

}

package cc.wdev.platform.commons.security.jackson;

import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

/**
 * @author elvea
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonDeserialize(using = LongDeserializer.class)
public abstract class LongMixin {
}

class LongDeserializer extends ValueDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode root = context.readTree(parser);
        return deserialize(parser, context, root);
    }

    private Long deserialize(JsonParser parser, DeserializationContext context, JsonNode root) {
        String value = JacksonUtils.findStringValue(root, "value");
        if (StringUtils.isNotEmpty(value)) {
            return Long.valueOf(value);
        }
        return null;
    }

}

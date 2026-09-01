package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.utils.jackson.CommonModule;
import lombok.Getter;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
public abstract class JacksonUtils {

    public final static TypeReference<Set<String>> STRING_SET = new TypeReference<>() {
    };

    public final static TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<>() {
    };

    @Getter
    public final static JsonMapper objectMapper = JsonMapper.builder()
        .addModule(new CommonModule())
        .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
        .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
        .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
        .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .build();

    /**
     * 用于某些缓存场景通过 Jaskcon 序列化对象时，
     * 忽略 @JsonIgnore 注解，避免反序列化字段丢失
     */
    public static JsonMapper getCacheObjectMapper() {
        JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(MapperConfig<?> config, AnnotatedMember m) {
                return false;
            }
        };

        return JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .annotationIntrospector(introspector)
            .build();
    }

    /**
     * 把对象转换成JSON文本
     */
    public static String toJson(Object obj) throws Exception {
        return getObjectMapper().writeValueAsString(obj);
    }

    /**
     * 把JSON文本转成对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return getObjectMapper().readValue(json, clazz);
    }

    /**
     * 把JSON文本转成对象
     */
    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        return getObjectMapper().readValue(json, typeReference);
    }

    /**
     * 把JSON文本转成对象
     */
    public static Map<String, Object> toMap(String json) {
        return getObjectMapper().readValue(json, new TypeReference<>() {
        });
    }

    /**
     *
     */
    public static byte[] toBytes(Object object) {
        return getObjectMapper().writeValueAsBytes(object);
    }

    public static Long findLongValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode value = jsonNode.findValue(fieldName);
        return (value != null) ? value.asLong() : null;
    }

    public static boolean findBooleanValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null) {
            return false;
        }
        JsonNode value = jsonNode.findValue(fieldName);
        return value != null && value.isString() && value.asBoolean();
    }

    public static String findStringValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode value = jsonNode.findValue(fieldName);
        return (value != null && value.isString()) ? value.asString() : null;
    }

    public static <T> T findValue(JsonNode jsonNode, String fieldName, TypeReference<T> valueTypeReference, DeserializationContext context) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode value = jsonNode.findValue(fieldName);
        return (value != null && value.isContainer())
            ? context.readTreeAsValue(value, context.getTypeFactory().constructType(valueTypeReference)) : null;
    }

    public static JsonNode findObjectNode(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode value = jsonNode.findValue(fieldName);
        return (value != null && value.isObject()) ? value : null;
    }

}

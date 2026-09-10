package cc.wdev.platform.commons.ai.ui;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@NoArgsConstructor
public class UiComponentRegistry {

    private final Map<String, UiComponentDefinition> definitions = Maps.newHashMap();

    public void register(List<UiComponentDefinition> definitions) {
        CollectionUtils.nvl(definitions, Lists.newArrayList()).forEach(this::register);
    }

    public void register(UiComponentDefinition definition) {
        this.definitions.put(definition.type(), definition);
    }

    public String buildJsonSchema() {
        List<Map<String, Object>> variants = definitions.values().stream().map(d -> Map.of(
            "type", "object",
            "properties", Map.of(
                "id", Map.of("type", "string"),
                "type", Map.of("type", "string", "const", d.type()),
                "props", d.propsSchema()
            ),
            "required", List.of("id", "type", "props"),
            "additionalProperties", false
        )).toList();

        Map<String, Object> schema = Map.of(
            "$schema", "https://json-schema.org/draft/2020-12/schema",
            "type", "object",
            "properties", Map.of(
                "blocks", Map.of(
                    "type", "array",
                    "items", Map.of("oneOf", variants)
                )
            ),
            "required", List.of("blocks"),
            "additionalProperties", false
        );

        try {
            return JacksonUtils.toJson(schema);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build UI schema", e);
        }
    }

    public UiOutputConverter getConverter() {
        return new UiOutputConverter(this.buildJsonSchema());
    }

}

package cc.wdev.platform.commons.ai.ui;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@NoArgsConstructor
public class UiComponentRegistry {

    private static final String SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";

    private final Map<String, UiComponentDefinition> definitions = Maps.newLinkedHashMap();

    public void register(List<UiComponentDefinition> definitions) {
        CollectionUtils.nvl(definitions, Lists.newArrayList()).forEach(this::register);
    }

    public void register(UiComponentDefinition definition) {
        this.definitions.put(definition.type(), definition);
    }

    public String buildJsonSchema() {
        Map<String, Object> schema = UiSchemaUtils.of(UiResponseSchema.class);
        Map<String, Object> blocks = node(node(schema, "properties"), "blocks");
        blocks.put("items", Map.of("oneOf", this.definitions.values().stream().map(this::blockSchema).toList()));
        schema.put("$schema", SCHEMA_VERSION);

        try {
            return JacksonUtils.toJson(schema);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build UI schema", e);
        }
    }

    /**
     * 单个 block 信封，type 与 props 由组件定义决定
     */
    private Map<String, Object> blockSchema(UiComponentDefinition definition) {
        Map<String, Object> schema = UiSchemaUtils.of(BlockEnvelope.class);
        Map<String, Object> properties = node(schema, "properties");
        node(properties, "type").put("const", definition.type());
        properties.put("props", definition.propsSchema());
        return schema;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> node(Map<String, Object> schema, String key) {
        return (Map<String, Object>) schema.get(key);
    }

    public UiOutputConverter getConverter() {
        return new UiOutputConverter(this.buildJsonSchema());
    }

    public record UiResponseSchema(
        @Schema(description = "UI 块列表，按前端渲染顺序排列")
        List<Map<String, Object>> blocks) {
    }

    public record BlockEnvelope(
        @Schema(description = "块 ID，同一响应内唯一")
        String id,
        @Schema(description = "块类型，取组件定义的 type")
        String type,
        @Schema(description = "块数据，结构由 type 对应的组件定义")
        Map<String, Object> props) {
    }

}

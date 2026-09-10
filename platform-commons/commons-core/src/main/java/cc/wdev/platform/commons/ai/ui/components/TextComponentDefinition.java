package cc.wdev.platform.commons.ai.ui.components;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;

import java.util.Map;

/**
 * @author elvea
 */
public class TextComponentDefinition implements UiComponentDefinition {

    @Override
    public String type() {
        return "text";
    }

    @Override
    public String description() {
        return "普通 Markdown 文本";
    }

    @Override
    public Map<String, Object> propsSchema() {
        return Map.of(
            "type", "object",
            "properties", Map.of("content", Map.of("type", "string")),
            "required", java.util.List.of("content"),
            "additionalProperties", false
        );
    }

}

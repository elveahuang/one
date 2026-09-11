package cc.wdev.platform.commons.ai.ui.components;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.ai.ui.UiSchemaUtils;
import io.swagger.v3.oas.annotations.media.Schema;

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
        return UiSchemaUtils.of(Props.class);
    }

    public record Props(
        @Schema(description = "文本内容")
        String content) {
    }

}

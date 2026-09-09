package cc.wdev.platform.commons.ai.ui.components;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.utils.JacksonUtils;
import tools.jackson.databind.node.ObjectNode;

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
    public ObjectNode propsSchema() {
        ObjectNode schema = JacksonUtils.getSimpleObjectMapper().createObjectNode();
        schema.put("type", "object");
        schema.putArray("required").add("content");
        ObjectNode props = schema.putObject("properties");
        props.putObject("content").put("type", "string");
        return schema;
    }

}

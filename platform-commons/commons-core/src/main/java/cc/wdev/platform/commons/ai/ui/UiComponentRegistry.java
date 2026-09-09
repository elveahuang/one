package cc.wdev.platform.commons.ai.ui;

import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * @author elvea
 */
@NoArgsConstructor
public class UiComponentRegistry {

    private final List<UiComponentDefinition> definitions = Lists.newArrayList();

    private final ObjectMapper mapper = new ObjectMapper();

    public void register(List<UiComponentDefinition> definitions) {
        this.definitions.addAll(definitions);
    }

    public void register(UiComponentDefinition definition) {
        this.definitions.add(definition);
    }

    public String buildJsonSchema() {
        ObjectNode root = mapper.createObjectNode();
        root.put("type", "object");

        ObjectNode properties = root.putObject("properties");
        ObjectNode blocks = properties.putObject("blocks");
        blocks.put("type", "array");

        ObjectNode items = blocks.putObject("items");
        var oneOf = items.putArray("oneOf");

        for (UiComponentDefinition definition : definitions) {
            ObjectNode block = mapper.createObjectNode();
            block.put("type", "object");

            ObjectNode blockProps = block.putObject("properties");
            blockProps.putObject("id").put("type", "string");

            var type = blockProps.putObject("type");
            type.put("type", "string");
            type.put("const", definition.type());

            blockProps.set("props", definition.propsSchema());

            block.putArray("required")
                .add("id")
                .add("type")
                .add("props");

            oneOf.add(block);
        }

        root.putArray("required").add("blocks");
        return root.toString();
    }

    public String buildComponentInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Allowed UI components and props:\n");
        for (UiComponentDefinition definition : definitions) {
            sb.append("- ")
                .append(definition.type())
                .append(": ")
                .append(definition.description())
                .append("; props schema = ")
                .append(definition.propsSchema().toPrettyString())
                .append('\n');
        }
        return sb.toString();
    }

}

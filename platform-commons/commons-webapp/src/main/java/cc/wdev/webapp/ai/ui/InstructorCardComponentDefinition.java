package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.utils.JacksonUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ObjectNode;

/**
 * @author elvea
 */
@Component
public class InstructorCardComponentDefinition implements UiComponentDefinition {

    @Override
    public String type() {
        return "instructor-card";
    }

    @Override
    public String description() {
        return "Instructor recommendation card. AI should provide instructorId, instructorName, and instructorDetails.";
    }

    @Override
    public ObjectNode propsSchema() {
        ObjectNode schema = JacksonUtils.getSimpleObjectMapper().createObjectNode();
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("instructorId").put("type", "string");
        props.putObject("instructorName").put("type", "string");
        props.putObject("instructorDetails").put("type", "string");
        schema.putArray("required").add("content");
        return schema;
    }

}

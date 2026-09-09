package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.utils.JacksonUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ObjectNode;

@Component
public class CourseCardComponentDefinition implements UiComponentDefinition {

    public String type() {
        return "course-card";
    }

    public String description() {
        return "Course recommendation card. AI should provide courseId and reason; backend enriches facts.";
    }

    @Override
    public ObjectNode propsSchema() {
        ObjectNode schema = JacksonUtils.getSimpleObjectMapper().createObjectNode();
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("courseId").put("type", "string");
        props.putObject("courseTitle").put("type", "string");
        props.putObject("courseSummary").put("type", "string");
        schema.putArray("required").add("content");
        return schema;
    }

}

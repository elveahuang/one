package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Component
public class CourseCardComponentDefinition implements UiComponentDefinition {

    public String type() {
        return "course-card";
    }

    public String description() {
        return "Course recommendation card. AI should provide courseId and reason; backend enriches facts.";
    }

    @Override
    public Map<String, Object> propsSchema() {
        Map<String, Object> properties = Map.of(
            "courseId", Map.of("type", "string"),
            "courseTitle", Map.of("type", "string"),
            "courseSummary", Map.of("type", "string")
        );
        return Map.of(
            "type", "object",
            "properties", properties,
            "required", List.of("courseId"),
            "additionalProperties", false
        );
    }

}

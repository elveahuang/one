package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    public Map<String, Object> propsSchema() {
        Map<String, Object> properties = Map.of(
            "instructorId", Map.of("type", "string"),
            "instructorName", Map.of("type", "string"),
            "instructorDetails", Map.of("type", "string")
        );

        return Map.of(
            "type", "object",
            "properties", properties,
            "required", List.of("instructorId"),
            "additionalProperties", false
        );
    }

}

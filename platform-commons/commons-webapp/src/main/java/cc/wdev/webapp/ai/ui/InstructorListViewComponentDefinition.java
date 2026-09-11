package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentDefinition;
import cc.wdev.platform.commons.ai.ui.UiSchemaUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Component
public class InstructorListViewComponentDefinition implements UiComponentDefinition {

    @Override
    public String type() {
        return "instructor-list-view";
    }

    @Override
    public String description() {
        return "Instructor list view. AI should provide items with instructorId and instructorName.";
    }

    @Override
    public Map<String, Object> propsSchema() {
        return UiSchemaUtils.of(Props.class);
    }

    public record Props(
        @Schema(description = "讲师列表，条目必须来自 searchInstructor 工具结果，禁止编造")
        List<Item> items) {
    }

    public record Item(
        @Schema(description = "讲师 ID")
        String instructorId,
        @Schema(description = "讲师姓名")
        String instructorName) {
    }

}

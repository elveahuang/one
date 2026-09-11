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
public class CourseListViewComponentDefinition implements UiComponentDefinition {

    @Override
    public String type() {
        return "course-list-view";
    }

    @Override
    public String description() {
        return "Course list view. AI should provide items with courseId and courseTitle; backend enriches facts.";
    }

    @Override
    public Map<String, Object> propsSchema() {
        return UiSchemaUtils.of(Props.class);
    }

    public record Props(
        @Schema(description = "课程列表，条目必须来自 searchCourse 工具结果，禁止编造")
        List<Item> items) {
    }

    public record Item(
        @Schema(description = "课程 ID")
        String courseId,
        @Schema(description = "课程名称")
        String courseTitle) {
    }

}

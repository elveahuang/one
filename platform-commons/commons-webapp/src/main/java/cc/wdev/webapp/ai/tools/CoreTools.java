package cc.wdev.webapp.ai.tools;

import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.CourseElasticEntity;
import cc.wdev.webapp.es.domain.entity.InstructorElasticEntity;
import cc.wdev.webapp.es.service.CourseElasticService;
import cc.wdev.webapp.es.service.InstructorElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
@Component
@RequiredArgsConstructor
public class CoreTools {

    private final CourseElasticService courseElasticService;

    private final InstructorElasticService instructorElasticService;

    @Tool(name = "searchCourse", description = """
        搜索课程
        """)
    public List<CourseElasticEntity> searchCourse(@ToolParam(description = "关键字") String keyword) {
        PageRequest request = PageRequest.builder().page(1).size(10).q(keyword).build();
        Page<CourseElasticEntity> page = courseElasticService.search(request);
        return page.isEmpty() ? emptyList() : page.getContent().stream().toList();
    }

    @Tool(name = "searchInstructor", description = """
        搜索讲师
        """)
    public List<InstructorElasticEntity> searchInstructor(@ToolParam(description = "关键字") String keyword) {
        PageRequest request = PageRequest.builder().page(1).size(10).q(keyword).build();
        Page<InstructorElasticEntity> page = instructorElasticService.search(request);
        return page.isEmpty() ? emptyList() : page.getContent().stream().toList();
    }

}

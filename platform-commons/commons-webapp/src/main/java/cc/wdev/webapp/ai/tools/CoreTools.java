package cc.wdev.webapp.ai.tools;

import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.CourseElasticEntity;
import cc.wdev.webapp.es.service.CourseElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
@Component
@RequiredArgsConstructor
public class CoreTools {

    private final CourseElasticService courseElasticService;

    @Tool(name = "searchCourse", description = """
        搜索课程
        """)
    public List<CourseElasticEntity> searchCourse(@ToolParam(description = "关键字") String keyword) {
        PageRequest request = PageRequest.builder().page(1).size(10).q(keyword).build();
        Page<CourseElasticEntity> page = courseElasticService.search(request);
        return page.isEmpty() ? emptyList() : page.getContent().stream().toList();
    }

    @Tool(name = "getBooks", description = "获取推荐书籍")
    List<String> getBooks() {
        List<String> books = new ArrayList<>();
        books.add("三国演义");
        books.add("高等数学");
        books.add("语言榆树");
        return books;
    }

}

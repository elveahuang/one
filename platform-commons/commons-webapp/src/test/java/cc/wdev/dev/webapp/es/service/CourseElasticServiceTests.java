package cc.wdev.dev.webapp.es.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.core.sequence.SequenceManager;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.CourseElasticEntity;
import cc.wdev.webapp.es.service.CourseElasticService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public class CourseElasticServiceTests extends BaseTests {

    @Autowired
    CourseElasticService courseElasticService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.courseElasticService);

        this.courseElasticService.deleteAll();

        List<String> books = List.of("三国演义", "高等数学", "西方政治经济学");
        for (String book : books) {
            CourseElasticEntity entity = CourseElasticEntity.builder()
                .title(book)
                .details(book)
                .requirement(book)
                .build();
            entity.setId(SequenceManager.getSequence().nextId());
            this.courseElasticService.save(entity);
        }

        PageRequest request = PageRequest.builder().page(1).size(10).build();
        request.setQ("三");
        Page<CourseElasticEntity> page = this.courseElasticService.search(request);
        Assertions.assertNotNull(page);
    }

}

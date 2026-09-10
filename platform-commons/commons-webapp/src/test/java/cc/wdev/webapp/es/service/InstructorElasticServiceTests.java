package cc.wdev.webapp.es.service;

import cc.wdev.platform.commons.core.sequence.SequenceManager;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.BaseTests;
import cc.wdev.webapp.es.domain.entity.InstructorElasticEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public class InstructorElasticServiceTests extends BaseTests {

    @Autowired
    InstructorElasticService instructorElasticService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.instructorElasticService);

        this.instructorElasticService.deleteAll();

        List<String> items = List.of("张三", "李四", "王五");
        for (String item : items) {
            InstructorElasticEntity entity = InstructorElasticEntity.builder()
                .name(item)
                .details(item)
                .build();
            entity.setId(SequenceManager.getSequence().nextId());
            this.instructorElasticService.save(entity);
        }

        PageRequest request = PageRequest.builder().page(1).size(10).build();
        request.setQ("三");
        Page<InstructorElasticEntity> page = this.instructorElasticService.search(request);
        Assertions.assertNotNull(page);
    }

}

package cc.wdev.webapp.es.service.impl;

import cc.wdev.platform.commons.data.elasticsearch.service.BaseEntityService;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.InstructorElasticEntity;
import cc.wdev.webapp.es.repository.CourseInstructorRepository;
import cc.wdev.webapp.es.service.CourseElasticService;
import cc.wdev.webapp.es.service.InstructorElasticService;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class InstructorElasticServiceImpl extends BaseEntityService<InstructorElasticEntity, Long, CourseInstructorRepository>
    implements InstructorElasticService {

    /**
     * @see CourseElasticService#search(PageRequest)
     */
    @Override
    public Page<InstructorElasticEntity> search(PageRequest request) {
        NativeQueryBuilder builder = NativeQuery.builder();

        // 查询条件构造
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        String q = request.getQ();
        if (StringUtils.isNotEmpty(q)) {
            Query multiMatchQuery = MultiMatchQuery.of(mmq -> mmq
                .fields("title", "details")
                .query(q)
            )._toQuery();
            boolQueryBuilder.must(multiMatchQuery);
        }

        // 查询
        builder.withQuery(boolQueryBuilder.build()._toQuery());
        builder.withPageable(request.getPageable());

        SearchHits<InstructorElasticEntity> hits = this.getTemplate().search(builder.build(), InstructorElasticEntity.class);
        SearchPage<InstructorElasticEntity> page = SearchHitSupport.searchPageFor(hits, request.getPageable());
        return new PageImpl<>(page.getContent().stream().map(SearchHit::getContent).collect(Collectors.toList()),
            request.getPageable(),
            page.getTotalElements()
        );
    }

}

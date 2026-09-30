package cc.wdev.webapp.es.domain.entity;

import cc.wdev.platform.commons.data.elasticsearch.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@Document(indexName = "wdev_intructor")
public class InstructorElasticEntity extends BaseEntity {
    /**
     * 讲师名称
     */
    @Field(type = FieldType.Text, fielddata = true, analyzer = "ik_max_word")
    private String name;
    /**
     * 讲师介绍
     */
    private String details;
}

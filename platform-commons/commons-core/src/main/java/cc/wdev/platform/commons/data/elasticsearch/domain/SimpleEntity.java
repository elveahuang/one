package cc.wdev.platform.commons.data.elasticsearch.domain;

import cc.wdev.platform.commons.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
public abstract class SimpleEntity extends AbstractEntity {
    /**
     * 启用状态
     */
    @Field(type = FieldType.Integer)
    private Integer active;
    /**
     * 创建人
     */
    @Field(type = FieldType.Keyword, ignoreAbove = 256)
    private String createdBy;

    public void setCreatedBy(Long createdBy) {
        this.createdBy = NumberUtils.toString(createdBy);
    }
}

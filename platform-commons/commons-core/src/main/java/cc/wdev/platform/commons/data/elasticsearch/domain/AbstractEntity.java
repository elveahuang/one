package cc.wdev.platform.commons.data.elasticsearch.domain;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.utils.NumberUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author elvea
 */
public abstract class AbstractEntity implements IdEntity {

    /**
     * 租户ID
     */
    @Field(type = FieldType.Keyword, ignoreAbove = 256)
    protected String id;

    @Override
    public Long getId() {
        return NumberUtils.toLong(this.id);
    }

    @Override
    public void setId(Long id) {
        this.id = NumberUtils.toString(id);
    }

    @Override
    public String getIdAsString() {
        return this.id;
    }

}

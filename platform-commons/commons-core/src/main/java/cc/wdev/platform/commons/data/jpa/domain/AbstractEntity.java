package cc.wdev.platform.commons.data.jpa.domain;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.jpa.id.Sequence;
import cc.wdev.platform.commons.utils.NumberUtils;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 * @see IdEntity
 */
@MappedSuperclass
public abstract class AbstractEntity implements IdEntity {

    @Id
    @Sequence
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getIdAsString() {
        return NumberUtils.toString(this.id);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}

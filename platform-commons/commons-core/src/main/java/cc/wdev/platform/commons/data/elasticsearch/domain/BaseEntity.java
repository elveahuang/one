package cc.wdev.platform.commons.data.elasticsearch.domain;

import cc.wdev.platform.commons.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Getter
@Setter
public abstract class BaseEntity extends AbstractEntity {

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
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;
    /**
     * 最后修改人
     */
    @Field(type = FieldType.Keyword, ignoreAbove = 256)
    private String updatedBy;
    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;
    /**
     * 删除人
     */
    @Field(type = FieldType.Keyword, ignoreAbove = 256)
    private String deletedBy;
    /**
     * 删除时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime deletedAt;

    public void setCreatedBy(Long createdBy) {
        this.createdBy = NumberUtils.toString(createdBy);
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = NumberUtils.toString(updatedBy);
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = NumberUtils.toString(deletedBy);
    }
}

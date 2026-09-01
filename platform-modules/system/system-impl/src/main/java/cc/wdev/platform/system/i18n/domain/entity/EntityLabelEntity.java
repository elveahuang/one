package cc.wdev.platform.system.i18n.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_entity_label")
public class EntityLabelEntity extends BaseEntity {
    /**
     * 实体类名
     */
    private String className;
    /**
     * 实体属性名
     */
    private String propertyName;
    /**
     * 简体中文
     */
    private String zhLabel;
    /**
     * 繁体中文
     */
    private String zhTwLabel;
    /**
     * 英语
     */
    private String enLabel;
    /**
     * 法语
     */
    private String frLabel;
    /**
     * 日语
     */
    private String jaLabel;
}

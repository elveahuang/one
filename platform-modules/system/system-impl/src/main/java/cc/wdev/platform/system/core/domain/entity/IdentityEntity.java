package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_identity")
public class IdentityEntity extends BaseEntity {
    /**
     * 用户标识（uuid 与 jsqlparser 5.4 关键字冲突，用带引号标识符；列型 varchar 与实体 Long 的不匹配由 DB 隐式转换承接）
     */
    @TableField("\"uuid\"")
    private Long uuid;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 来源
     */
    private Integer source;
}

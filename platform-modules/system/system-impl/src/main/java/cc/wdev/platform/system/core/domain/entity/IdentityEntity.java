package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
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
     * 用户标识
     */
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

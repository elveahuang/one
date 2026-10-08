package cc.wdev.platform.system.dict.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
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
@TableName("sys_dict_sequence")
public class DictSequenceEntity extends BaseTenantEntity {
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 字典ID
     */
    private Long dictId;
    /**
     * 序号
     */
    private Integer idx;
}

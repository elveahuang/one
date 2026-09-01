package cc.wdev.platform.system.ai.domain.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 智能体关联表
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRelationVo implements Serializable {

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 名称
     */
    private String name;
}

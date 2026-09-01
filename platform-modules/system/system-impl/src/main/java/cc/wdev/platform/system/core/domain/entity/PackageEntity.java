package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_package")
public class PackageEntity extends BaseEntity implements CodeEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 编号
     */
    private String code;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String label;
    /**
     * 特权
     */
    private String privilege;
    /**
     * 是否默认
     */
    private Integer defaultInd;
    /**
     * 是否允许试用
     */
    private Integer trialInd;
    /**
     * 试用时长，单位是自然天
     */
    private Integer trialLimit;
    /**
     * 会员等级，等级越高显示优先级越高
     */
    private Integer level;
    /**
     * 序号
     */
    private Integer idx;
    /**
     * 备注
     */
    private String description;
    /**
     * 来源
     */
    private Integer source;
}

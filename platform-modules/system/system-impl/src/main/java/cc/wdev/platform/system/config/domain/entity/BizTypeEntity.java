package cc.wdev.platform.system.config.domain.entity;

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
@TableName("sys_biz_type")
public class BizTypeEntity extends BaseEntity {
    /**
     * 业务分组类型
     */
    private String bizGroupType;
    /**
     * 业务范围类型
     */
    private String bizScopeType;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 附加信息
     */
    private String extra;
    /**
     * 附加信息
     */
    private String defaultConfig;
    /**
     * 附加信息
     */
    private String customConfig;
    /**
     * 名称
     */
    private String title;
    /**
     * 多语言文本
     */
    private String labelKey;
    /**
     * 多语言文本分组
     */
    private String labelGroup;
    /**
     * 备注
     */
    private String description;
    /**
     * 序号
     */
    private Integer idx;
    /**
     * 来源
     */
    private Integer source;
    /**
     * 状态
     */
    private Integer status;
}

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
@TableName("sys_label")
public class LabelEntity extends BaseEntity {
    /**
     * 分组
     */
    private String labelGroupType;
    /**
     * 标识
     */
    private String code;
    /**
     * 简体中文
     */
    private String zhCnLabel;
    private Integer zhCnStaticInd;
    /**
     * 繁体中文
     */
    private String zhTwLabel;
    private Integer zhTwStaticInd;
    /**
     * 英语
     */
    private String enLabel;
    private Integer enStaticInd;
    /**
     * 法语
     */
    private String frLabel;
    private Integer frStaticInd;
    /**
     * 日语
     */
    private String jaLabel;
    private Integer jaStaticInd;
    /**
     * 韩语
     */
    private String krLabel;
    private Integer krStaticInd;
    /**
     * 越南语
     */
    private String viLabel;
    private Integer viStaticInd;
}

package cc.wdev.platform.system.i18n.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import lombok.*;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabelExcelEntity extends BaseEntity {
    /**
     * 分组
     */
    @ExcelProperty(value = "分组", index = 0)
    private String group;
    /**
     * 标识
     */
    @ExcelProperty(value = "标识", index = 1)
    private String code;
    /**
     * 简体中文
     */
    @ExcelProperty(value = "简体中文", index = 2)
    private String zhLabel;
    /**
     * 繁体中文
     */
    @ExcelProperty(value = "繁体中文", index = 3)
    private String zhTwLabel;
    /**
     * 英语
     */
    @ExcelProperty(value = "英语", index = 4)
    private String enLabel;
    /**
     * 法语
     */
    @ExcelProperty(value = "法语", index = 5)
    private String frLabel;
    /**
     * 日语
     */
    @ExcelProperty(value = "日语", index = 6)
    private String jaLabel;
    /**
     * 韩语
     */
    @ExcelProperty(value = "韩语", index = 7)
    private String krLabel;
}

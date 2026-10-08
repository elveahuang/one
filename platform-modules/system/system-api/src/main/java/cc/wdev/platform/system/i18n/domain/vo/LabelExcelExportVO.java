package cc.wdev.platform.system.i18n.domain.vo;

import lombok.*;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LabelExcelExportVO implements Serializable {
    /**
     * 分组
     */
    @ExcelProperty(value = "分组", index = 0)
    private String labelGroupType;
    /**
     * 标识
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "标识", index = 1)
    private String code;
    /**
     * 简体中文
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"简体中文", "文本"}, index = 2)
    private String zhCnLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"简体中文", "静态文本"}, index = 3)
    private String zhCnStaticInd;
    /**
     * 繁体中文
     */
    @ExcelProperty(value = {"繁体中文", "文本"}, index = 4)
    private String zhTwLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"繁体中文", "静态文本"}, index = 5)
    private String zhTwStaticInd;
    /**
     * 英语
     */
    @ExcelProperty(value = {"英语", "文本"}, index = 6)
    private String enLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"英语", "静态文本"}, index = 7)
    private String enStaticInd;
    /**
     * 法语
     */
    @ExcelProperty(value = {"法语", "文本"}, index = 8)
    private String frLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"法语", "静态文本"}, index = 9)
    private String frStaticInd;
    /**
     * 日语
     */
    @ExcelProperty(value = {"日语", "文本"}, index = 10)
    private String jaLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"日语", "静态文本"}, index = 11)
    private String jaStaticInd;
    /**
     * 韩语
     */
    @ExcelProperty(value = {"韩语", "文本"}, index = 12)
    private String krLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"韩语", "静态文本"}, index = 13)
    private String krStaticInd;
    /**
     * 越南语
     */
    @ExcelProperty(value = {"越南语", "文本"}, index = 14)
    private String viLabel;
    @ColumnWidth(15)
    @ExcelProperty(value = {"越南语", "静态文本"}, index = 15)
    private String viStaticInd;
}

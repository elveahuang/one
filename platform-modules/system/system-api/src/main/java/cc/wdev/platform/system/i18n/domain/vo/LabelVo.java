package cc.wdev.platform.system.i18n.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.LangTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "多语言VO")
public class LabelVo implements Serializable {
    /**
     * Id
     */
    @Schema(description = "Id")
    private Long id;
    /**
     * 分组
     */
    @Schema(description = "分组")
    private String labelGroupType;
    /**
     * 标识
     */
    @Schema(description = "标识")
    private String code;
    /**
     * 简体中文
     */
    @Schema(description = "简体中文")
    private String zhCnLabel;
    @Schema(description = "简体中文静态标志")
    private Integer zhCnStaticInd;
    /**
     * 繁体中文
     */
    @Schema(description = "繁体中文")
    private String zhTwLabel;
    @Schema(description = "繁体中文静态标志")
    private Integer zhTwStaticInd;
    /**
     * 英语
     */
    @Schema(description = "英语")
    private String enLabel;
    @Schema(description = "英语静态标志")
    private Integer enStaticInd;
    /**
     * 法语
     */
    @Schema(description = "法语")
    private String frLabel;
    @Schema(description = "法语静态标志")
    private Integer frStaticInd;
    /**
     * 日语
     */
    @Schema(description = "日语")
    private String jaLabel;
    @Schema(description = "日语静态标志")
    private Integer jaStaticInd;
    /**
     * 韩语
     */
    @Schema(description = "韩语")
    private String krLabel;
    @Schema(description = "韩语静态标志")
    private Integer krStaticInd;
    /**
     * 越南语
     */
    @Schema(description = "越南语")
    private String viLabel;
    @Schema(description = "越南语静态标志")
    private Integer viStaticInd;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    /**
     * 选中翻译来源文本
     */
    @Schema(description = "选中翻译来源文本")
    private String selectLabel;

    /**
     * 来源文本设置
     */
    public void setSourceLang() {
        if (null != this.getZhCnStaticInd() && this.getZhCnStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.ZH_CN.getValue().toLowerCase());
        }
        if (null != this.getZhTwStaticInd() && this.getZhTwStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.ZH_TW.getValue().toLowerCase());
        }
        if (null != this.getEnStaticInd() && this.getEnStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.EN.getValue().toLowerCase());
        }
        if (null != this.getFrStaticInd() && this.getFrStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.FR.getValue().toLowerCase());
        }
        if (null != this.getJaStaticInd() && this.getJaStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.JA.getValue().toLowerCase());
        }
        if (null != this.getFrStaticInd() && this.getFrStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.FR.getValue().toLowerCase());
        }
        if (null != this.getViStaticInd() && this.getViStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
            this.setSelectLabel(LangTypeEnum.VI.getValue().toLowerCase());
        }
    }
}

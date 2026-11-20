package cc.wdev.platform.commons.ai.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AI向用户提问交互VO
 *
 * @author dfox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI向用户提问交互VO")
public class AiQuestionVo implements Serializable {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String conversationId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 问题列表
     */
    @Schema(description = "问题列表")
    private List<QuestionItemVo> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "问题明细")
    public static class QuestionItemVo implements Serializable {
        /**
         * 问题描述
         */
        @Schema(description = "问题描述")
        private String question;

        /**
         * 标签/标题
         */
        @Schema(description = "标签/标题")
        private String header;

        /**
         * 是否多选
         */
        @Schema(description = "是否多选")
        private Boolean multiSelect;

        /**
         * 选项列表
         */
        @Schema(description = "选项列表")
        private List<OptionItemVo> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "选项明细")
    public static class OptionItemVo implements Serializable {
        /**
         * 选项标签
         */
        @Schema(description = "选项标签")
        private String label;

        /**
         * 选项描述
         */
        @Schema(description = "选项描述")
        private String description;
    }

}

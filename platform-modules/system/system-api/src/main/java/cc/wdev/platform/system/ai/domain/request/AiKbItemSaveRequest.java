package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "知识条目保存请求", description = "知识条目保存请求")
public class AiKbItemSaveRequest implements Serializable {

    /**
     * 知识条目ID
     */
    @Schema(title = "知识条目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID", description = "知识库ID")
    private Long kbId;

    /**
     * 知识库编号
     */
    @Schema(title = "知识库编号", description = "知识库编号")
    private String kbCode;

    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(title = "知识库ID", description = "知识库ID")
    private Long bizId;

    /**
     * 类型
     */
    @Schema(title = "类型", description = "类型")
    private String type;

    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    private String title;

    /**
     * 内容
     */
    @Schema(title = "内容", description = "内容")
    private String content;

    /**
     * 问题
     */
    @Schema(title = "内容", description = "内容")
    private String question;

    /**
     * 答案
     */
    @Schema(title = "答案", description = "答案")
    private String answer;

    /**
     * 元数据
     */
    @Schema(title = "元数据", description = "元数据")
    private Map<String, Object> metadata;

}

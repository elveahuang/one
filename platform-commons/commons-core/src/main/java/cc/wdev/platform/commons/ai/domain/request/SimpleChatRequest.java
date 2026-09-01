package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleChatRequest extends Request {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 智能体ID
     */
    @Schema(description = "智能体ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long agentId;
    /**
     * 智能体标识
     */
    @Schema(description = "智能体标识")
    private String agentCode;
    /**
     * 模型ID
     */
    @Schema(description = "模型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelId;
    /**
     * 模型标识
     */
    @Schema(description = "模型标识")
    private String modelCode;
    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;
    /**
     * 知识库标识
     */
    @Schema(description = "知识库标识")
    private String kbCode;
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String conversationId;
    /**
     * 对话类型
     */
    @Schema(title = "对话类型")
    private String chatType;
    /**
     * 响应类型
     */
    @Schema(title = "响应类型")
    private String responseType;
    /**
     * 用户提示词
     */
    @Schema(description = "用户提示词")
    private String prompt;
    /**
     * 智能体工具名称
     */
    @Schema(description = "智能体工具名称")
    private String systemPrompt;
    /**
     * 系统提示词
     */
    @Schema(description = "系统提示词")
    private List<String> toolNames;
    /**
     * 附加参数
     */
    @Builder.Default
    @Schema(description = "附加参数")
    private Map<String, Object> params = Maps.newHashMap();
    /**
     *
     */
    @Schema(description = "附加参数")
    private Float temperature;
    /**
     * 是否支持工具调用
     */
    @Builder.Default
    @Schema(description = "是否支持工具调用")
    private Boolean withToolCalling = Boolean.FALSE;
    /**
     * 是否支持会话
     */
    @Builder.Default
    @Schema(description = "是否支持会话")
    private Boolean withSession = Boolean.FALSE;
    /**
     * 是否支持知识检索
     */
    @Builder.Default
    @Schema(description = "是否支持知识检索")
    private Boolean withRag = Boolean.FALSE;
}

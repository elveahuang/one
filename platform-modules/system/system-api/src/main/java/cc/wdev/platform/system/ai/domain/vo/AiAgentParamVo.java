package cc.wdev.platform.system.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能体参数")
public class AiAgentParamVo implements Serializable {

    @Schema(title = "可用模型列表", description = "可用模型列表")
    private List<AiModelSimpleVo> aiModels;

    @Schema(title = "可用工具列表", description = "可用工具列表")
    private List<AiToolSimpleVo> aiTools;

}

package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "模型参数")
public class AiModelOptionsVo implements Serializable {

    @Schema(title = "模型提供商列表", description = "模型提供商列表")
    private List<SimpleOptionVo> modelProviders;

    @Schema(title = "服务提供商列表", description = "服务提供商列表")
    private List<SimpleOptionVo> modelServiceProviders;

    @Schema(title = "模型类型列表", description = "模型类型列表")
    private List<SimpleOptionVo> modelTypes;

}

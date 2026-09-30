package cc.wdev.platform.system.commons.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关联VO")
public class RelationVo<T> implements Serializable {
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 关联业务类型
     */
    @Schema(title = "关联业务类型", description = "关联业务类型")
    private String relationBizType;
    /**
     * ID集合
     */
    @Schema(title = "ID集合", description = "ID集合")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Long[] ids;
    /**
     * 集合
     */
    @Schema(title = "items集合", description = "items集合")
    private List<T> items;
}

package cc.wdev.platform.system.dict.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class DictEntity extends BaseTenantEntity implements CodeEntity {
    /**
     * 参考Id
     */
    @Schema(title = "参考ID", description = "参考ID")
    private Long referenceId;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;
    /**
     * 序号
     */
    @Schema(title = "序号", description = "序号")
    private Integer idx;
    /**
     * 附加信息
     */
    @Schema(title = "附加信息", description = "附加信息")
    private String extra;
    /**
     * 数据范围
     */
    @Schema(title = "数据范围", description = "数据范围")
    private Integer scope;
    /**
     * 文本
     */
    @Schema(title = "文本", description = "文本")
    private String title;
    /**
     * 来源
     */
    @Schema(title = "来源", description = "来源")
    private Integer source;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;
}

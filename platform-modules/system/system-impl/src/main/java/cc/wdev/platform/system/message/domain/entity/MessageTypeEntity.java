package cc.wdev.platform.system.message.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.core.domain.TitleEntity;
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
@TableName("sys_message_type")
@Schema(description = "消息类型")
public class MessageTypeEntity extends BaseTenantEntity implements CodeEntity, TitleEntity {
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String label;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private Integer status;
}

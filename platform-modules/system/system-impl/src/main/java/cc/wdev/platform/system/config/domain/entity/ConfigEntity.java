package cc.wdev.platform.system.config.domain.entity;

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
@TableName("sys_config")
@Schema(description = "配置实体")
public class ConfigEntity extends BaseTenantEntity {
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 多语言文本
     */
    @Schema(description = "多语言文本")
    private String label;
    /**
     * 分组
     */
    @Schema(description = "分组")
    private String configGroupType;
    /**
     * 内容类型
     */
    @Schema(description = "内容类型")
    private String configContentType;
    /**
     * 参数名
     */
    @Schema(description = "参数名")
    private String configKey;
    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String configValue;
    /**
     * 默认值
     */
    @Schema(description = "默认值")
    private String defaultValue;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 帮助信息
     */
    @Schema(description = "帮助信息")
    private String help;
    /**
     * 数据来源
     */
    @Schema(description = "数据来源")
    private Long source;
}

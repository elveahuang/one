package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_ai_model")
@EqualsAndHashCode(callSuper = true)
public class AiModelEntity extends BaseTenantEntity implements CodeEntity {
    /**
     * 编号
     */
    private String code;
    /**
     * 标题
     */
    private String title;
    /**
     * 服务提供商
     */
    private String serviceProvider;
    /**
     * 模型提供商
     */
    private String modelProvider;
    /**
     * 模型类型
     */
    private String modelType;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * API密钥
     */
    private String apiKey;
    /**
     * 基础URL
     */
    private String baseUrl;
    /**
     * 参数配置
     */
    private String variables;
    /**
     * 备注说明
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
}

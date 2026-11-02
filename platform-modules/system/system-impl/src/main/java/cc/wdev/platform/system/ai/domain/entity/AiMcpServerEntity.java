package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MCP Server
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_ai_mcp_server")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiMcpServerEntity extends BaseTenantEntity {

    /**
     * 编号
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 服务地址
     */
    private String url;

    /**
     * 请求头（JSON 对象，如 {"Authorization":"Bearer xxx"}），HTTP / SSE / Streamable 传输使用
     */
    private String headers;

    /**
     * 启动命令，仅 stdio 传输使用
     */
    private String command;

    /**
     * 命令参数（JSON 数组），仅 stdio 传输使用
     */
    private String args;

    /**
     * 环境变量（JSON 对象），仅 stdio 传输使用
     */
    private String env;

    /**
     * 备注说明
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;
}

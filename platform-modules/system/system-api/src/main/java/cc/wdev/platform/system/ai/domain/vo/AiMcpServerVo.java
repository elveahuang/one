package cc.wdev.platform.system.ai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiMcpServerVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
     * 环境变量
     */
    private String headers;

    /**
     * 参数
     */
    private String args;

    /**
     * 备注说明
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;
}

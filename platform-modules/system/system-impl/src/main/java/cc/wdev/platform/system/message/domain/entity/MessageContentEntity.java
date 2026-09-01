package cc.wdev.platform.system.message.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_content")
public class MessageContentEntity extends BaseTenantEntity {
    /**
     * 消息通道
     */
    private String messageChannel;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 消息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 响应内容
     */
    private String resp;
    /**
     * 异常内容
     */
    private String exception;
    /**
     * 发送时间
     */
    private LocalDateTime sentDatetime;
    /**
     * 发送状态
     */
    private Integer status;
}

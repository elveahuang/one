package cc.wdev.platform.system.im.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_chat_message")
public class ChatMessageEntity extends BaseTenantEntity {
    /**
     * 会话ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 业务ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 发送人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderUserId;
    /**
     * 发送人类型
     */
    private String senderUserType;
    /**
     * 接收人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverUserId;
    /**
     * 接收人类型
     */
    private String receiverUserType;
    /**
     * 消息类型
     */
    private String messageContentType;
    /**
     * 消息序号
     */
    private Integer sequence;
    /**
     * 状态
     */
    private Integer status;
}

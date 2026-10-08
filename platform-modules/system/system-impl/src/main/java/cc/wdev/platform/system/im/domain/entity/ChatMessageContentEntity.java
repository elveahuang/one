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
@TableName("sys_chat_message_content")
public class ChatMessageContentEntity extends BaseTenantEntity {
    /**
     * 会话ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 消息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatMessageId;
    /**
     * 内容
     */
    private String content;
    /**
     * 附加信息
     */
    private String extra;
    /**
     * 状态
     */
    private Integer status;
}

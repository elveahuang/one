package cc.wdev.platform.system.im.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_chat_entity_message")
public class ChatEntityMessageEntity extends BaseTenantEntity {
    /**
     * 互动会话ID
     */
    private Long chatSessionId;
    /**
     * 互动消息ID
     */
    private Long chatMessageId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 是否已读
     */
    private Integer readInd;
    /**
     * 是否已删
     */
    private Integer deleteInd;
    /**
     * 状态
     */
    private Integer status;
}

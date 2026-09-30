package cc.wdev.platform.system.message.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_template")
public class MessageTemplateEntity extends BaseTenantEntity {
    /**
     * 消息通道
     */
    private String messageChannel;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 模版内容
     */
    private String content;
    /**
     * 发布状态
     */
    private Integer status;
}

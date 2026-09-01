package cc.wdev.platform.system.im.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "聊天消息VO")
public class ChatEntitySessionVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 互动会话ID
     */
    private Long chatSessionId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 最后消息ID
     */
    private Long lastReadMessageId;
    /**
     * 最后读取时间
     */
    private LocalDateTime lastReadTime;
    /**
     * 最后一次清除会话沟通的时间
     */
    private LocalDateTime clearAt;
    /**
     * 是否置顶
     */
    private Integer topInd;
    /**
     * 是否收藏
     */
    private Integer collectInd;
}

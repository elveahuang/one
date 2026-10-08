package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "沟通消息记录查询对象")
public class ChatSessionMessageCountRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID数组
     */
    @Schema(description = "业务ID数组")
    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> bizIds;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 最后消息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastReadMessageId;
    /**
     * 最近一次清除会话沟通的时间
     */
    private LocalDateTime clearAt;
    /**
     * 是否包括未加入会话的消息数量
     */
    @Builder.Default
    private Boolean withoutEntitySession = Boolean.TRUE;
}

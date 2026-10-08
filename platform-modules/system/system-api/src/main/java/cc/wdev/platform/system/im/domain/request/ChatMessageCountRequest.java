package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class ChatMessageCountRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> chatSessionIds;
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
     * 是否是招聘管理员
     */
    private Boolean isManager;
}

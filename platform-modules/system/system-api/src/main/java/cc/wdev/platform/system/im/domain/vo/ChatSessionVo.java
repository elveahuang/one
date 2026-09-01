package cc.wdev.platform.system.im.domain.vo;

import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "聊天室VO")
public class ChatSessionVo implements Serializable {
    /**
     * ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * chatEntitySessionId
     */
    @Schema(description = "chatEntitySessionId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatEntitySessionId;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 目标用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetUserId;
    /**
     * 最近一条已读消息的ID
     */
    @Schema(description = "最近一条已读消息的ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastReadMessageId;
    /**
     * 最近一条消息
     */
    @Schema(description = "最近一条消息")
    private ChatMessageVo lastMessage;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Integer topInd;

    /**
     * 是否收藏
     */
    @Schema(description = "是否收藏")
    private Integer collectInd;

    /**
     * 是否不合适
     */
    @Schema(description = "是否不合适")
    private Integer unsuitableInd;

    @Schema(description = "标签")
    private RelationVo<?> tag;

    @Schema(description = "业务对象")
    private Object bizObj;
}

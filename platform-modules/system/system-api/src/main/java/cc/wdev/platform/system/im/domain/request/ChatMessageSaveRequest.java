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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "创建文本消息请求对象")
public class ChatMessageSaveRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 内容ID
     */
    @Schema(title = "内容ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 业务ID
     */
    @Schema(title = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 内容ID
     */
    @Schema(title = "内容ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String type;
    /**
     * 内容
     */
    @Schema(title = "内容")
    private String content;
    /**
     * 附加信息
     */
    @Schema(title = "附加信息")
    private String extra;
    /**
     * 发送人ID
     */
    @Schema(title = "发送人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderUserId;
    /**
     * 发送人用户类型
     */
    @Schema(title = "发送人用户类型")
    private String senderUserType;
    /**
     * 接收人ID
     */
    @Schema(title = "接收人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverUserId;
    /**
     * 接收人用户类型
     */
    @Schema(title = "接收人用户类型")
    private String receiverUserType;
}

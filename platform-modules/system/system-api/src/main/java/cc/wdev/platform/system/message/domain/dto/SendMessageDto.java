package cc.wdev.platform.system.message.domain.dto;

import com.google.common.collect.Maps;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class SendMessageDto implements Serializable {

    private Long id;

    private Long tenantId;

    private Long contentId;

    private String subject;

    private String url;

    private String content;

    @Builder.Default
    private Map<String, Object> params = Maps.newHashMap();

    private MessageSenderDto sender;

    private List<MessageRecipientDto> recipients;

}

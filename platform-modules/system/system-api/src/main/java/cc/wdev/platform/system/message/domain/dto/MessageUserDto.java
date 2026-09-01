package cc.wdev.platform.system.message.domain.dto;

import cc.wdev.platform.system.message.enums.MessageUserTypeEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class MessageUserDto implements Serializable {

    private MessageUserTypeEnum type;

    private Long userId;

    private String username;

    private String email;

    private String mobileCountryCode;

    private String mobileNumber;

    public MessageUserDto(MessageUserTypeEnum type, Long userId) {
        this.type = type;
        this.userId = userId;
    }

    public MessageUserDto(MessageUserTypeEnum type, String email) {
        this.type = type;
        this.userId = 0L;
        this.email = email;
    }

    public MessageUserDto(MessageUserTypeEnum type, String mobileCountryCode, String mobileNumber) {
        this.type = type;
        this.userId = 0L;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNumber = mobileNumber;
    }

}

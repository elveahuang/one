package cc.wdev.platform.system.site.domain.dto;

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
public class LinkDto implements Serializable {
    private String larkUrl;
    private String larkWebappUrl;
    private String larkMobileUrl;

    private String dingtalkUrl;
    private String dingtalkWebappUrl;
    private String dingtalkMobileUrl;

    private String wecomUrl;
    private String wecomWebappUrl;
    private String wecomMobileUrl;
}

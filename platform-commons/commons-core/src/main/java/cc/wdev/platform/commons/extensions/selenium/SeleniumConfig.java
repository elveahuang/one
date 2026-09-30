package cc.wdev.platform.commons.extensions.selenium;

import cc.wdev.platform.commons.enums.SeleniumDriverTypeEnum;
import cc.wdev.platform.commons.extensions.http.HttpProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeleniumConfig implements Serializable {

    @Builder.Default
    private SeleniumDriverTypeEnum driverType = SeleniumDriverTypeEnum.CHROME;

    private String driverVersion;

    @Builder.Default
    private HttpProxy proxy = HttpProxy.builder().build();

}

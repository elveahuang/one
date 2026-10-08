package cc.wdev.platform.commons.extensions.ip.geoip2;

import cc.wdev.platform.commons.extensions.ip.LocationEnum;
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
public class GeoLiteConfig implements Serializable {

    @Builder.Default
    private LocationEnum location = LocationEnum.FILE_SYSTEM;

    @Builder.Default
    private String path = "";

}

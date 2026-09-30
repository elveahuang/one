package cc.wdev.platform.commons.extensions.ip.ip2region;

import cc.wdev.platform.commons.extensions.ip.LocationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ip2RegionConfig {

    @Builder.Default
    private LocationEnum location = LocationEnum.FILE_SYSTEM;

    @Builder.Default
    private String path = "";

}

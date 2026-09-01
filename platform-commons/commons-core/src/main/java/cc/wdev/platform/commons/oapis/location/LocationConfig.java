package cc.wdev.platform.commons.oapis.location;

import cc.wdev.platform.commons.oapis.location.enums.LocationTypeEnum;
import cc.wdev.platform.commons.oapis.location.tianditu.TiandituConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationConfig implements Serializable {
    @Builder.Default
    private LocationTypeEnum type = LocationTypeEnum.Tianditu;

    @Builder.Default
    private TiandituConfig tianditu = TiandituConfig.builder().build();
}

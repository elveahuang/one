package cc.wdev.platform.commons.core.log.config;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
public class LogConfig implements Serializable {

    @Setter
    @Getter
    @Builder.Default
    private LogConfig.Url url = LogConfig.Url.builder().build();

    @Data
    @Builder
    public static class Url implements Serializable {

        @Builder.Default
        private List<String> excludes = Lists.newArrayList();

    }

}

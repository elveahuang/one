package cc.wdev.platform.commons.extensions.ip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ip implements Serializable {

    private String ip;

    private String details;

    private Country country;

    private City city;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Country implements Serializable {
        private String code;
        private String name;
        private Map<String, String> label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class City implements Serializable {
        private String name;
        private Map<String, String> label;
    }

}

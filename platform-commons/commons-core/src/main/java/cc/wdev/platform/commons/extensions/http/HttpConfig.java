package cc.wdev.platform.commons.extensions.http;

import cc.wdev.platform.commons.extensions.http.enums.HttpClientTypeEnum;
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
public class HttpConfig implements Serializable {

    @Builder.Default
    HttpDebug debug = HttpDebug.builder().build();

    @Builder.Default
    HttpClientTypeEnum type = HttpClientTypeEnum.OKHTTP;

    @Builder.Default
    HttpProxy proxy = HttpProxy.builder().build();

}

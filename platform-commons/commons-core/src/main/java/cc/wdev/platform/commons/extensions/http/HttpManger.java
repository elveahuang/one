package cc.wdev.platform.commons.extensions.http;

import cc.wdev.platform.commons.extensions.http.enums.HttpClientTypeEnum;
import lombok.Getter;

/**
 * @author elvea
 */
public class HttpManger {

    @Getter
    private static volatile HttpFactory http = HttpFactory.builder().type(HttpClientTypeEnum.OKHTTP).build();

    public static void setHttp(HttpFactory http) {
        HttpManger.http = http;
    }

}

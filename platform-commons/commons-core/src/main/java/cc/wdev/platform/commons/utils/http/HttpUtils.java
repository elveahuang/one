package cc.wdev.platform.commons.utils.http;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import okhttp3.MediaType;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author elvea
 */
public abstract class HttpUtils {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int CONNECT_TIMEOUT = 3 * 60 * 1000;
    public static final int READ_TIMEOUT = 3 * 60 * 1000;
    public static final int WRITE_TIMEOUT = 3 * 60 * 1000;

    public static String handleQueryParam(String uri, Map<String, String> paramMap) {
        if (MapUtils.isNotEmpty(paramMap) && StringUtils.isNotEmpty(uri)) {
            if (uri.indexOf('?') == -1) {
                uri += '?';
            }
            StringBuilder sb = new StringBuilder(uri);
            for (String key : paramMap.keySet()) {
                String param = key + '=' + ObjectUtils.nvl(paramMap.get(key), "");
                sb.append(sb.toString().endsWith("?") ? param : '&' + param);
            }
            uri = sb.toString();
        }
        return uri;
    }

}

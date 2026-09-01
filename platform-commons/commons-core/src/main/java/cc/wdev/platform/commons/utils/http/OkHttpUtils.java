package cc.wdev.platform.commons.utils.http;

import cc.wdev.platform.commons.utils.JacksonUtils;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cc.wdev.platform.commons.utils.http.HttpUtils.*;

/**
 * OkHttpUtils
 * 基于OkHttp封装网络请求相关的方法
 * <a href="https://square.github.io/okhttp/">...</a>
 *
 * @author elvea
 */
public abstract class OkHttpUtils {

    // -----------------------------------------------------------------------------------------------------------------
    // Get
    // -----------------------------------------------------------------------------------------------------------------

    public static Response get(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        HttpUrl.Builder builder = HttpUrl.get(uri).newBuilder();
        if (MapUtils.isNotEmpty(params)) {
            params.forEach(builder::addQueryParameter);
        }
        Request.Builder request = new Request.Builder().url(builder.build()).get();
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(request::addHeader);
        }
        return execute(request.build());
    }

    public static Response get(String uri, Map<String, String> params) throws Exception {
        return get(uri, Maps.newHashMap(), params);
    }

    public static Response get(String uri) throws Exception {
        return get(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Post
    // -----------------------------------------------------------------------------------------------------------------

    public static Response post(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        Request.Builder builder = new Request.Builder().url(uri);
        setRequestHeader(builder, headers);
        setRequestParams(builder, params);
        return execute(builder.build());
    }

    public static Response post(String uri, Map<String, String> params) throws Exception {
        return post(uri, Maps.newHashMap(), params);
    }

    public static Response post(String uri) throws Exception {
        return post(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Post JSON
    // -----------------------------------------------------------------------------------------------------------------

    public static Response postJson(String uri, Map<String, String> headers, String json) throws Exception {
        Request.Builder builder = new Request.Builder().url(uri);
        setRequestHeader(builder, headers);
        setRequestJson(builder, json);
        return execute(builder.build());
    }

    public static Response postJson(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        Request.Builder builder = new Request.Builder().url(uri);
        setRequestHeader(builder, headers);
        setRequestJson(builder, params);
        return execute(builder.build());
    }

    public static Response postJson(String uri, Map<String, String> params) throws Exception {
        return postJson(uri, Collections.emptyMap(), params);
    }

    public static Response postJson(String uri, String json) throws Exception {
        return postJson(uri, Collections.emptyMap(), json);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Commons
    // -----------------------------------------------------------------------------------------------------------------

    public static void setRequestHeader(Request.Builder builder, Map<String, String> headers) {
        if (!ObjectUtils.isEmpty(headers)) {
            headers.forEach(builder::addHeader);
        }
    }

    public static void setRequestParams(Request.Builder builder, Map<String, String> params) {
        if (!ObjectUtils.isEmpty(params)) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            params.forEach(bodyBuilder::add);
            builder.post(bodyBuilder.build());
        }
    }

    public static void setRequestJson(Request.Builder builder, Map<String, String> params) throws Exception {
        if (!ObjectUtils.isEmpty(params)) {
            setRequestJson(builder, JacksonUtils.toJson(params));
        }
    }

    public static void setRequestJson(Request.Builder builder, String json) {
        builder.post(RequestBody.create(json, MEDIA_TYPE_JSON));
    }

    /**
     * 获取客户端构建器
     */
    public static OkHttpClient.Builder getBuilder() {
        return new OkHttpClient().newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取客户端
     */
    public static OkHttpClient getClient() {
        return getBuilder().build();
    }

    /**
     * 执行请求
     */
    public static Response execute(Request request) throws Exception {
        return execute(getClient(), request);
    }

    /**
     * 执行请求
     */
    public static Response execute(OkHttpClient client, Request request) throws Exception {
        return client.newCall(request).execute();
    }

}

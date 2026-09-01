package cc.wdev.platform.commons.utils.http;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpComponentsUtils
 * 基于HttpComponents封装网络请求相关的方法
 * <a href="https://hc.apache.org/">...</a>
 *
 * @author elvea
 */
public abstract class ApacheHttpUtils extends HttpUtils {

    // -----------------------------------------------------------------------------------------------------------------
    // Get
    // -----------------------------------------------------------------------------------------------------------------

    public static String get(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(createURI(uri, params)).build();
            setRequestHeader(httpGet, headers);
            return getHttpResponse(client, httpGet);
        }
    }

    public static String get(String uri, Map<String, String> params) throws Exception {
        return get(uri, new HashMap<>(), params);
    }

    public static String get(String uri) throws Exception {
        return get(uri, new HashMap<>(), new HashMap<>());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Post
    // -----------------------------------------------------------------------------------------------------------------

    public static String post(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest httpPost = ClassicRequestBuilder.post(createURI(uri)).build();
            setRequestHeader(httpPost, headers);
            setRequestParams(httpPost, params);
            return getHttpResponse(client, httpPost);
        }
    }

    public static String post(String uri, Map<String, String> params) throws Exception {
        return post(uri, Maps.newHashMap(), params);
    }

    public static String post(String uri) throws Exception {
        return post(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Post Json
    // -----------------------------------------------------------------------------------------------------------------

    public static String postJson(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest httpPost = ClassicRequestBuilder.post(createURI(uri)).build();
            setRequestHeader(httpPost, headers);
            setRequestJson(httpPost, params);
            return getHttpResponse(client, httpPost);
        }
    }

    public static String postJson(String uri, Map<String, String> params) throws Exception {
        return postJson(uri, Maps.newHashMap(), params);
    }

    public static String postJson(String uri) throws Exception {
        return postJson(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Put
    // -----------------------------------------------------------------------------------------------------------------

    public static String put(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest httpPut = ClassicRequestBuilder.post(createURI(uri)).build();
            setRequestHeader(httpPut, headers);
            setRequestParams(httpPut, params);
            return getHttpResponse(client, httpPut);
        }
    }

    public static String put(String uri, Map<String, String> params) throws Exception {
        return put(uri, Maps.newHashMap(), params);
    }

    public static String put(String uri) throws Exception {
        return put(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------------------------------------------------

    public static String delete(String uri, Map<String, String> headers, Map<String, String> params) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest httpDelete = ClassicRequestBuilder.delete(createURI(uri)).build();
            setRequestHeader(httpDelete, headers);
            setRequestParams(httpDelete, params);
            return getHttpResponse(client, httpDelete);
        }
    }

    public static String delete(String uri, Map<String, String> params) throws Exception {
        return delete(uri, Maps.newHashMap(), params);
    }

    public static String delete(String uri) throws Exception {
        return delete(uri, Maps.newHashMap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Commons
    // -----------------------------------------------------------------------------------------------------------------

    private static URI createURI(String uri) throws Exception {
        return createURI(uri, Maps.newHashMap());
    }

    private static URI createURI(String uri, Map<String, String> params) throws Exception {
        URIBuilder builder = new URIBuilder(uri);
        if (!ObjectUtils.isEmpty(params)) {
            params.forEach(builder::setParameter);
        }
        return builder.build();
    }

    public static void setRequestHeader(ClassicHttpRequest httpRequest, Map<String, String> headers) {
        if (!ObjectUtils.isEmpty(headers)) {
            headers.forEach(httpRequest::setHeader);
        }
    }

    public static void setRequestParams(ClassicHttpRequest httpRequest, Map<String, String> params) {
        if (!ObjectUtils.isEmpty(params)) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            params.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
            httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, GlobalConstants.UTF8));
        }
    }

    public static void setRequestJson(ClassicHttpRequest httpRequest, Map<String, String> params) throws Exception {
        if (!ObjectUtils.isEmpty(params)) {
            String json = JacksonUtils.toJson(params);
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpRequest.setEntity(stringEntity);
        }
    }

    public static String getHttpResponse(HttpClient httpClient, ClassicHttpRequest httpRequest) throws Exception {
        return httpClient.execute(httpRequest, response -> {
            String resp = "";
            if (!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.getEntity())) {
                resp = EntityUtils.toString(response.getEntity(), GlobalConstants.UTF8);
            }
            return resp;
        });
    }

    /**
     * 获取客户端构建器
     */
    public static HttpClientBuilder getBuilder() {
        return HttpClients.custom();
    }

    /**
     * 获取客户端
     */
    public static CloseableHttpClient getClient() {
        return getBuilder().build();
    }

}

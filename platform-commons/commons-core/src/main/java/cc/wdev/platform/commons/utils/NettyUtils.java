package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.GlobalConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;

import static cc.wdev.platform.commons.utils.NetUtils.isValidIp;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author elvea
 */
public abstract class NettyUtils {

    public static String getAuthorization(FullHttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        List<String> params = decoder.parameters().get("authorization");
        if (params != null && !params.isEmpty()) {
            return params.getFirst();
        }
        return null;
    }

    public static boolean isWebSocketHandshake(FullHttpRequest request) {
        return request.method() == HttpMethod.GET
            && request.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
    }

    public static void sendUnauthorizedResponse(ChannelHandlerContext ctx, FullHttpRequest req) {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.UNAUTHORIZED
        );
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private FullHttpResponse getFullHttpResponse(HttpResponseStatus status, String content) throws UnsupportedEncodingException {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.wrappedBuffer(content.getBytes(GlobalConstants.ENCODING)));
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.headers().set("Access-Control-Allow-Headers", "DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization");
        response.headers().setInt("Content-Length", response.content().readableBytes());
        return response;
    }

    public static String getRemoteIp(FullHttpRequest request, ChannelHandlerContext ctx) {
        // 1. 尝试从 X-Forwarded-For 头部获取
        String xForwardedFor = request.headers().get("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            String[] ips = xForwardedFor.split(",");
            String ip = ips[0].trim();
            if (isValidIp(ip)) {
                return ip;
            }
        }

        // 2. 尝试从 X-Real-IP 头部获取
        String xRealIp = request.headers().get("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && isValidIp(xRealIp)) {
            return xRealIp;
        }

        // 3. 尝试从 Proxy-Client-IP 头部获取
        String proxyClientIp = request.headers().get("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty() && isValidIp(proxyClientIp)) {
            return proxyClientIp;
        }

        // 4. 尝试从 WL-Proxy-Client-IP 头部获取
        String wlProxyClientIp = request.headers().get("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && isValidIp(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        // 5. 尝试从 HTTP_CLIENT_IP 头部获取
        String httpClientIp = request.headers().get("HTTP_CLIENT_IP");
        if (httpClientIp != null && !httpClientIp.isEmpty() && isValidIp(httpClientIp)) {
            return httpClientIp;
        }

        // 6. 尝试从 HTTP_X_FORWARDED_FOR 头部获取
        String httpXForwardedFor = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (httpXForwardedFor != null && !httpXForwardedFor.isEmpty()) {
            String[] ips = httpXForwardedFor.split(",");
            String clientIp = ips[0].trim();
            if (isValidIp(clientIp)) {
                return clientIp;
            }
        }
        return getRemoteHostAddress(ctx);
    }

    public static String getRemoteHostAddress(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        }
        return null;
    }

}

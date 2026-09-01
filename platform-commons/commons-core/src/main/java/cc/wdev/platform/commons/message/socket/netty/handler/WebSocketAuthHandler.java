package cc.wdev.platform.commons.message.socket.netty.handler;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.commons.utils.NettyUtils;
import cc.wdev.platform.commons.utils.SpringUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.jwt.JwtService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class WebSocketAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (NettyUtils.isWebSocketHandshake(req)) {
            String token = NettyUtils.getAuthorization(req);
            if (StringUtils.isEmpty(token)) {
                log.error("Authorization header is empty. Request URI: {}. Remote IP: {}", req.uri(), NettyUtils.getRemoteIp(req, ctx));
                NettyUtils.sendUnauthorizedResponse(ctx, req);
                return;
            }

            JwtService jwtService = SpringUtils.getBean(JwtService.class);
            try {
                Jwt jwt = jwtService.parseJwtToken(token);
                Map<String, Object> claims = jwt.getClaims();
                Long uid = MapUtils.getLong(claims, SecurityConstants.JWT_KEY_UID);
                String username = MapUtils.getString(claims, SecurityConstants.JWT_KEY_USERNAME);
                log.info("Authorization paas. [{}][{}]. Remote IP: {}", uid, username, NettyUtils.getRemoteIp(req, ctx));
            } catch (Exception e) {
                log.error("Invalid token. Request URI: {}. Remote IP: {}", req.uri(), NettyUtils.getRemoteIp(req, ctx), e);
                NettyUtils.sendUnauthorizedResponse(ctx, req);
                return;
            }
            ctx.fireChannelRead(req.retain());
        } else {
            ctx.fireChannelRead(req.retain());
        }
    }

}

package cc.wdev.platform.commons.message.socket.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public class WebSocketHeartbeatHandler extends ChannelInboundHandlerAdapter {

    int idles = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            log.info("{} channel [{}}]", IdleState.READER_IDLE, ctx.channel().id().asShortText());
            if (event.state().equals(IdleState.READER_IDLE)) {
                idles++;
                if (idles > 3) {
                    log.info("{} channel [{}}] close.", IdleState.READER_IDLE, ctx.channel().id().asShortText());
                    ctx.channel().close();
                } else {
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("ping"));
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

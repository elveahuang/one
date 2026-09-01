package cc.wdev.platform.commons.message.socket.netty.handler;

import cc.wdev.platform.commons.message.socket.netty.NettyWebSocketManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
@ChannelHandler.Sharable
public abstract class WebSocketMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    protected abstract NettyWebSocketManager getWebSocketManager();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 客户端成功连接
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        this.getWebSocketManager().createSession(channel);
    }

    /**
     * 客户端断开连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        this.getWebSocketManager().createSession(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        Channel channel = ctx.channel();
        if (frame instanceof TextWebSocketFrame textFrame) {
            String message = textFrame.text();
            log.info("Text Message received from [{}]：{}", channel.id(), message);
            channel.writeAndFlush(new TextWebSocketFrame("Get"));
        } else if (frame instanceof BinaryWebSocketFrame binaryFrame) {
            log.info("Binary Message received from [{}]", channel.id());
        }
    }

    /**
     * 通道活跃
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Channel [{}] active.", ctx.channel().id());
    }

    /**
     * 通道不活跃
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Channel [{}] disconnected.", ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught in WebSocketMessageHandler", cause);
        ctx.close();
    }

}

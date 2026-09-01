package cc.wdev.platform.commons.message.socket.netty;

import cc.wdev.platform.commons.message.socket.netty.handler.WebSocketAuthHandler;
import cc.wdev.platform.commons.message.socket.netty.handler.WebSocketMessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author elvea
 */
public class NettyWebSocketInitializer extends ChannelInitializer<SocketChannel> {

    private final WebSocketMessageHandler webSocketMessageHandler;

    public NettyWebSocketInitializer(WebSocketMessageHandler webSocketMessageHandler) {
        this.webSocketMessageHandler = webSocketMessageHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // 日志处理器
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        // 基础处理器
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerCompressionHandler(0));
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        // 认证处理器
        ch.pipeline().addLast(new WebSocketAuthHandler());
        // 协议处理器
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true));
        // 心跳处理器
        pipeline.addLast(new IdleStateHandler(60, 0, 0));
        // 消息处理器
        pipeline.addLast(webSocketMessageHandler);
    }

}

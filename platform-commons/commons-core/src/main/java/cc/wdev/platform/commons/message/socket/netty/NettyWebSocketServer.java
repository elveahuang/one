package cc.wdev.platform.commons.message.socket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static cc.wdev.platform.commons.message.socket.WebSocketConstants.WEB_SOCKET_PORT;

/**
 * @author elvea
 */
@Slf4j
public class NettyWebSocketServer {

    private final NettyWebSocketInitializer initializer;

    private EventLoopGroup parentGroup;

    private EventLoopGroup childGroup;

    @Getter
    private boolean ready;

    private Channel channel;

    public NettyWebSocketServer(NettyWebSocketInitializer initializer) {
        this.initializer = initializer;
    }

    @PostConstruct
    public void start() {
        try {
            this.parentGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
            this.childGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(initializer);

            this.channel = bootstrap.bind(WEB_SOCKET_PORT).sync().channel();
            System.out.println("Netty WebSocket server started on port " + WEB_SOCKET_PORT);
            this.ready = true;
        } catch (Exception e) {
            log.error("Netty WebSocket server start failed", e);
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.channel != null) {
            this.channel.close();
        }
        if (this.parentGroup != null && this.parentGroup.isShutdown() && this.childGroup.isShuttingDown()) {
            parentGroup.shutdownGracefully();
        }
        if (this.childGroup != null && this.childGroup.isShutdown() && this.childGroup.isShuttingDown()) {
            childGroup.shutdownGracefully();
        }
        this.ready = false;
        System.out.println("Netty WebSocket server stopped");
    }

}

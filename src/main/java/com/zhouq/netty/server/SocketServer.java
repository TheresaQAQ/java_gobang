package com.zhouq.netty.server;

import com.zhouq.core.entity.Game;
import com.zhouq.core.handlerProcess.FunctionLoader;
import com.zhouq.netty.message.MessageCodecSharable;
import com.zhouq.netty.message.basic.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务端
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */
@Slf4j
public class SocketServer {
    public static Map<Integer, Game> games = new HashMap<>();
    public static void main(String[] args) {
        runServer(8080);
    }

    public static void runServer(int point) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        try {
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<Message>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Message message) {
                            FunctionLoader.execute(
                                    "com.zhouq.netty.server.handler.ServerMessageHandler",
                                    message,
                                    ctx,
                                    games);
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(point).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动失败");
        }
    }
}
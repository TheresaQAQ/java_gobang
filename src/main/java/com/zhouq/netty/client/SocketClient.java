package com.zhouq.netty.client;

import com.zhouq.core.entity.Player;
import com.zhouq.gui.GamePage;
import com.zhouq.core.handlerProcess.FunctionLoader;
import com.zhouq.netty.message.MessageCodecSharable;
import com.zhouq.netty.message.basic.Message;
import com.zhouq.netty.message.requests.CreateGameRequestsMessage;
import com.zhouq.netty.message.requests.JoinGameRequestsMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * <p>
 *  客户端
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */
@Slf4j
public class SocketClient {
    public static void main(String[] args){
        runClient("localhost", 8080, null, null);
    }

    public static void runClient(String host, int port, Integer id, JFrame frame) {
        GamePage gamePage = new GamePage(id == null ? "五子棋:黑方" : "五子棋:白方");
        NioEventLoopGroup group = new NioEventLoopGroup();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        try {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel){
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    socketChannel.pipeline().addLast("test", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx){
                            frame.setVisible(false);
                            if (id == null) {
                                log.info("开始创建对局...");
                                CreateGameRequestsMessage message = new CreateGameRequestsMessage();
                                ctx.writeAndFlush(message);
                            } else {
                                log.info("申请加入对局...");
                                JoinGameRequestsMessage message = new JoinGameRequestsMessage();
                                message.setGameId(id);
                                message.setPlayerType(Player.WHITE_CHESS);
                                ctx.writeAndFlush(message);
                            }
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx){
                            frame.setVisible(true);
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<Message>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Message message) {
                            FunctionLoader.execute(
                                    "com.zhouq.netty.client.handler.ClientMessageHandler"
                                    , message
                                    , ctx
                                    , gamePage
                                    , frame);
                        }
                    });

                }
            });
            Channel channel = bootstrap.connect(host, port).sync().channel();
            gamePage.setChannel(channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动失败...");
        }
    }
}


package com.zhouq.nio.client;

import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.Player;
import com.zhouq.gui.GamePage;
import com.zhouq.nio.message.MessageCodecSharable;
import com.zhouq.nio.message.requests.CreateGameRequestsMessage;
import com.zhouq.nio.message.requests.JoinGameRequestsMessage;
import com.zhouq.nio.message.requests.PlayChessRequestsMessage;
import com.zhouq.nio.message.response.CreateGameResponseMessage;
import com.zhouq.nio.message.response.JoinGameResponseMessage;
import com.zhouq.nio.message.response.PlayChessResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */

public class SocketClient {
    public static void main(String[] args) throws InterruptedException {
        runClient("localhost", 8080, null);
    }

    public static void runClient(String host, int port, Integer id) {
        GamePage gamePage = new GamePage(id == null ? "五子棋:黑方" : "五子棋:白方");
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        try {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    socketChannel.pipeline().addLast("test", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            if (id == null) {
                                CreateGameRequestsMessage message = new CreateGameRequestsMessage();
                                ctx.writeAndFlush(message);
                            } else {
                                JoinGameRequestsMessage message = new JoinGameRequestsMessage();
                                message.setGameID(id);
                                message.setPlayerType(Player.WHITE_CHESS);
                                ctx.writeAndFlush(message);
                            }
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<CreateGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, CreateGameResponseMessage msg) throws Exception {
                            JoinGameRequestsMessage message = new JoinGameRequestsMessage();
                            message.setGameID(msg.getGameID());
                            message.setPlayerType(Player.BLACK_CHESS);
                            ctx.writeAndFlush(message);
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<JoinGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, JoinGameResponseMessage msg) throws Exception {
                            if (msg.isFlag()) {
                                System.out.println("连接成功...");
                                gamePage.setGameId(msg.getGameId());
                                gamePage.setPlayerType(msg.getPlayerType());
                                System.out.println("我是" + msg.getPlayerType());
                            }
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<PlayChessResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, PlayChessResponseMessage msg) throws Exception {
                            Game game = msg.getGame();
                            gamePage.setMaps(game.getMaps());
                            gamePage.setBlack(game.isBlack());
                            gamePage.repaint();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect(host, port).sync().channel();
            gamePage.setChannel(channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("启动失败...");
        }
    }
}


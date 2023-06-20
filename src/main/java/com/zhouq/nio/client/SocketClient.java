package com.zhouq.nio.client;

import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.Player;
import com.zhouq.gui.GamePage;
import com.zhouq.nio.client.handler.ResponseMessageHandler;
import com.zhouq.nio.message.MessageCodecSharable;
import com.zhouq.nio.message.requests.CreateGameRequestsMessage;
import com.zhouq.nio.message.requests.JoinGameRequestsMessage;
import com.zhouq.nio.message.requests.RetractChessRequestsMessage;
import com.zhouq.nio.message.requests.SuePeaceRequestsMessage;
import com.zhouq.nio.message.response.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */
@Slf4j
public class SocketClient {
    public static void main(String[] args) throws InterruptedException {
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
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    socketChannel.pipeline().addLast("test", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
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
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            frame.setVisible(true);
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<CreateGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, CreateGameResponseMessage msg) throws Exception {
                            if (msg.isFlag()) {
                                JOptionPane.showMessageDialog(gamePage, "对局创建成功，对局ID为：" + msg.getGameId());
                                JoinGameRequestsMessage message = new JoinGameRequestsMessage();
                                message.setGameId(msg.getGameId());
                                message.setPlayerType(Player.BLACK_CHESS);
                                ctx.writeAndFlush(message);
                            } else {
                                JOptionPane.showMessageDialog(gamePage, "对局创建失败");
                            }
                        }
                    });
                    socketChannel.pipeline().addLast(ResponseMessageHandler.CREATE_GAME);
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<JoinGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, JoinGameResponseMessage msg) throws Exception {
                            if (msg.isFlag()) {
                                JOptionPane.showMessageDialog(gamePage, "连接成功");
                                gamePage.setGameId(msg.getGameId());
                                gamePage.setPlayerType(msg.getPlayerType());
                                log.debug("我是" + msg.getPlayerType());
                            } else {
                                JOptionPane.showMessageDialog(gamePage, "连接失败");
                                gamePage.setVisible(false);
                                frame.setVisible(true);
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
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<WinGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, WinGameResponseMessage winGameResponseMessage) throws Exception {
                            JOptionPane.showMessageDialog(gamePage, "你赢了！");
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<LoseGameResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoseGameResponseMessage loseGameResponseMessage) throws Exception {
                            JOptionPane.showMessageDialog(gamePage, "你输了！");
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<RetractChessResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RetractChessResponseMessage msg) throws Exception {
                            Game game = msg.getGame();
                            gamePage.setMaps(game.getMaps());
                            gamePage.repaint();
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<RetractChessRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RetractChessRequestsMessage msg) throws Exception {
                            int confirmRetract = JOptionPane.showConfirmDialog(gamePage, "是否同意对方悔棋", "请求悔棋", JOptionPane.YES_NO_OPTION);
                            RetractChessResponseMessage response = new RetractChessResponseMessage();
                            response.setGameId(msg.getGameId());
                            response.setTo(msg.getTo());
                            response.setFrom(msg.getFrom());
                            if (confirmRetract == 0) {
                                response.setFlag(true);
                            } else if (confirmRetract == 1) {
                                response.setFlag(false);
                            }
                            channelHandlerContext.writeAndFlush(response);
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<SuePeaceResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, SuePeaceResponseMessage msg) throws Exception {
                            JOptionPane.showMessageDialog(gamePage,"和棋了");
                        }
                    });
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<SuePeaceRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, SuePeaceRequestsMessage msg) throws Exception {
                            log.debug("客户端收到求和请求");
                            int confirmSueSpace = JOptionPane.showConfirmDialog(gamePage, "是否同意与对方和棋", "请求和棋", JOptionPane.YES_NO_OPTION);
                            SuePeaceResponseMessage response = new SuePeaceResponseMessage();
                            response.setGameId(msg.getGameId());
                            response.setTo(msg.getTo());
                            response.setFrom(msg.getFrom());
                            if (confirmSueSpace == 0) {
                                response.setFlag(true);
                            } else if (confirmSueSpace == 1) {
                                response.setFlag(false);
                            }
                            channelHandlerContext.writeAndFlush(response);
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


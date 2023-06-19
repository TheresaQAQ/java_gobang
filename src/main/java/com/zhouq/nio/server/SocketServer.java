package com.zhouq.nio.server;

import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.Player;
import com.zhouq.myUtils.RandomUtils;
import com.zhouq.nio.message.MessageCodecSharable;
import com.zhouq.nio.message.requests.CreateGameRequestsMessage;
import com.zhouq.nio.message.requests.JoinGameRequestsMessage;
import com.zhouq.nio.message.requests.PlayChessRequestsMessage;
import com.zhouq.nio.message.response.CreateGameResponseMessage;
import com.zhouq.nio.message.response.JoinGameResponseMessage;
import com.zhouq.nio.message.response.PlayChessResponseMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */

public class SocketServer {
    public static void main(String[] args) {
        runServer(8080);
    }

    public static void runServer(int point) {
        Map<Integer, Game> games = new HashMap<>();
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
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<CreateGameRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext context, CreateGameRequestsMessage msg) throws Exception {
                            int id = RandomUtils.randomInt(6);
                            System.out.println("对局id为：" + id);
                            games.put(id, new Game());
                            games.values().forEach(System.out::print);
                            context.writeAndFlush(new CreateGameResponseMessage(id));
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<JoinGameRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, JoinGameRequestsMessage msg) throws Exception {
                            System.out.println("我是" + msg.getPlayerType());
                            Integer gameID = msg.getGameID();
                            Game game = games.get(gameID);
                            if (msg.getPlayerType() == Player.BLACK_CHESS) {
                                game.setBlackPlayer(new Player(Player.BLACK_CHESS, channelHandlerContext));
                            } else {
                                game.setWhitePlayer(new Player(Player.WHITE_CHESS, channelHandlerContext));
                            }
                            JoinGameResponseMessage message = new JoinGameResponseMessage();
                            message.setFlag(true);
                            message.setGameId(gameID);
                            message.setPlayerType(msg.getPlayerType());
                            channelHandlerContext.writeAndFlush(message);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<PlayChessRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, PlayChessRequestsMessage msg) throws Exception {
                            int sequenceId = msg.getGameId();
                            Game game = games.get(sequenceId);
                            System.out.println("上传信息的是：" + msg.getPlayerType());
                            if (game.isBlack() != (msg.getPlayerType() == Player.BLACK_CHESS)) {
                                System.out.println("不是" + msg.getPlayerType() + "出棋");
                                return;
                            }
                            int[][] maps = game.getMaps();
                            if (msg.getPlayerType() == Player.BLACK_CHESS) {
                                maps[msg.getMapY()][msg.getMapX()] = 1;
                                game.setBlack(false);
                            } else {
                                maps[msg.getMapY()][msg.getMapX()] = 2;
                                game.setBlack(true);
                            }
                            game.setMaps(maps);

                            games.put(sequenceId, game);
                            game.getBlackPlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));
                            game.getWhitePlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(point).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("服务启动失败。。。");
        }

    }
}
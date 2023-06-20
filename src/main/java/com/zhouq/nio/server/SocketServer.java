package com.zhouq.nio.server;

import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.PlayChessHistory;
import com.zhouq.core.entity.Player;
import com.zhouq.myUtils.GameUtils;
import com.zhouq.myUtils.RandomUtils;
import com.zhouq.nio.message.MessageCodecSharable;
import com.zhouq.nio.message.requests.*;
import com.zhouq.nio.message.response.*;
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
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 18:54
 */
@Slf4j
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
                            CreateGameResponseMessage message = new CreateGameResponseMessage();
                            try {
                                int id = RandomUtils.randomInt(6);
                                games.put(id, new Game());
                                message.setGameId(id);
                                message.setFlag(true);
                                log.info("成功创建对局");
                            } catch (Exception e) {
                                message.setFlag(false);
                                log.info("创建对局失败");
                            }
                            context.writeAndFlush(message);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<JoinGameRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, JoinGameRequestsMessage msg) throws Exception {
                            JoinGameResponseMessage message = new JoinGameResponseMessage();
                            try {
                                int gameID = msg.getGameId();
                                Game game = games.get(gameID);
                                if (msg.getPlayerType() == Player.BLACK_CHESS) {
                                    game.setBlackPlayer(new Player(Player.BLACK_CHESS, channelHandlerContext));
                                } else {
                                    game.setWhitePlayer(new Player(Player.WHITE_CHESS, channelHandlerContext));
                                }
                                message.setGameId(gameID);
                                message.setPlayerType(msg.getPlayerType());
                                message.setFlag(true);
                            } catch (Exception e) {
                                message.setFlag(false);
                            }
                            channelHandlerContext.writeAndFlush(message);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<PlayChessRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, PlayChessRequestsMessage msg) throws Exception {
                            int gameId = msg.getGameId();
                            Game game = games.get(gameId);
                            log.debug("上传信息的是：" + msg.getPlayerType());
                            if (game.isBlack() != (msg.getPlayerType() == Player.BLACK_CHESS)) {
                                log.debug("不是" + msg.getPlayerType() + "出棋");
                                return;
                            }
                            if (game.isHasWinner()) {
                                return;
                            }
                            int[][] maps = game.getMaps();
                            if (msg.getPlayerType() == Player.BLACK_CHESS) {
                                maps[msg.getMapY()][msg.getMapX()] = 1;
                                game.getPlayChessHistory().add(new PlayChessHistory(msg.getMapX(), msg.getMapY(), Player.BLACK_CHESS));
                                game.setBlack(false);
                            } else {
                                maps[msg.getMapY()][msg.getMapX()] = 2;
                                game.getPlayChessHistory().add(new PlayChessHistory(msg.getMapX(), msg.getMapY(), Player.WHITE_CHESS));
                                game.setBlack(true);
                            }
                            game.setMaps(maps);

                            games.put(gameId, game);
                            game.getBlackPlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));
                            game.getWhitePlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));

                            int check = GameUtils.check(maps, msg.getMapX(), msg.getMapY());
                            if (check == Player.BLACK_CHESS) {
                                game.setHasWinner(true);
                                game.getBlackPlayer().getChannel().writeAndFlush(new WinGameResponseMessage());
                                game.getWhitePlayer().getChannel().writeAndFlush(new LoseGameResponseMessage());
                            } else if (check == Player.WHITE_CHESS) {
                                game.setHasWinner(true);
                                game.getBlackPlayer().getChannel().writeAndFlush(new LoseGameResponseMessage());
                                game.getWhitePlayer().getChannel().writeAndFlush(new WinGameResponseMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<RetractChessResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RetractChessResponseMessage msg) throws Exception {
                            if (!msg.isFlag()) {
                                return;
                            }
                            //修改棋盘
                            log.info(Player.NAMES.get(Player.NAMES.get(msg.getTo()) + "同意" + Player.NAMES.get(msg.getFrom()) + "悔棋"));
                            Game game = games.get(msg.getGameId());
                            List<PlayChessHistory> history = game.getPlayChessHistory();
                            PlayChessHistory playChessHistory = history.get(history.size() - 1);
                            game.getMaps()[playChessHistory.getMapY()][playChessHistory.getMapX()] = 0;

                            //修改下棋对象
                            game.setBlack(!game.isBlack());

                            //更新棋盘
                            RetractChessResponseMessage response = new RetractChessResponseMessage();
                            response.setMsg(Player.NAMES.get(msg.getTo()) + "同意" + Player.NAMES.get(msg.getFrom()) + "悔棋");
                            response.setGame(game);

                            game.getBlackPlayer().getChannel().writeAndFlush(response);
                            game.getWhitePlayer().getChannel().writeAndFlush(response);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<RetractChessRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RetractChessRequestsMessage msg) throws Exception {
                            Game game = games.get(msg.getGameId());
                            if ((game.isBlack() && msg.getFrom() == Player.BLACK_CHESS) || !game.isBlack() && msg.getFrom() == Player.WHITE_CHESS) {
                                return;
                            }
                            if (game.isHasWinner()) {
                                return;
                            }
                            if (game.getPlayChessHistory().size() == 0) {
                                return;
                            }
                            log.info(Player.NAMES.get(Player.NAMES.get(msg.getFrom()) + "向" + Player.NAMES.get(msg.getTo())));
                            msg.setMsg(Player.NAMES.get(msg.getFrom()) + "向" + Player.NAMES.get(msg.getTo()));
                            game.getPlay(msg.getTo()).getChannel().writeAndFlush(msg);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<SuePeaceRequestsMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, SuePeaceRequestsMessage msg) throws Exception {
                            log.debug("收到和棋请求");
                            Game game = games.get(msg.getGameId());
                            if (game.isHasWinner()) {
                                return;
                            }
                            game.getPlay(msg.getTo()).getChannel().writeAndFlush(msg);
                        }
                    });
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<SuePeaceResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, SuePeaceResponseMessage msg) throws Exception {
                            if (!msg.isFlag()) {
                                return;
                            }
                            Game game = games.get(msg.getGameId());
                            game.setHasWinner(true);
                            game.getBlackPlayer().getChannel().writeAndFlush(msg);
                            game.getWhitePlayer().getChannel().writeAndFlush(msg);
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
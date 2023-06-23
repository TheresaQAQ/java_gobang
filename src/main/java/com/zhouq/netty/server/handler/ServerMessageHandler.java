package com.zhouq.netty.server.handler;

import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.ChessHistory;
import com.zhouq.core.entity.Player;
import com.zhouq.core.utils.GameUtils;
import com.zhouq.core.utils.RandomUtils;
import com.zhouq.core.handlerProcess.HandlerAnno;
import com.zhouq.netty.message.basic.Message;
import com.zhouq.netty.message.requests.*;
import com.zhouq.netty.message.response.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/21 11:51
 */
@Slf4j
public class ServerMessageHandler {
    @HandlerAnno(messageType = Message.CREATE_GAME_RESPONSE)
    public void createGameResponse(CreateGameResponseMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {

    }

    @HandlerAnno(messageType = Message.CREATE_GAME_REQUESTS)
    public void createGameRequest(Message msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        CreateGameResponseMessage message = new CreateGameResponseMessage();
        try {
            int id = RandomUtils.randomInt(4);
            games.put(id, new Game());
            message.setGameId(id);
            message.setFlag(true);
            log.info("成功创建对局");
        } catch (Exception e) {
            message.setFlag(false);
            log.info("创建对局失败");
        }
        new Thread(() -> {
            ctx.writeAndFlush(message);
        }).start();
    }

    @HandlerAnno(messageType = Message.JOIN_GAME_RESPONSE)
    public void joinGameResponseMessage(JoinGameResponseMessage message, ChannelHandlerContext ctx, Map<Integer, Game> games) {

    }

    @HandlerAnno(messageType = Message.JOIN_GAME_REQUESTS)
    public void joinGameRequests(JoinGameRequestsMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        JoinGameResponseMessage message = new JoinGameResponseMessage();
        try {
            int gameID = msg.getGameId();
            Game game = games.get(gameID);
            if (msg.getPlayerType() == Player.BLACK_CHESS) {
                game.setBlackPlayer(new Player(Player.BLACK_CHESS, ctx));
            } else {
                game.setWhitePlayer(new Player(Player.WHITE_CHESS, ctx));
            }
            message.setGameId(gameID);
            message.setPlayerType(msg.getPlayerType());
            message.setFlag(true);
        } catch (Exception e) {
            message.setFlag(false);
        }
        new Thread(() -> {
            ctx.writeAndFlush(message);
        }).start();
    }

    @HandlerAnno(messageType = Message.PLAY_CHESS_RESPONSE)
    public void playChessResponse(PlayChessResponseMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {

    }

    @HandlerAnno(messageType = Message.PLAY_CHESS_REQUESTS)
    public void playChessRequests(PlayChessRequestsMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
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
            game.getChessHistory().add(new ChessHistory(msg.getMapX(), msg.getMapY(), Player.BLACK_CHESS));
            game.setBlack(false);
        } else {
            maps[msg.getMapY()][msg.getMapX()] = 2;
            game.getChessHistory().add(new ChessHistory(msg.getMapX(), msg.getMapY(), Player.WHITE_CHESS));
            game.setBlack(true);
        }
        game.setMaps(maps);

        games.put(gameId, game);
        new Thread(() -> {
            game.getBlackPlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));
            game.getWhitePlayer().getChannel().writeAndFlush(new PlayChessResponseMessage(game, true));
        }).start();

        int check = GameUtils.check(maps, msg.getMapX(), msg.getMapY());
        if (check == Player.BLACK_CHESS) {
            game.setHasWinner(true);
            new Thread(() -> {
                game.getBlackPlayer().getChannel().writeAndFlush(new WinGameResponseMessage());
                game.getWhitePlayer().getChannel().writeAndFlush(new LoseGameResponseMessage());
            }).start();
        } else if (check == Player.WHITE_CHESS) {
            game.setHasWinner(true);
            new Thread(() -> {
                game.getBlackPlayer().getChannel().writeAndFlush(new LoseGameResponseMessage());
                game.getWhitePlayer().getChannel().writeAndFlush(new WinGameResponseMessage());
            }).start();
        }
    }

    @HandlerAnno(messageType = Message.RETRACT_CHESS_RESPONSE)
    public void retractChessResponse(RetractChessResponseMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        if (!msg.isFlag()) {
            return;
        }
        //修改棋盘
        log.info(Player.NAMES.get(Player.NAMES.get(msg.getTo()) + "同意" + Player.NAMES.get(msg.getFrom()) + "悔棋"));
        Game game = games.get(msg.getGameId());
        List<ChessHistory> history = game.getChessHistory();
        ChessHistory chessHistory = history.get(history.size() - 1);
        game.getMaps()[chessHistory.getMapY()][chessHistory.getMapX()] = 0;

        //修改下棋对象
        game.setBlack(!game.isBlack());

        //更新棋盘
        RetractChessResponseMessage response = new RetractChessResponseMessage();
        response.setGame(game);

        new Thread(() -> {
            game.getBlackPlayer().getChannel().writeAndFlush(response);
            game.getWhitePlayer().getChannel().writeAndFlush(response);
        }).start();

    }

    @HandlerAnno(messageType = Message.RETRACT_CHESS_REQUESTS)
    public void retractChessRequests(RetractChessRequestsMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        Game game = games.get(msg.getGameId());
        if ((game.isBlack() && msg.getFrom() == Player.BLACK_CHESS) || !game.isBlack() && msg.getFrom() == Player.WHITE_CHESS) {
            return;
        }
        if (game.isHasWinner()) {
            return;
        }
        if (game.getChessHistory().size() == 0) {
            return;
        }
        log.info(Player.NAMES.get(Player.NAMES.get(msg.getFrom()) + "向" + Player.NAMES.get(msg.getTo())));

        new Thread(() -> {
            game.getPlay(msg.getTo()).getChannel().writeAndFlush(msg);
        }).start();
    }

    @HandlerAnno(messageType = Message.SUE_PEACE_REQUESTS)
    public void suePeaceRequests(SuePeaceRequestsMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        log.debug("收到和棋请求");
        Game game = games.get(msg.getGameId());
        if (game.isHasWinner()) {
            return;
        }

        new Thread(() -> {
            game.getPlay(msg.getTo()).getChannel().writeAndFlush(msg);
        }).start();
    }

    @HandlerAnno(messageType = Message.SUE_PEACE_RESPONSE)
    public void suePeaceResponse(SuePeaceResponseMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        if (!msg.isFlag()) {
            return;
        }
        Game game = games.get(msg.getGameId());
        game.setHasWinner(true);
        new Thread(() -> {
            game.getBlackPlayer().getChannel().writeAndFlush(msg);
            game.getWhitePlayer().getChannel().writeAndFlush(msg);
        }).start();
    }

    @HandlerAnno(messageType = Message.CHAT_REQUESTS)
    public void chatRequests(ChatRequestsMessage msg, ChannelHandlerContext ctx, Map<Integer, Game> games) {
        Game game = games.get(msg.getGameId());
        game.getChatHistory().add(msg.getChat());

        ChatResponseMessage message = new ChatResponseMessage();
        message.setGameId(msg.getGameId());
        message.setChatHistory(game.getChatHistory());
        new Thread(() -> {
            game.getBlackPlayer().getChannel().writeAndFlush(message);
            game.getWhitePlayer().getChannel().writeAndFlush(message);
        }).start();
    }
}

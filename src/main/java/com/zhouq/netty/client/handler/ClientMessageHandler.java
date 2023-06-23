package com.zhouq.netty.client.handler;

import com.zhouq.core.entity.Chat;
import com.zhouq.core.entity.Game;
import com.zhouq.core.entity.Player;
import com.zhouq.core.handlerProcess.HandlerAnno;
import com.zhouq.gui.GamePage;
import com.zhouq.netty.message.basic.Message;
import com.zhouq.netty.message.requests.JoinGameRequestsMessage;
import com.zhouq.netty.message.requests.RetractChessRequestsMessage;
import com.zhouq.netty.message.requests.SuePeaceRequestsMessage;
import com.zhouq.netty.message.response.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/21 11:51
 */
@Slf4j
public class ClientMessageHandler {
    @HandlerAnno(messageType = Message.CREATE_GAME_RESPONSE)
    public void createGameResponse(CreateGameResponseMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        log.debug("创建对局");
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
    @HandlerAnno(messageType = Message.CREATE_GAME_REQUESTS)
    public void createGameRequest(JoinGameRequestsMessage message, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        message.setGameId(message.getGameId());
        message.setPlayerType(Player.BLACK_CHESS);
        new Thread(() -> {
            ctx.writeAndFlush(message);
        }).start();
    }

    @HandlerAnno(messageType = Message.JOIN_GAME_RESPONSE)
    public void joinGameResponseMessage(JoinGameResponseMessage message, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        if (message.isFlag()) {
            JOptionPane.showMessageDialog(gamePage, "连接成功");
            gamePage.setGameId(message.getGameId());
            gamePage.setPlayerType(message.getPlayerType());
        } else {
            JOptionPane.showMessageDialog(gamePage, "连接失败");
            gamePage.setVisible(false);
            frame.setVisible(true);
        }
    }

    @HandlerAnno(messageType = Message.PLAY_CHESS_RESPONSE)
    public void playChessResponse(PlayChessResponseMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        Game game = msg.getGame();
        gamePage.setMaps(game.getMaps());
        gamePage.setBlack(game.isBlack());
        if (gamePage.isBlack()) {
            gamePage.setMessage("黑方出棋");
        }else {
            gamePage.setMessage("白方出棋");
        }
        gamePage.repaint();
    }

    @HandlerAnno(messageType = Message.WIN_GAME_RESPONSE)
    public void winGame(Message msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        JOptionPane.showMessageDialog(gamePage, "你赢了！");
    }

    @HandlerAnno(messageType = Message.LOSE_GAME_RESPONSE)
    public void loseGame(Message msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        JOptionPane.showMessageDialog(gamePage, "你输了！");
    }

    @HandlerAnno(messageType = Message.RETRACT_CHESS_RESPONSE)
    public void retractChessResponse(RetractChessResponseMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        Game game = msg.getGame();
        gamePage.setMaps(game.getMaps());
        gamePage.setBlack(game.isBlack());
        if (gamePage.isBlack()) {
            gamePage.setMessage("黑方出棋");
        }else {
            gamePage.setMessage("白方出棋");
        }
        gamePage.repaint();
    }

    @HandlerAnno(messageType = Message.RETRACT_CHESS_REQUESTS)
    public void retractChessRequests(RetractChessRequestsMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
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
        new Thread(() -> {
            ctx.writeAndFlush(response);
        }).start();
    }

    @HandlerAnno(messageType = Message.SUE_PEACE_REQUESTS)
    public void suePeaceRequests(SuePeaceRequestsMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
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
        new Thread(() -> {
            ctx.writeAndFlush(response);
        }).start();
    }

    @HandlerAnno(messageType = Message.SUE_PEACE_RESPONSE)
    public void suePeaceResponse(SuePeaceResponseMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        JOptionPane.showMessageDialog(gamePage, "和棋了");
    }

    @HandlerAnno(messageType = Message.CHAT_RESPONSE)
    public void chatResponse(ChatResponseMessage msg, ChannelHandlerContext ctx, GamePage gamePage, JFrame frame) {
        List<Chat> chatHistory = msg.getChatHistory();
        gamePage.getChatArea().setText("");
        chatHistory.forEach(chat -> gamePage.getChatArea().append(chat.toString() + "\n"));
    }
}

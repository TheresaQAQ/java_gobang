package com.zhouq.netty.message.basic;

import com.zhouq.netty.message.requests.*;
import com.zhouq.netty.message.response.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 12:43
 */

@Data
public abstract class Message implements Serializable {
    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;
    private int messageType;
    private int gameId;
    private String msg;

    public abstract int getMessageType();

    public static final int CREATE_GAME_REQUESTS = 0;
    public static final int CREATE_GAME_RESPONSE = 1;
    public static final int JOIN_GAME_REQUESTS = 2;
    public static final int JOIN_GAME_RESPONSE = 3;
    public static final int PLAY_CHESS_REQUESTS = 4;
    public static final int PLAY_CHESS_RESPONSE = 5;
    public static final int RETRACT_CHESS_REQUESTS = 6;
    public static final int RETRACT_CHESS_RESPONSE = 7;
    public static final int SUE_PEACE_REQUESTS = 8;
    public static final int SUE_PEACE_RESPONSE = 9;
    public static final int END_GAME_REQUESTS = 10;
    public static final int END_GAME_RESPONSE = 11;
    public static final int WIN_GAME_RESPONSE = 12;
    public static final int LOSE_GAME_RESPONSE = 13;
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(0, CreateGameRequestsMessage.class);
        messageClasses.put(1, CreateGameResponseMessage.class);
        messageClasses.put(2, JoinGameRequestsMessage.class);
        messageClasses.put(3, JoinGameResponseMessage.class);
        messageClasses.put(4, PlayChessRequestsMessage.class);
        messageClasses.put(5, PlayChessResponseMessage.class);
        messageClasses.put(6, RetractChessRequestsMessage.class);
        messageClasses.put(7, RetractChessResponseMessage.class);
        messageClasses.put(8, SuePeaceRequestsMessage.class);
        messageClasses.put(9, SuePeaceResponseMessage.class);
        messageClasses.put(12, WinGameResponseMessage.class);
        messageClasses.put(13, LoseGameResponseMessage.class);
    }
    //public int CREATE_GAME_REQUESTS = 12;
    //public int CREATE_GAME_RESPONSE = 13;
    //public int CREATE_GAME_REQUESTS = 14;
    //public int CREATE_GAME_RESPONSE = 15;
    //public int CREATE_GAME_REQUESTS = 16;
}

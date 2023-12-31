package com.zhouq.netty.message.requests;

import com.zhouq.netty.message.basic.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:02
 */
@Getter
@Setter
public class PlayChessRequestsMessage extends Message {
    private int mapX;
    private int mapY;
    private int playerType;
    @Override
    public int getMessageType() {
        return PLAY_CHESS_REQUESTS;
    }
}

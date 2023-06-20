package com.zhouq.nio.message.response;

import com.zhouq.core.entity.Game;
import com.zhouq.nio.message.basic.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:05
 */
@Getter
@Setter
public class RetractChessResponseMessage extends Message {
    private boolean flag;
    private int from;
    private int to;
    private Game game;
    @Override
    public int getMessageType() {
        return RETRACT_CHESS_RESPONSE;
    }
}

package com.zhouq.netty.message.response;

import com.zhouq.core.entity.Game;
import com.zhouq.netty.message.basic.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:03
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayChessResponseMessage extends Message {
    private Game game;
    private boolean flag;
    @Override
    public int getMessageType() {
        return PLAY_CHESS_RESPONSE;
    }
}

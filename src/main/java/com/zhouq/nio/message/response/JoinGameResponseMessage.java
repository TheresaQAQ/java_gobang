package com.zhouq.nio.message.response;

import com.zhouq.nio.message.basic.Message;
import lombok.Data;
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
public class JoinGameResponseMessage extends Message {
    private boolean flag;
    private Integer gameId;
    private Integer playerType;
    @Override
    public int getMessageType() {
        return JOIN_GAME_RESPONSE;
    }
}

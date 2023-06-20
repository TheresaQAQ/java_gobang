package com.zhouq.nio.message.requests;

import com.zhouq.nio.message.basic.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:01
 */

@Getter
@Setter
public class JoinGameRequestsMessage extends Message {
    private Integer playerType;
    @Override
    public int getMessageType() {
        return JOIN_GAME_REQUESTS;
    }
}

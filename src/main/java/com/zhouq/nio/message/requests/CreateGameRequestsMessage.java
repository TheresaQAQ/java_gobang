package com.zhouq.nio.message.requests;

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
 * @since 2023/6/18 13:26
 */

@Getter
@Setter
public class CreateGameRequestsMessage extends Message {
    @Override
    public int getMessageType() {
        return CREATE_GAME_REQUESTS;
    }
}
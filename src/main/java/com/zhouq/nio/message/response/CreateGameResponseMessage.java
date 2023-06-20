package com.zhouq.nio.message.response;

import com.zhouq.nio.message.basic.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/18 13:27
 */

@Getter
@Setter
public class CreateGameResponseMessage extends Message {

    private boolean flag;
    @Override
    public int getMessageType() {
        return CREATE_GAME_RESPONSE;
    }
}

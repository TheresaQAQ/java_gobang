package com.zhouq.nio.message.response;

import com.zhouq.nio.message.basic.Message;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 21:46
 */

public class LoseGameResponseMessage extends Message {
    @Override
    public int getMessageType() {
        return LOSE_GAME_RESPONSE;
    }
}

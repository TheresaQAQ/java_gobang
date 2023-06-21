package com.zhouq.netty.message.response;

import com.zhouq.netty.message.basic.Message;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 21:46
 */

public class WinGameResponseMessage extends Message {
    @Override
    public int getMessageType() {
        return WIN_GAME_RESPONSE;
    }
}

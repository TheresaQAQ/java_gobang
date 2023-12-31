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
 * @since 2023/6/19 13:05
 */
@Getter
@Setter
public class RetractChessRequestsMessage extends Message {
    private int from;
    private int to;
    @Override
    public int getMessageType() {
        return RETRACT_CHESS_REQUESTS;
    }
}

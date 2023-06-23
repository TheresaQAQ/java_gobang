package com.zhouq.netty.message.requests;

import com.zhouq.core.entity.Chat;
import com.zhouq.netty.message.basic.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/23 1:57
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestsMessage extends Message {
    private Chat chat;
    @Override
    public int getMessageType() {
        return Message.CHAT_REQUESTS;
    }
}

package com.zhouq.netty.message.response;

import com.zhouq.core.entity.Chat;
import com.zhouq.netty.message.basic.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/23 1:58
 */
@Setter
@Getter
public class ChatResponseMessage extends Message {
    private List<Chat> chatHistory;
    @Override
    public int getMessageType() {
        return Message.CHAT_RESPONSE;
    }
}

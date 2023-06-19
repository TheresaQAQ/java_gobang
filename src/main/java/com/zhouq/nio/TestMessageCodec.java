package com.zhouq.nio;

import com.zhouq.nio.message.MessageCodecSharable;
import com.zhouq.nio.message.requests.CreateGameRequestsMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:31
 */

public class TestMessageCodec {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(
                        1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodecSharable());

        CreateGameRequestsMessage message = new CreateGameRequestsMessage();
        channel.writeOutbound(message);
    }

}

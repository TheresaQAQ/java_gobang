package com.zhouq.nio.client.handler;

import com.zhouq.core.entity.Player;
import com.zhouq.nio.message.requests.JoinGameRequestsMessage;
import com.zhouq.nio.message.response.CreateGameResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 22:00
 */

public class ResponseMessageHandler{
    public static SimpleChannelInboundHandler<CreateGameResponseMessage> CREATE_GAME = new SimpleChannelInboundHandler<>() {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, CreateGameResponseMessage msg) throws Exception {
            JoinGameRequestsMessage message = new JoinGameRequestsMessage();
            message.setGameId(msg.getGameId());
            message.setPlayerType(Player.BLACK_CHESS);
            ctx.writeAndFlush(message);
        }
    };
}

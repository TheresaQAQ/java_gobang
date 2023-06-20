package com.zhouq.nio.message;

import com.alibaba.fastjson.JSON;
import com.zhouq.nio.message.basic.Message;
import com.zhouq.nio.message.requests.SuePeaceRequestsMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * 编解码器
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 13:10
 */
@ChannelHandler.Sharable
@Slf4j
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf,Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> outList) throws Exception {
        ByteBuf out = channelHandlerContext.alloc().buffer();
        //4字节魔数
        out.writeByte(0);
        //1字节的版本
        out.writeByte(1);
        //1字节序列化 0:json序列化
        out.writeByte(0);
        //1字节数据类型
        out.writeByte(message.getMessageType());
        //4字节数据长度
        byte[] bytes = JSON.toJSONString(message).getBytes();
        out.writeInt(bytes.length);
        //内容
        out.writeBytes(bytes);
        outList.add(out);
        if (message instanceof SuePeaceRequestsMessage suePeaceRequestsMessage) {
            log.debug("编码求和请求");
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        ByteBuf magic = in.readBytes(1);
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);
        if (Integer.parseInt(String.valueOf(serializerType))==0) {
            //json
            Message object = JSON.parseObject(bytes, Message.getMessageClass(messageType));
            list.add(object);
            if (object instanceof SuePeaceRequestsMessage suePeaceRequestsMessage) {
                log.debug("解码求和请求");
            }
        }
    }
}

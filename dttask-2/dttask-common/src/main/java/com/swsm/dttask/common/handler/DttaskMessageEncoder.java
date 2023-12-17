package com.swsm.dttask.common.handler;

import com.swsm.dttask.common.model.message.DttaskMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.swsm.dttask.common.util.Constant.MESSAGE_INFO_SIZE;
import static com.swsm.dttask.common.util.Constant.MESSAGE_TYPE_SIZE;

/**
 * @author swsm
 * @date 2023-04-15
 */
public class DttaskMessageEncoder extends MessageToByteEncoder<DttaskMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DttaskMessage dttaskMessage, ByteBuf byteBuf) throws Exception {
        int bodyLength = MESSAGE_TYPE_SIZE + MESSAGE_INFO_SIZE;
        byte[] infoBytes = null;
        if (dttaskMessage.getInfo() != null) {
            infoBytes = dttaskMessage.getInfo().getBytes();
            bodyLength += infoBytes.length;
        }

        byteBuf.writeInt(bodyLength);

        byteBuf.writeByte(dttaskMessage.getType());

        if (infoBytes != null) {
            byteBuf.writeInt(infoBytes.length);
            byteBuf.writeBytes(infoBytes);
        } else {
            byteBuf.writeInt(0x00);
        }

    }
}

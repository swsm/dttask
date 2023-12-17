package com.swsm.dttask.common.handler;

import com.swsm.dttask.common.model.message.DttaskMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import static com.swsm.dttask.common.util.Constant.MESSAGE_TOTAL_SIZE;


/**
 * @author swsm
 * @date 2023-04-15
 */
public class DttaskMessageDecoder extends LengthFieldBasedFrameDecoder {
    public DttaskMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf newIn = (ByteBuf) super.decode(ctx, in);
        if (newIn == null) {
            return null;
        }

        if (newIn.readableBytes() < MESSAGE_TOTAL_SIZE) {
            return null;
        }

        int frameLength = newIn.readInt();
        if (newIn.readableBytes() < frameLength) {
            return null;
        }
        DttaskMessage dttaskMessage = new DttaskMessage();
        byte type = newIn.readByte();
        dttaskMessage.setType(type);

        int infoLength = newIn.readInt();
        byte[] infoBytes = new byte[infoLength];
        newIn.readBytes(infoBytes);
        dttaskMessage.setInfo(new String(infoBytes));


        newIn.release();
        return dttaskMessage;
    }
}

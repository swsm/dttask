package com.swsm.dttask.server.service.message;

import com.swsm.dttask.common.model.message.DttaskMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Component
@Slf4j
public class DefaultMessageService implements IMessageService{
    @Override
    public byte getMessageType() {
        return Byte.MIN_VALUE;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        log.info("有未被明确处理的消息类型，消息内容为:{}", message);
    }
}

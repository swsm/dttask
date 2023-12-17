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
public class PongMessageService implements IMessageService {
    @Override
    public byte getMessageType() {
        return DttaskMessage.PONG;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        log.debug("收到dttask-server 的pong消息：{}", message.getInfo());
    }
}

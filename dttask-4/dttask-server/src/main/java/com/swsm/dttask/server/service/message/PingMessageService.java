package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.PingMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Component
@Slf4j
public class PingMessageService implements IMessageService {
    @Override
    public byte getMessageType() {
        return DttaskMessage.PING;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        PingMessage pingMessage = JSON.parseObject(message.getInfo(), PingMessage.class);
        log.debug("收到dttask-server发来的PING消息:{}", pingMessage);
        ctx.writeAndFlush(DttaskMessage.buildPongMessage(pingMessage.getServerId()));
    }
}

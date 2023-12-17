package com.swsm.dttask.server.service.message;

import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.server.model.ServerInfo;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Component
@Slf4j
public class CommonRespMessageService implements IMessageService {
    @Override
    public byte getMessageType() {
        return DttaskMessage.COMMON_RESP;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        log.info("收到serverId={}的COMMON_RESP信息：{}",
                ServerInfo.getServerIdByChannelId(ctx.channel().id()), message.getInfo());
    }
}

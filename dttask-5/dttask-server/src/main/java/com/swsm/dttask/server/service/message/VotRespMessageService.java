package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.VotRespMessage;
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
public class VotRespMessageService implements IMessageService {
    @Override
    public byte getMessageType() {
        return DttaskMessage.VOT_RESP;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        long localServerId = ServerInfo.getServerId();
        VotRespMessage votRespMessage = JSON.parseObject(message.getInfo(), VotRespMessage.class);
        long controllerServerId = votRespMessage.getServerId();
        log.info("选举出 serverId={}的是controller, 本节点={}是follower", controllerServerId, localServerId);
        ServerInfo.initFollower();
    }
}

package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.ServerRole;
import com.swsm.dttask.common.model.collect.RebalanceJobType;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.NodeOnlineMessage;
import com.swsm.dttask.server.job.RebalanceJobContext;
import com.swsm.dttask.server.model.ServerInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Slf4j
@Component
public class NodeOnlineMessageService implements IMessageService {
    
    @Override
    public byte getMessageType() {
        return DttaskMessage.NODE_ONLINE;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        Channel channel = ctx.channel();
        NodeOnlineMessage nodeOnlineMessage = JSON.parseObject(message.getInfo(), NodeOnlineMessage.class);
        long onlineServerId = nodeOnlineMessage.getServerId();
        ServerInfo.addOtherNode(onlineServerId, channel, ServerRole.FOLLOWER);
        ServerInfo.refreshRedisNodeInfo();
        channel.writeAndFlush(DttaskMessage.buildNodeOnlineRespMessage(onlineServerId));
        ServerInfo.getController().rebalanceJob(new RebalanceJobContext(RebalanceJobType.NODE_ONLINE, onlineServerId));
    }
}

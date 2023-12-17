package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.ServerStatus;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.VotRequestMessage;
import com.swsm.dttask.server.model.Controller;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.model.VotResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Slf4j
@Component
public class VotingMessageService implements IMessageService {

    @Override
    public byte getMessageType() {
        return DttaskMessage.VOTING;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        Channel channel = ctx.channel();
        VotRequestMessage votRequestMessage = JSON.parseObject(message.getInfo(), VotRequestMessage.class);
        long fromServerId = votRequestMessage.getFromServerId();
        Long lastControllerServerId = votRequestMessage.getLastControllerServerId();
        ServerInfo.addOtherNode(fromServerId, channel);
        boolean addRes = ServerInfo.addVotResult(votRequestMessage.getVersion(), votRequestMessage.getServerId());
        if (!addRes) {
            log.info("丢弃以前版本的投票信息={}", votRequestMessage);
            return;
        }
        // 归票
        VotResult votResult = ServerInfo.getVotResult();
        Map<Long, Integer> votMap = votResult.getVotMap();
        for (Map.Entry<Long, Integer> entry : votMap.entrySet()) {
            long controllerServerId = entry.getKey();
            if (votMap.get(controllerServerId) >= ServerInfo.getVotMax()) {
                // 归票成功
                log.info("本节点={}是controller", controllerServerId);
                ServerInfo.setStatus(ServerStatus.RUNNING);
                Controller controller = ServerInfo.initController();
                for (Long otherServerId : ServerInfo.getOtherNodeIds()) {
                    Channel otherNodeChannel = ServerInfo.getChannelByServerId(otherServerId);
                    otherNodeChannel.writeAndFlush(DttaskMessage.buildVotRespMessage(controllerServerId));
                }
                if (lastControllerServerId == null) {
                    controller.allotJob();
                }
                return;
            }
        }
    }
}

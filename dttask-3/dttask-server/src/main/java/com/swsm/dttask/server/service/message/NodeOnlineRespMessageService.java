package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.NodeOnlineRespMessage;
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
public class NodeOnlineRespMessageService implements IMessageService {
    
    @Override
    public byte getMessageType() {
        return DttaskMessage.NODE_ONLINE_RESP;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        NodeOnlineRespMessage nodeOnlineRespMessage = JSON.parseObject(message.getInfo(), NodeOnlineRespMessage.class);
        log.info("收到节点上线的信息={}", nodeOnlineRespMessage);
        ServerInfo.initFollower();
    }
}

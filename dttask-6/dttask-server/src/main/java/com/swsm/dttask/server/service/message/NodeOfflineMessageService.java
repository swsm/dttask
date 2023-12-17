package com.swsm.dttask.server.service.message;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.model.message.NodeOfflineMessage;
import com.swsm.dttask.server.model.NodeInfo;
import com.swsm.dttask.server.model.ServerInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Component
@Slf4j
public class NodeOfflineMessageService implements IMessageService {
    @Override
    public byte getMessageType() {
        return DttaskMessage.NODE_OFFLINE;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        Channel channel = ctx.channel();
        Long remoteServerId = ServerInfo.getServerIdByChannelId(channel.id());
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(remoteServerId);
        if (!nodeInfo.getServerRole().isController()) {
            log.error("只有controller的节点才可以向其它节点发送掉线信息,现发送节点id={}", remoteServerId);
            return;
        }
        NodeOfflineMessage nodeOfflineMessage = JSON.parseObject(message.getInfo(), NodeOfflineMessage.class);
        Integer offlineServerId = nodeOfflineMessage.getServerId();
        ServerInfo.removeNode(offlineServerId);
        channel.writeAndFlush(DttaskMessage.buildCommonRespMessage(CharSequenceUtil.format("删除掉线节点={}成功", offlineServerId), true));
    }
}

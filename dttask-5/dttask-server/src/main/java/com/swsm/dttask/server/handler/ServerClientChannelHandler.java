package com.swsm.dttask.server.handler;

import com.swsm.dttask.common.model.ServerStatus;
import com.swsm.dttask.common.model.collect.RebalanceJobType;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.common.util.RedisUtil;
import com.swsm.dttask.server.job.RebalanceJobContext;
import com.swsm.dttask.server.model.Controller;
import com.swsm.dttask.server.model.NodeInfo;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.service.NetworkService;
import com.swsm.dttask.server.service.message.MessageServiceManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-04-15
 */
@Slf4j
public class ServerClientChannelHandler extends SimpleChannelInboundHandler<DttaskMessage> {


    private NetworkService networkService;
    private MessageServiceManager messageServiceManager;
    private RedisUtil redisUtil;

    public ServerClientChannelHandler(NetworkService networkService, RedisUtil redisUtil, MessageServiceManager messageServiceManager) {
        super();
        this.networkService = networkService;
        this.redisUtil = redisUtil;
        this.messageServiceManager = messageServiceManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive={}", ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DttaskMessage message) throws Exception {
        log.info("收到客户端的请求:{}", message);
        messageServiceManager.chooseMessageService(message.getType()).execute(ctx, message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive...");
        stopChannel(ctx, null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("exceptionCaught...", cause);
        stopChannel(ctx, cause);
    }

    private void stopChannel(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        long localServerId = ServerInfo.getServerId();
        Long serverId = ServerInfo.getServerIdByChannelId(ctx.channel().id());
        if (serverId == null) {
            return;
        }
        if (cause != null) {
            log.error("nodeId={}与本节点id={}通信出现异常", serverId, localServerId, cause);
        } else {
            log.error("nodeId={}与本节点id={}通信失效", serverId, localServerId);
        }
        if (channel.isActive()) {
            channel.close();
        }
        // 判断下线的是follower 还是 controller
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(serverId);
        if (!nodeInfo.getServerRole().isController()) {
            log.info("下线的是follower,id={}", serverId);
            Set<Channel> otherNodeChannels = ServerInfo.getOtherNodeChannel(serverId);
            for (Channel otherNodeChannel : otherNodeChannels) {
                otherNodeChannel.writeAndFlush(DttaskMessage.buildNodeOfflineMessage(serverId));
            }
            ServerInfo.removeChannel(channel.id());
            ServerInfo.refreshRedisNodeInfo();
            ServerInfo.getController().rebalanceJob(new RebalanceJobContext(RebalanceJobType.FOLLOWER_OFFLINE, serverId));
        } else {
            log.info("下线的是controller,id={}", serverId);
            redisUtil.setCacheObject(Constant.RedisConstants.DTTASK_CONTROLLER, null);
            ServerInfo.removeChannel(channel.id());
            long minNodeId = ServerInfo.getMinNodeId();
            if (minNodeId != localServerId) {
                // 重新选举
                networkService.startVote(serverId, minNodeId);
            } else {
                // 当前就只剩自己一个节点
                ServerInfo.setStatus(ServerStatus.RUNNING);
                Controller controller = ServerInfo.initController();
                controller.rebalanceJob(new RebalanceJobContext(RebalanceJobType.CONTROLLER_OFFLINE, serverId));
            }
            ServerInfo.refreshRedisNodeInfo();
        }

    }
}

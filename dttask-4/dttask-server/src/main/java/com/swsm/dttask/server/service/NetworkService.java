package com.swsm.dttask.server.service;

import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.server.model.NodeInfo;
import com.swsm.dttask.server.model.ServerInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-05
 */
@Component
@Slf4j
public class NetworkService {


    public void connectController(long controllerServerId) {
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(controllerServerId);
        doConnectController(nodeInfo);
    }

    private void doConnectController(NodeInfo nodeInfo) {
        try {
            long controllerServerId = nodeInfo.getServerId();
            long localServerId = ServerInfo.getServerId();
            ChannelFuture connect = ServerInfo.getConnectOtherNodeBootStrap().connect(nodeInfo.getIp(),
                    nodeInfo.getPort()).sync();
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("连接controller={} 成功...", nodeInfo);
                    Channel channel = future.channel();
                    ServerInfo.cacheChannelAnsServerIdRel(controllerServerId, channel);
                    DttaskMessage dttaskMessage = DttaskMessage.buildNodeOnlineMessage(localServerId);
                    log.info("向Node ip-port={},发送NodeOnlineMessage={}", nodeInfo, dttaskMessage);
                    channel.writeAndFlush(dttaskMessage);
                } else {
                    log.info("连接controller={} 失败...", nodeInfo);
                }
            });
        } catch (Exception e) {
            log.error("连接controller节点={}出现异常", nodeInfo, e);
        }
        
    }
    
    private int getVotVersion() {
        return Constant.VOTE_VERSION;
    }

    public void startVote(Long lastControllerServerId, long minServerId) {
        long localServerId = ServerInfo.getServerId();
        int votVersion = getVotVersion();
        if (localServerId < minServerId) {
            log.info("当前节点是最小的，等待别人来选举它");
            return;
        }
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(minServerId);
        doVote(lastControllerServerId, votVersion, minServerId, localServerId, nodeInfo);
    }

    private void doVote(Long lastControllerServerId, int votVersion,
                        long minServerId, long localServerId,
                        NodeInfo nodeInfo) {
        boolean res = false;
        while(true) {
            try {
                ChannelFuture connect = ServerInfo.getConnectOtherNodeBootStrap().connect(nodeInfo.getIp(),
                        nodeInfo.getPort()).sync();
                connect.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info("连接dttask-server serverId={} 成功...", minServerId);
                        // 向服务端发送 客户端id信息 
                        Channel channel = future.channel();
                        ServerInfo.cacheChannelAnsServerIdRel(minServerId, channel);
                        DttaskMessage dttaskMessage = DttaskMessage.buildVotRequestMessage(
                                lastControllerServerId, localServerId, minServerId, votVersion);
                        log.info("向Node={},发送VotRequestMessage={}", nodeInfo, dttaskMessage);
                        channel.writeAndFlush(dttaskMessage);
                    } else {
                        log.info("连接dttask-server id={} 失败...", minServerId);
                    }
                });
                res = true;
                break;
            } catch (Exception e) {
                log.error("连接节点={}出现异常={}", minServerId, e.getMessage());
            } finally {
                if (!res) {
                    try {
                        Thread.sleep(3L * 1000);
                    } catch (InterruptedException e) {
                        log.error("连接节点={}出现InterruptedException异常={}", minServerId, e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

}

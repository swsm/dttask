package com.swsm.dttask.server.model;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.swsm.dttask.common.exception.BusinessException;
import com.swsm.dttask.common.model.ServerRole;
import com.swsm.dttask.common.model.ServerStatus;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.common.util.RedisUtil;
import com.swsm.dttask.server.BeanUseHelper;
import com.swsm.dttask.server.config.DttaskServerConfig;
import com.swsm.dttask.server.job.JobAllotManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-04
 */
@Slf4j
public class ServerInfo {
    
    private ServerInfo() {}
    
    private static NioEventLoopGroup bossGroup;
    public static void setBossGroup(NioEventLoopGroup bg) {
        bossGroup = bg;
    }
    public static NioEventLoopGroup getBossGroup() {
        return bossGroup;
    }
    private static NioEventLoopGroup workerGroup;
    private static Channel serverChannel;
    private static Bootstrap connectOtherNodeBootStrap;
    private static ServerBootstrap bootstrapForClient;

    private static NodeInfo myNodeInfo;
        private static Map<Long, Channel> nodeChannelMap = new ConcurrentHashMap<>();
    private static Map<ChannelId, Long> nodeChannelServerIdMap = new ConcurrentHashMap<>();
    private static volatile ServerStatus status;
    private static Map<Long, NodeInfo> otherNodeInfoMap = new ConcurrentHashMap<>();
    private static VotResult votResult = new VotResult();

    private static Controller controller;
    private static Follower follower;


    public static void init() {
        RedisUtil redisUtil = BeanUseHelper.redisUtil();
        DttaskServerConfig dttaskServerConfig = BeanUseHelper.dttaskServerConfig();
        long localServerId = dttaskServerConfig.getServerId();
        ServerInfo.setStatus(ServerStatus.STARTING);
        Long controllerServerId = redisUtil.getLongValue(Constant.RedisConstants.DTTASK_CONTROLLER);
        if (controllerServerId == null) {
            log.info("当前启动状态为：未确定controller");
            initNodeInfoByConfig();
            Long minServerId = ServerInfo.getMinNodeId();
            if (minServerId == localServerId) {
                log.info("就当前一个节点:{},此节点就是controller", localServerId);
                ServerInfo.setStatus(ServerStatus.RUNNING);
            } else {
                log.info("有多个节点，节点状态应为VOTING");
                ServerInfo.setStatus(ServerStatus.VOTING);
            }
        } else {
            log.info("当前启动状态为：已确定controller");
            ServerInfo.refreshNodeInfoByRedis();
            InetSocketAddress address = dttaskServerConfig.getServerInfoMap().get(localServerId);
            ServerInfo.setMyNodeInfo(localServerId, address.getHostString(), address.getPort(), null);
            ServerInfo.setStatus(ServerStatus.IDENTIFYING);
        }

    }

    private static void initNodeInfoByConfig() {
        DttaskServerConfig dttaskServerConfig = BeanUseHelper.dttaskServerConfig();
        long localServerId = dttaskServerConfig.getServerId();
        Map<Long, InetSocketAddress> serverInfoMap = dttaskServerConfig.getServerInfoMap();
        for (Map.Entry<Long, InetSocketAddress> entry : serverInfoMap.entrySet()) {
            long id = entry.getKey();
            InetSocketAddress address = serverInfoMap.get(id);
            if (localServerId != id) {
                ServerInfo.addOtherNode(id, address.getHostString(), address.getPort());
            } else {
                ServerInfo.setMyNodeInfo(localServerId, address.getHostString(), address.getPort(), null);
            }
        }
    }

    public static Controller initController() {
        long localServerId = ServerInfo.getServerId();
        RedisUtil redisUtil = BeanUseHelper.redisUtil();
        log.info("初始化本节点={}controller信息...", ServerInfo.getServerId());
        ServerInfo.setRole(ServerRole.CONTROLLER);
        redisUtil.setCacheObject(Constant.RedisConstants.DTTASK_CONTROLLER, localServerId);
        ServerInfo.setOtherNodeRole(localServerId);
        ServerInfo.refreshRedisNodeInfo();
        controller = Controller.getInstance(JobAllotManager.getStrategy());
        return controller;
    }

    public static Follower initFollower() {
        log.info("初始化本节点={}follower信息...", ServerInfo.getServerId());
        RedisUtil redisUtil = BeanUseHelper.redisUtil();
        Long controllerServerId = redisUtil.getLongValue(Constant.RedisConstants.DTTASK_CONTROLLER);
        if (controllerServerId == null) {
            log.error("init follower时，controller还没有确定...");
            throw new BusinessException("init follower时，controller还没有确定...");
        }
        ServerInfo.setRole(ServerRole.FOLLOWER);
        ServerInfo.setStatus(ServerStatus.RUNNING);
        ServerInfo.setOtherNodeRole(controllerServerId);
        follower = Follower.getInstance();
        return follower;
    }

    public static void setMyNodeInfo(long serverId, String ip, int port, ServerRole serverRole) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setServerId(serverId);
        nodeInfo.setIp(ip);
        nodeInfo.setPort(port);
        nodeInfo.setServerRole(serverRole);
        myNodeInfo = nodeInfo;
    }


    public static long getServerId() {
        return myNodeInfo.getServerId();
    }

    public static NodeInfo getMyNodeInfo() {
        return myNodeInfo;
    }

    public static Map<Long, NodeInfo> getOtherNodeInfoMap() {
        return otherNodeInfoMap;
    }

    public static int getVotMax() {
        return otherNodeInfoMap.size();
    }

    public static VotResult getVotResult() {
        return votResult;
    }

    public static synchronized void cacheChannelAnsServerIdRel(long serverId, Channel channel) {
        nodeChannelMap.put(serverId, channel);
        nodeChannelServerIdMap.put(channel.id(), serverId);

    }

    public static Channel getChannelByServerId(Long serverId) {
        return nodeChannelMap.get(serverId);
    }

    public static synchronized void removeChannel(ChannelId channelId) {
        Long serverId = nodeChannelServerIdMap.get(channelId);
        if (serverId != null) {
            log.info("删除和节点id={}的连接", serverId);
            nodeChannelServerIdMap.remove(channelId);
            nodeChannelMap.remove(serverId);
            otherNodeInfoMap.remove(serverId);
        }
    }

    public static synchronized void refreshRedisNodeInfo() {
        if (myNodeInfo != null && getRole() != null && getRole().isController()) {
            RedisUtil redisUtil = BeanUseHelper.redisUtil();
            Map<Long, NodeInfo> otherNodeInfoMap = ServerInfo.getOtherNodeInfoMap();
            List<NodeInfo> nodeInfoList = new ArrayList<>();
            nodeInfoList.addAll(otherNodeInfoMap.values());
            nodeInfoList.add(ServerInfo.getMyNodeInfo());
            log.info("controller刷新节点信息到redis:{}", nodeInfoList);
            redisUtil.setCacheObject(Constant.RedisConstants.DTTASK_NODE_INFO, JSON.toJSONString(nodeInfoList));
        }
    }



    public static Long getServerIdByChannelId(ChannelId channelId) {
        return nodeChannelServerIdMap.get(channelId);
    }

    public static synchronized boolean addVotResult(int version, long chooseServerId) {
        Integer curVersion = votResult.getVersion();
        if (version < votResult.getVersion()) {
            log.info("版本={}已失效,当前的为:{}", version, curVersion);
            return false;
        }
        votResult.setVersion(version);
        if (votResult.getVotMap().containsKey(chooseServerId)) {
            votResult.getVotMap().put(chooseServerId, votResult.getVotMap().get(chooseServerId) + 1);
        } else {
            votResult.getVotMap().put(chooseServerId, 1);
        }
        return true;
    }

    public static List<NodeInfo> getNodeInfoList() {
        Map<Long, NodeInfo> otherNodeInfoMap = ServerInfo.getOtherNodeInfoMap();
        NodeInfo myNodeInfo = ServerInfo.getMyNodeInfo();
        List<NodeInfo> nodeInfoList = new ArrayList<>();
        nodeInfoList.addAll(otherNodeInfoMap.values());
        nodeInfoList.add(myNodeInfo);
        return nodeInfoList;
    }

    public static void addOtherNode(long serverId, String ip, int port) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setServerId(serverId);
        nodeInfo.setIp(ip);
        nodeInfo.setPort(port);
        otherNodeInfoMap.put(serverId, nodeInfo);
    }
    public static void addOtherNode(long serverId, Channel channel) {
        addOtherNode(serverId, channel, null);
    }

    public static void addOtherNode(long serverId, Channel channel, ServerRole serverRole) {
        InetSocketAddress address = BeanUseHelper.dttaskServerConfig().getServerInfoMap().get(serverId);
        if (address == null) {
            throw new BusinessException(CharSequenceUtil.format("id={}的没有配置在文件中", serverId));
        }
        if (channel != null && ServerInfo.getServerIdByChannelId(channel.id()) == null) {
            ServerInfo.cacheChannelAnsServerIdRel(serverId, channel);
        }
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setServerId(serverId);
        nodeInfo.setIp(address.getHostString());
        nodeInfo.setPort(address.getPort());
        nodeInfo.setServerRole(serverRole);
        otherNodeInfoMap.put(serverId, nodeInfo);
    }

    public static NodeInfo getNodeInfo(long serverId) {
        return otherNodeInfoMap.get(serverId);
    }

    public static Set<Long> getOtherNodeIds() {
        return otherNodeInfoMap.keySet();
    }

    public static long getMinNodeId() {
        if (getOtherNodeIds().isEmpty()) {
            return ServerInfo.getServerId();
        }
        return Collections.min(getOtherNodeIds());
    }

    public static void setOtherNodeRole(long controllerServerId) {
        for (Map.Entry<Long, NodeInfo> entry : otherNodeInfoMap.entrySet()) {
            long serverId = entry.getKey();
            if (serverId == controllerServerId) {
                otherNodeInfoMap.get(serverId).setServerRole(ServerRole.CONTROLLER);
            } else {
                otherNodeInfoMap.get(serverId).setServerRole(ServerRole.FOLLOWER);
            }
        }
    }

    public static ServerRole getRole() {
        return myNodeInfo.getServerRole();
    }

    public static void setStatus(ServerStatus s) {
        status = s;
    }

    public static void setRole(ServerRole r) {
        myNodeInfo.setServerRole(r);
    }

    public static Set<Channel> getOtherNodeChannel(Long serverId) {
        Set<Channel> res = new HashSet<>();
        for (Map.Entry<Long, Channel> entry : nodeChannelMap.entrySet()) {
            long id = entry.getKey();
            if (!Objects.equals(serverId, id)) {
                res.add(nodeChannelMap.get(id));
            }
        }
        return res;
    }

    /**
     * 这个方法针对，本节点并没有和要断开节点有连接的
     * @param offlineServerId 掉线的节点id
     */
    public static void removeNode(long offlineServerId) {
        log.info("删除掉线节点id={}", offlineServerId);
        otherNodeInfoMap.remove(offlineServerId);
    }

    public static void refreshNodeInfoByRedis() {
        RedisUtil redisUtil = BeanUseHelper.redisUtil();
        Object obj = redisUtil.getCacheObject(Constant.RedisConstants.DTTASK_NODE_INFO);
        if (obj != null) {
            List<NodeInfo> nodeInfoList = JSON.parseObject(obj.toString(),
                    new TypeReference<List<NodeInfo>>() {}.getType());
            for (NodeInfo nodeInfo : nodeInfoList) {
                otherNodeInfoMap.put(nodeInfo.getServerId(), nodeInfo);
            }
        }
    }

    public static boolean isIdentifying() {
        return status.isIdentifying();
    }

    public static boolean isVoting() {
        return status.isVoting();
    }

    public static boolean isRunning() {
        return status.isRunning();
    }

    
    
    public static void setWorkerGroup(NioEventLoopGroup bg) {
        workerGroup = bg;
    }
    public static NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public static void setServerChannel(Channel ch) {
        serverChannel = ch;
    }
    public static Channel getServerChannel() {
        return serverChannel;
    }
    public static void setConnectOtherNodeBootStrap(Bootstrap bs) {
        connectOtherNodeBootStrap = bs;
    }
    public static Bootstrap getConnectOtherNodeBootStrap() {
        return connectOtherNodeBootStrap;
    }

    public static void setBootstrapForClient(ServerBootstrap sbs) {
        bootstrapForClient = sbs;
    }
    public static ServerBootstrap getBootstrapForClient() {
        return bootstrapForClient;
    }

}

package com.swsm.dttask.server.model;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
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

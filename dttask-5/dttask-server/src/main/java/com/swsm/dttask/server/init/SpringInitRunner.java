package com.swsm.dttask.server.init;

import com.swsm.dttask.common.handler.DttaskMessageDecoder;
import com.swsm.dttask.common.handler.DttaskMessageEncoder;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.common.util.RedisUtil;
import com.swsm.dttask.server.BeanUseHelper;
import com.swsm.dttask.server.config.DttaskServerConfig;
import com.swsm.dttask.server.handler.HeartBeatServerHandler;
import com.swsm.dttask.server.handler.ServerClientChannelHandler;
import com.swsm.dttask.server.model.Controller;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.service.NetworkService;
import com.swsm.dttask.server.service.message.MessageServiceManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.swsm.dttask.common.util.Constant.MESSAGE_LENGTH_FILED_LENGTH;
import static com.swsm.dttask.common.util.Constant.MESSAGE_LENGTH_FILED_OFFSET;
import static com.swsm.dttask.common.util.Constant.MESSAGE_MAX_SIZE;

/**
 * @author swsm
 * @date 2023-11-21
 */
@Component
@Slf4j
public class SpringInitRunner implements CommandLineRunner {

    @Autowired
    private DttaskServerConfig dttaskServerConfig;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private MessageServiceManager messageServiceManager;
    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        initServerBootStrap();
        initConnectOtherNodeBootStrap();
    }

    private void initConnectOtherNodeBootStrap() {
        ServerInfo.setConnectOtherNodeBootStrap(new Bootstrap());
        ServerInfo.getConnectOtherNodeBootStrap().group(new NioEventLoopGroup(4))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(
                                new DttaskMessageDecoder(MESSAGE_MAX_SIZE, MESSAGE_LENGTH_FILED_OFFSET, MESSAGE_LENGTH_FILED_LENGTH));
                        socketChannel.pipeline().addLast(
                                new IdleStateHandler(dttaskServerConfig.getReadIdleSecondTime(),
                                        dttaskServerConfig.getWriteIdleSecondTime(),
                                        dttaskServerConfig.getAllIdleSecondTime()));
                        socketChannel.pipeline().addLast(new DttaskMessageEncoder());
                        socketChannel.pipeline().addLast(
                                new ServerClientChannelHandler(networkService, redisUtil, messageServiceManager));
                    }
                });
    }

    private void initServerBootStrap() {
        ServerInfo.setBossGroup(new NioEventLoopGroup(4));
        ServerInfo.setWorkerGroup(new NioEventLoopGroup(8));
        ServerInfo.setBootstrapForClient(new ServerBootstrap());
        ServerInfo.getBootstrapForClient().group(ServerInfo.getBossGroup(), ServerInfo.getWorkerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(
                                new DttaskMessageDecoder(MESSAGE_MAX_SIZE, MESSAGE_LENGTH_FILED_OFFSET, MESSAGE_LENGTH_FILED_LENGTH));
                        socketChannel.pipeline().addLast(new DttaskMessageEncoder());
                        IdleStateHandler idleStateHandler = new IdleStateHandler(dttaskServerConfig.getReadIdleSecondTime(), dttaskServerConfig.getWriteIdleSecondTime(), dttaskServerConfig.getAllIdleSecondTime());
                        socketChannel.pipeline().addLast(idleStateHandler);
                        socketChannel.pipeline().addLast(new HeartBeatServerHandler());
                        socketChannel.pipeline().addLast(
                                new ServerClientChannelHandler(networkService, redisUtil, messageServiceManager));
                    }
                });
    }

    @Override
    public void run(String... args) {
        log.info("spring启动完成，接下来启动 netty");
        ServerInfo.init();
        try {
            log.info("启动监听其它节点端请求的服务端...");
            ServerInfo.setServerChannel(ServerInfo.getBootstrapForClient().bind(dttaskServerConfig.listenerPort()).sync().channel());
        } catch (Exception e) {
            log.error("启动 监听其它节点请求的服务端出现异常", e);
            System.exit(-1);
        }
        try {
            log.info("连接controller或开始vote...");
            if (ServerInfo.isIdentifying()) {
                log.info("连接controller...");
                RedisUtil redisUtil = BeanUseHelper.redisUtil();
                long controllerServerId = redisUtil.getLongValue(Constant.RedisConstants.DTTASK_CONTROLLER);
                networkService.connectController(controllerServerId);
            } else if (ServerInfo.isVoting()){
                log.info("开始vote...");
                long minNodeId = ServerInfo.getMinNodeId();
                networkService.startVote(null, minNodeId);
            } else if (ServerInfo.isRunning()) {
                log.info("已确认本节点={}就是controller...", ServerInfo.getServerId());
                Controller controller = ServerInfo.initController();
                controller.allotJob();
            }
        }  catch (Exception e) {
            log.error("连接controller或开始vote出现异常", e);
            System.exit(-1);
        }
        log.info("netty 启动成功...");
    }

    @PreDestroy
    public void shutdown() {
        if (ServerInfo.getOtherNodeIds().isEmpty()) {
            redisUtil.setCacheObject(Constant.RedisConstants.DTTASK_CONTROLLER, null);
            redisUtil.setCacheObject(Constant.RedisConstants.DTTASK_NODE_INFO, null);
        }
        try {
            ServerInfo.getServerChannel().close().sync();
        } catch (InterruptedException e) {
            log.error("dttask-server netty shutdown 出现异常", e);
            Thread.currentThread().interrupt();
        } finally {
            ServerInfo.getWorkerGroup().shutdownGracefully();
            ServerInfo.getBossGroup().shutdownGracefully();
        }
    }
}

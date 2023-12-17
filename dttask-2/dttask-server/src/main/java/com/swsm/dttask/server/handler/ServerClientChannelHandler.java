package com.swsm.dttask.server.handler;

import com.swsm.dttask.common.model.message.DttaskMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-04-15
 */
@Slf4j
public class ServerClientChannelHandler extends SimpleChannelInboundHandler<DttaskMessage> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive={}", ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DttaskMessage message) throws Exception {
        log.info("收到客户端的请求:{}", message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("exceptionCaught...", cause);
    }


}

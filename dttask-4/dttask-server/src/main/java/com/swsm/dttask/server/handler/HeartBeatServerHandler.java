package com.swsm.dttask.server.handler;

import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.server.model.ServerInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {  
    
    @Override  
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
        if (evt instanceof IdleStateEvent) {  
            IdleStateEvent event = (IdleStateEvent) evt;  
            if (event.state() == IdleState.READER_IDLE) {  
                log.warn("读取空闲...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.warn("写入空闲...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Long serverId = ServerInfo.getServerIdByChannelId(ctx.channel().id());
                log.warn("serverId={}与server通信读取或写入空闲...", serverId);
                if (serverId != null) {
                    ctx.writeAndFlush(DttaskMessage.buildPingMessage(serverId));
                } else {
                    ctx.close();
                }
            }
        }  
    }  
}

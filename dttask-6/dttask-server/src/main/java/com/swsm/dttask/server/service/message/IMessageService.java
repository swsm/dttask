package com.swsm.dttask.server.service.message;

import com.swsm.dttask.common.model.message.DttaskMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author swsm
 * @date 2023-12-07
 */
public interface IMessageService {
    
    byte getMessageType();
    
    void execute(ChannelHandlerContext ctx, DttaskMessage message);
    
}

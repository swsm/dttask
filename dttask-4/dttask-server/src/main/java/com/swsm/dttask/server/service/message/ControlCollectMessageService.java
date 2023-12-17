package com.swsm.dttask.server.service.message;

import com.alibaba.fastjson.JSON;
import com.swsm.dttask.common.model.message.ControlCollectMessage;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.protocol.CollectDataService;
import io.netty.channel.ChannelHandlerContext;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-08
 */
@Component
@Slf4j
public class ControlCollectMessageService implements IMessageService {
    
    @Autowired
    private CollectDataService collectDataService;
    
    @Override
    public byte getMessageType() {
        return DttaskMessage.CONTROL_COLLECT;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, DttaskMessage message) {
        ControlCollectMessage controlCollectMessage = JSON.parseObject(message.getInfo(), ControlCollectMessage.class);
        log.info("收到ControlCollectMessage={}", controlCollectMessage);
        Set<Long> dttaskJobIds = controlCollectMessage.getDttaskJobIds();
        if (!Objects.equals(controlCollectMessage.getDttaskId(), ServerInfo.getServerId())) {
            log.error("收到了不属于自己的ControlCollectMessage消息={}", controlCollectMessage);
            return;
        }
        if (controlCollectMessage.isStart()) {
            // 启动采集
            collectDataService.startCollectData(dttaskJobIds);
        } else {
            // 关闭采集
            collectDataService.stopCollectData(dttaskJobIds);
        }
        
    }
}

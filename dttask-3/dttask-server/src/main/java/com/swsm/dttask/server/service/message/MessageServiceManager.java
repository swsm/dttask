package com.swsm.dttask.server.service.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Slf4j
@Component
public class MessageServiceManager {
    
    @Autowired(required = false)
    private List<IMessageService> messageServices;
    
    private Map<Byte, IMessageService> messageServiceMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        if (messageServices != null) {
            for (IMessageService messageService : messageServices) {
                messageServiceMap.put(messageService.getMessageType(), messageService);
            }
        }
    }
    
    public IMessageService chooseMessageService(byte messageType) {
        if (messageServiceMap.containsKey(messageType)) {
            return messageServiceMap.get(messageType);
        }
        return messageServiceMap.get(Byte.MIN_VALUE);
    }
    
}

package com.swsm.dttask.server.protocol;

import cn.hutool.extra.spring.SpringUtil;
import com.swsm.dttask.common.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-11
 */
@Slf4j
@Component
public class ProtocolManager {
    
    @Autowired
    private List<IProtocol> protocolList;
    
    private static Map<Integer, IProtocol> protocolMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        for (IProtocol protocol : protocolList) {
            protocolMap.put(protocol.getType(),protocol);
        }
    }
    
    public synchronized void refreshMap() {
        Map<String, IProtocol> map = SpringUtil.getBeansOfType(IProtocol.class);
        protocolMap.clear();
        for (IProtocol protocol : map.values()) {
            protocolMap.put(protocol.getType(), protocol);
        }
    }
    
    public static IProtocol getProtocol(int type) {
        if (protocolMap.containsKey(type)) {
            return protocolMap.get(type);
        }
        
        throw new BusinessException("type=" + type + "不正确");
    }
    
}

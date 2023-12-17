package com.swsm.dttask.server.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.swsm.dttask.common.exception.BusinessException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author swsm
 * @date 2023-04-15
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "dttask.server")
public class DttaskServerConfig {

    private Long serverId;
    private String serverInfo;
    private Map<Long, InetSocketAddress> serverInfoMap = new HashMap<>();
    private Integer readIdleSecondTime;
    private Integer writeIdleSecondTime;
    private Integer allIdleSecondTime;
    private Integer allotJobStrategyType;
    private Integer mqttPort = 7789;
    private String topicPrefix;
    private String topicSuffix;


    @PostConstruct
    public void init() {
        if (CharSequenceUtil.isEmpty(serverInfo)) {
            throw new BusinessException("serverInfo config null");
        }
        String[] serverInfoArray = serverInfo.split(";");
        if (serverInfoArray.length == 0) {
            throw new BusinessException("serverInfo config not null but empty");
        }
        Set<Long> serverIds = new HashSet<>();
        for (String s : serverInfoArray) {
            String[] serverArray = s.split(":");
            if (serverArray.length != 3) {
                throw new BusinessException("serverInfo config error,length must 3");
            }
            long sId = Long.parseLong(serverArray[2]);
            if (serverIds.contains(sId)) {
                throw new BusinessException("serverInfo config wrong, id duplicate");
            }
            serverInfoMap.put(sId,
                    new InetSocketAddress(serverArray[0], Integer.parseInt(serverArray[1])));
        }
        if (!serverInfoMap.containsKey(serverId)) {
            throw new BusinessException("serverInfo config wrong, id not exist");
        }
    }

    public Integer listenerPort() {
        return serverInfoMap.get(serverId).getPort();
    }

}

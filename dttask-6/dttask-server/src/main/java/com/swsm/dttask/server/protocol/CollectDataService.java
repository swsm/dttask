package com.swsm.dttask.server.protocol;

import com.swsm.dttask.protocol.sdk.service.ICollectDataWorker;
import com.swsm.dttask.server.BeanUseHelper;
import com.swsm.dttask.server.model.ServerInfo;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-11-23
 */
@Component
@Slf4j
public class CollectDataService {
    
    @Autowired
    private ICollectDataWorker collectDataWorker;
    
    private CollectDataService() {
    }

    /**
     * 开始采集数据
     */
    public void startCollectData(Set<Long> dttaskJobId) {
        BeanUseHelper.entityHelpService().runDttaskJob(dttaskJobId, ServerInfo.getServerId());
        collectDataWorker.doCollect(dttaskJobId);
    }
    
    public void stopCollectData(Set<Long> dttaskJobId) {
        collectDataWorker.stopCollect(dttaskJobId);
        BeanUseHelper.entityHelpService().stopDttaskJob(dttaskJobId, ServerInfo.getServerId());
    }
    
    
    
}

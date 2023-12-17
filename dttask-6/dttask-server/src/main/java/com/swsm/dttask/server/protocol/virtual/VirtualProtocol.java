package com.swsm.dttask.server.protocol.virtual;

import com.alibaba.fastjson.JSONObject;
import com.swsm.dttask.protocol.sdk.model.ProtocolContext;
import com.swsm.dttask.protocol.sdk.service.AbstractProtocol;
import com.swsm.dttask.protocol.sdk.service.ICollectDataWorker;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-11
 */
@Data
@Slf4j
@Component
public class VirtualProtocol extends AbstractProtocol {

    private VirtualSpecModel virtualSpecModel;
    @Autowired
    private ICollectDataWorker collectDataWorker;

    @Override
    public int getType() {
        return -1;
    }
    
    @Override
    public void doParseConfig(ProtocolContext protocolContext) {
        JSONObject jsonObject = protocolContext.getParam();
        this.virtualSpecModel = VirtualSpecModel.getFromParam(jsonObject);
    }

    @Override
    public void doStart() {
        try {
            VirtualCollectWorker worker = new VirtualCollectWorker(
                    dttaskId, dttaskJobId, deviceId, virtualSpecModel);
            collectDataWorker.addCollectTask(dttaskJobId, worker, 5, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("DttaskId={}采集config={}出现异常", dttaskId, protocolContext, e);
        }
    }
}

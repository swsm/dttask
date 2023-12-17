package com.swsm.dttask.protocol.sdk;

import com.alibaba.fastjson.JSONObject;
import com.swsm.dttask.protocol.sdk.model.ProtocolContext;
import com.swsm.dttask.protocol.sdk.model.ProtocolType;
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
public class SimulatorProtocol extends AbstractProtocol {

    private SimulatorSpecModel simulatorSpecModel;
    @Autowired
    private ICollectDataWorker collectDataWorker;

    @Override
    public int getType() {
        return ProtocolType.SIMULATOR.getCode();
    }
    
    @Override
    public void doParseConfig(ProtocolContext protocolContext) {
        JSONObject jsonObject = protocolContext.getParam();
        this.simulatorSpecModel = SimulatorSpecModel.getFromParam(jsonObject);
    }

    @Override
    public void doStart() {
        try {
            SimulatorCollectWorker worker = new SimulatorCollectWorker(
                    dttaskId, dttaskJobId, deviceId, simulatorSpecModel);
            collectDataWorker.addCollectTask(dttaskJobId, worker, 5, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("DttaskId={}采集config={}出现异常", dttaskId, protocolContext, e);
        }
    }
}

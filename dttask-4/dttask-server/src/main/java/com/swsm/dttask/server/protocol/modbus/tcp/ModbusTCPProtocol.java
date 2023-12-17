package com.swsm.dttask.server.protocol.modbus.tcp;

import com.alibaba.fastjson.JSONObject;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.server.protocol.AbstractProtocol;
import com.swsm.dttask.server.protocol.ICollectDataWorker;
import com.swsm.dttask.server.protocol.ProtocolContext;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Component
@Slf4j
public class ModbusTCPProtocol extends AbstractProtocol {
    
    private ModbusTCPSpecModel modbusTCPSpecModel;
    @Autowired
    private ICollectDataWorker collectDataWorker;
    
    @Override
    public int getType() {
        return Constant.EntityConstants.LINK_TYPE_MODBUSTCP;
    }

    @Override
    protected void doParseConfig(ProtocolContext protocolContext) {
        JSONObject param = protocolContext.getParam();
        modbusTCPSpecModel = ModbusTCPSpecModel.getFromParam(param);
    }

    @Override
    public void doStart() {
        try {
            ModbusTCPMaster modbusTCPMaster = new ModbusTCPMaster(modbusTCPSpecModel.getIp(), modbusTCPSpecModel.getPort(), 5, true);
            modbusTCPMaster.connect();
            ModbusTCPCollectWorker worker = new ModbusTCPCollectWorker(
                    this.dttaskId, this.dttaskJobId, deviceId, modbusTCPSpecModel, modbusTCPMaster);
            collectDataWorker.addCollectTask(dttaskJobId, worker, 5, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("DttaskId={}采集config={}出现异常", dttaskId, modbusTCPSpecModel, e);
        }
    }
}

package com.swsm.dttask.server.protocol.modbus.rtu;

import com.alibaba.fastjson.JSONObject;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import com.swsm.dttask.server.protocol.AbstractProtocol;
import com.swsm.dttask.server.protocol.ICollectDataWorker;
import com.swsm.dttask.server.protocol.ProtocolContext;
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
public class ModbusRTUProtocol extends AbstractProtocol {

    private ModbusRTUSpecModel modbusRTUSpecModel;
    @Autowired
    private ICollectDataWorker collectDataWorker;
    
    @Override
    public void doParseConfig(ProtocolContext protocolContext) {
        JSONObject jsonObject = protocolContext.getParam();
        this.modbusRTUSpecModel = ModbusRTUSpecModel.getFromParam(jsonObject);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void doStart() {
        try {
            SerialParameters serialParameters = new SerialParameters();
            serialParameters.setPortName(modbusRTUSpecModel.getPortName());
            serialParameters.setBaudRate(modbusRTUSpecModel.getBaudRate());
            serialParameters.setDatabits(modbusRTUSpecModel.getDatabits());
            serialParameters.setStopbits(modbusRTUSpecModel.getStopbits());
            serialParameters.setParity(modbusRTUSpecModel.getParity());
            serialParameters.setEncoding(modbusRTUSpecModel.getEncoding());
            serialParameters.setEcho(modbusRTUSpecModel.getEcho());
            serialParameters.setPortName(modbusRTUSpecModel.getPortName());

            ModbusSerialMaster modbusSerialMaster = new ModbusSerialMaster(serialParameters);
            modbusSerialMaster.connect();

            ModbusRTUCollectWorker modbusRTUCollectWorker = new ModbusRTUCollectWorker(
                    this.dttaskId, this.dttaskJobId, deviceId, modbusSerialMaster, modbusRTUSpecModel);
            collectDataWorker.addCollectTask(dttaskJobId, modbusRTUCollectWorker, 5, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("DttaskId={}采集config={}出现异常", this.dttaskId, protocolContext, e);
        }
    }
}

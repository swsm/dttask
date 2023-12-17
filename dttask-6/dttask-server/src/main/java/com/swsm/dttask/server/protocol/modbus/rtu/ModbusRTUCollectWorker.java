package com.swsm.dttask.server.protocol.modbus.rtu;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusRTUCollectWorker implements Runnable {

    private Long dttaskId;
    private Long dttaskJobId;
    private Long deviceId;
    private ModbusSerialMaster master;
    private ModbusRTUSpecModel modbusRTUSpecModel;
    private Map<String, Long> lastReadMap = new HashMap<>();

    public ModbusRTUCollectWorker(Long dttaskId, Long dttaskJobId, Long deviceId, ModbusSerialMaster master, ModbusRTUSpecModel modbusRTUSpecModel) {
        this.dttaskId = dttaskId;
        this.dttaskJobId = dttaskJobId;
        this.deviceId = deviceId;
        this.master = master;
        this.modbusRTUSpecModel = modbusRTUSpecModel;
    }

    @Override
    public void run() {
        long current = new Date().getTime();
        for (ModbusRTUSpecModel.PointDetail pointDetail : modbusRTUSpecModel.getPointDetailList()) {
            String key = pointDetail.getKey();
            if (lastReadMap.containsKey(key) &&
                    (current - lastReadMap.get(key)) % pointDetail.getSamplingInterval() >= 1) {
                try {
                    Register[] registers = master.readMultipleRegisters(modbusRTUSpecModel.getUnitId(),
                            pointDetail.getOffset(),
                            pointDetail.getNumOfRegisters());
                    for (Register register : registers) {
                        log.info("Register value:{}", register.getValue());
                    }
                } catch (Exception e) {
                    log.error("DttaskId={}采集registerConfig={}出现异常", this.dttaskId, pointDetail, e);
                }
            }
        }
    }
}

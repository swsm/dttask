package com.swsm.dttask.server.protocol.modbus.tcp;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusTCPCollectWorker implements Runnable {

    private Long dttaskId;
    private Long dttaskJobId;
    private Long deviceId;
    private ModbusTCPSpecModel modbusTCPSpecModel;
    private ModbusTCPMaster master;
    private Map<String, Long> lastReadMap = new HashMap<>();

    public ModbusTCPCollectWorker(Long dttaskId, Long dttaskJobId, Long deviceId, ModbusTCPSpecModel modbusTCPSpecModel, ModbusTCPMaster master) {
        this.dttaskId = dttaskId;
        this.dttaskJobId = dttaskJobId;
        this.deviceId = deviceId;
        this.modbusTCPSpecModel = modbusTCPSpecModel;
        this.master = master;
    }

    @Override
    public void run() {
        long current = new Date().getTime();
        for (ModbusTCPSpecModel.PointDetail pointDetail : modbusTCPSpecModel.getPointDetailList()) {
            String key = pointDetail.getKey();
            if (lastReadMap.containsKey(key) &&
                    (current - lastReadMap.get(key)) % pointDetail.getSamplingInterval() >= 1) {
                try {
                    Register[] registers = master.readMultipleRegisters(modbusTCPSpecModel.getSlaveId(),
                            pointDetail.getOffset(),
                            pointDetail.getNumOfRegisters());
                    for (Register register : registers) {
                        log.info("Register value:{}", register.getValue());
                    }
                } catch (Exception e) {
                    log.error("DttaskId={}采集pointDetail={}出现异常", dttaskId, pointDetail, e);
                }
            }
        }
    }
}

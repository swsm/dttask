package com.swsm.dttask.protocol.sdk;

import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Slf4j
public class SimulatorCollectWorker implements Runnable {

    private Long dttaskId;
    private Long dttaskJobId;
    private Long deviceId;
    private SimulatorSpecModel simulatorSpecModel;

    public SimulatorCollectWorker(Long dttaskId, Long dttaskJobId, Long deviceId, SimulatorSpecModel simulatorSpecModel) {
        this.dttaskId = dttaskId;
        this.dttaskJobId = dttaskJobId;
        this.deviceId = deviceId;
        this.simulatorSpecModel = simulatorSpecModel;
    }

    @Override
    public void run() {
        log.info("deviceId={},dttaskId={},dttaskJobId={},simulatorSpecModel={}",
                deviceId, dttaskId, dttaskJobId, simulatorSpecModel);
    }
}

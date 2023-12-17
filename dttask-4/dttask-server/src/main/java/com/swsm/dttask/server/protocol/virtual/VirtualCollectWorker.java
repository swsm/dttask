package com.swsm.dttask.server.protocol.virtual;

import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Slf4j
public class VirtualCollectWorker implements Runnable {

    private Long dttaskId;
    private Long dttaskJobId;
    private Long deviceId;
    private VirtualSpecModel virtualSpecModel;

    public VirtualCollectWorker(Long dttaskId, Long dttaskJobId, Long deviceId, VirtualSpecModel virtualSpecModel) {
        this.dttaskId = dttaskId;
        this.dttaskJobId = dttaskJobId;
        this.deviceId = deviceId;
        this.virtualSpecModel = virtualSpecModel;
    }

    @Override
    public void run() {
        log.info("deviceId={},dttaskId={},dttaskJobId={},virtualSpecModel={}",
                deviceId, dttaskId, dttaskJobId, virtualSpecModel);
    }
}

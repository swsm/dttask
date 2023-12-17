package com.swsm.dttask.protocol.sdk.service;

import com.swsm.dttask.protocol.sdk.model.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-11
 */
@Slf4j
public abstract class AbstractProtocol implements IProtocol {

    protected Long dttaskId;
    protected Long dttaskJobId;
    protected Long deviceId;
    protected ProtocolContext protocolContext;
    
    public void parseConfig(ProtocolContext protocolContext) {
        this.dttaskId = protocolContext.getDttaskId();
        this.dttaskJobId = protocolContext.getDttaskJobId();
        this.deviceId = protocolContext.getDeviceId();
        this.protocolContext = protocolContext;
        doParseConfig(protocolContext);
    }
    
    protected abstract void doParseConfig(ProtocolContext protocolContext);
    
    
    public abstract void doStart();
    
    public void start(ProtocolContext protocolContext) {
        log.info("进入 AbstractProtocol.start, protocolContext={}", protocolContext);
        parseConfig(protocolContext);
        doStart();
    }
    
}

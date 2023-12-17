package com.swsm.dttask.protocol.sdk.service;

import com.swsm.dttask.protocol.sdk.model.ProtocolContext;

/**
 * @author swsm
 * @date 2023-12-11
 */
public interface IProtocol {
    
    int getType();

    
    void start(ProtocolContext protocolContext);
    void parseConfig(ProtocolContext protocolContext);
    
}

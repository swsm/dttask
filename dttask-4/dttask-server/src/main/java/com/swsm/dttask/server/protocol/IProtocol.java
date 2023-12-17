package com.swsm.dttask.server.protocol;

/**
 * @author swsm
 * @date 2023-12-11
 */
public interface IProtocol {
    
    int getType();

    
    void start(ProtocolContext protocolContext);
    void parseConfig(ProtocolContext protocolContext);
    
}

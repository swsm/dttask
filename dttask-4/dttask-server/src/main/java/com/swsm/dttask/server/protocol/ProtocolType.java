package com.swsm.dttask.server.protocol;

/**
 * @author swsm
 * @date 2023-12-12
 */
public enum ProtocolType {
    SIMULATOR(-2, true), VIRTUAL(-1, true), MODBUS_RUT(0, true), MODBUS_TCP(1, true), MQTT(2, false);
    int code;
    boolean finish;

    ProtocolType(int code, boolean finish) {
        this.code = code;
        this.finish = finish;
    }
    
    public int getCode() {
        return code;
    }
    
    
}

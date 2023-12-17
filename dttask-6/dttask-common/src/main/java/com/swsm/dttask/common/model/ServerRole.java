package com.swsm.dttask.common.model;

/**
 * @author swsm
 * @date 2023-12-04
 */
public enum ServerRole {
    
    CONTROLLER(0), FOLLOWER(1);
    
    private int code;

    ServerRole(Integer code) {
        this.code = code;
    }
    
    public boolean isController() {
        return this.code == CONTROLLER.code;
    }
    
}

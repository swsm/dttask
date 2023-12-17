package com.swsm.dttask.common.model;

/**
 * @author swsm
 * @date 2023-12-04
 */
public enum ServerStatus {

    STARTING(0), IDENTIFYING(1), VOTING(2), RUNNING(3);
    
    private int code;

    ServerStatus(int code) {
        this.code = code;
    }
    
    public boolean isVoting() {
        return this.code == VOTING.code;
    }

    public boolean isRunning() {
        return this.code == RUNNING.code;
    }

    public boolean isIdentifying() {
        return this.code == IDENTIFYING.code;
    }

    public boolean isStarting() {
        return this.code == STARTING.code;
    }
    
    
}

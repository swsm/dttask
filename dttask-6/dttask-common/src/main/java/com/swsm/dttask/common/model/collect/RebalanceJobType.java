package com.swsm.dttask.common.model.collect;

/**
 * @author swsm
 * @date 2023-12-09
 */
public enum RebalanceJobType {
    
    FOLLOWER_OFFLINE(0), CONTROLLER_OFFLINE(1), NODE_ONLINE(2);
    
    private int code;

    RebalanceJobType(int code) {
        this.code = code;
    }
    
    public boolean isFollowerOffline() {
        return this.code == FOLLOWER_OFFLINE.code;
    }

    public boolean isControllerOffline() {
        return this.code == CONTROLLER_OFFLINE.code;
    }

    public boolean isNodeOnline() {
        return this.code == NODE_ONLINE.code;
    }
    
    
}

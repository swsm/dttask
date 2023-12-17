package com.swsm.dttask.server.model;

/**
 * @author swsm
 * @date 2023-12-07
 */
public class Follower {
    
    private Follower() {}
    
    private static class Instance {
        public static final Follower follower = new Follower();
    }

    public static Follower getInstance() {
        return Instance.follower;
    }
    
}

package com.swsm.dttask.server.job;

/**
 * @author swsm
 * @date 2023-12-08
 */
public interface JobAllotStrategy {
    
    JobAllotStrategyType getType();
    
    void allotJob();

    void rebalanceJob(RebalanceJobContext rebalanceJobContext);
    
}

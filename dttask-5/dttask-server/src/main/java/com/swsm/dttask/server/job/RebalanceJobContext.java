package com.swsm.dttask.server.job;

import com.swsm.dttask.common.model.collect.RebalanceJobType;
import lombok.Data;

/**
 * @author swsm
 * @date 2023-12-09
 */
@Data
public class RebalanceJobContext {
    
    private final RebalanceJobType type;
    
    private final Long serverId;

    public RebalanceJobContext(RebalanceJobType type, Long serverId) {
        this.type = type;
        this.serverId = serverId;
    }
    
    
}

package com.swsm.dttask.server.job;

import com.swsm.dttask.common.model.entity.Job;
import com.swsm.dttask.server.BeanUseHelper;
import java.util.List;

/**
 * @author swsm
 * @date 2023-12-08
 */
public abstract class AbstractJobAllotStrategy implements JobAllotStrategy {
    
    
    public List<Job> getAllJob() {
        return BeanUseHelper.entityHelpService().getAllJob();
    }
    
}

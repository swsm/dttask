package com.swsm.dttask.server.job;

import com.swsm.dttask.common.model.entity.DttaskJob;
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

    public List<DttaskJob> getAllDttaskJob() {
        return BeanUseHelper.entityHelpService().getAllDttaskJob();
    }
    
    public List<DttaskJob> getByDttaskId(long dttaskId) {
        return BeanUseHelper.entityHelpService().queryDttaskJob(dttaskId);
    }
    
}

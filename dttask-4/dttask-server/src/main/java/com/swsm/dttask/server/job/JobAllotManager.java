package com.swsm.dttask.server.job;

import cn.hutool.core.text.CharSequenceUtil;
import com.swsm.dttask.common.exception.BusinessException;
import com.swsm.dttask.server.BeanUseHelper;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-08
 */
@Component
@Slf4j
public class JobAllotManager {
    
    @Autowired
    private List<JobAllotStrategy> jobAllotStrategies;
    
    private static final Map<JobAllotStrategyType, JobAllotStrategy> map = new EnumMap<>(JobAllotStrategyType.class);
    
    @PostConstruct
    public void init() {
        if (jobAllotStrategies != null) {
            for (JobAllotStrategy jobAllotStrategy : jobAllotStrategies) {
                map.put(jobAllotStrategy.getType(), jobAllotStrategy);
            }
        }
    }
    
    public static JobAllotStrategy getStrategy() {
        JobAllotStrategyType allotJobStrategyType = JobAllotStrategyType.from(
                BeanUseHelper.dttaskServerConfig().getAllotJobStrategyType());
        if (map.containsKey(allotJobStrategyType)) {
            return map.get(allotJobStrategyType);
        }
        throw new BusinessException(CharSequenceUtil.format("allotJobStrategyType={} 配置有误", allotJobStrategyType));
    }
    
}

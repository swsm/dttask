package com.swsm.dttask.server.job;

import com.swsm.dttask.common.model.entity.DttaskJob;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author swsm
 * @date 2023-12-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteDttaskJobContext {

    private Map<Long, List<DttaskJob>> dttaskJobMap;
    private Boolean startFlag;
    
}

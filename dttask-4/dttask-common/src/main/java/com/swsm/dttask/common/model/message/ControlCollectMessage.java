package com.swsm.dttask.common.model.message;

import com.swsm.dttask.common.exception.BusinessException;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author swsm
 * @date 2023-11-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlCollectMessage {
    
    private Long dttaskId;
    private Set<Long> dttaskJobIds;
    private Boolean startFlag;
    
    public boolean isStart() {
        if (startFlag == null) {
            throw new BusinessException("ControlCollectMessage.startFlag == null");
        }
        return startFlag.booleanValue();
    }
    
    
}

package com.swsm.dttask.common.model.message;

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
public class CommonRespMessage {
    
    private String message;
    private Boolean successFlag;
    
}

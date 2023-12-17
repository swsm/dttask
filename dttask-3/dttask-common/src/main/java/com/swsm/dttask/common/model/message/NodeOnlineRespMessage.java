package com.swsm.dttask.common.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author swsm
 * @date 2023-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeOnlineRespMessage {
    
    private Long onlineServerId;
    
}

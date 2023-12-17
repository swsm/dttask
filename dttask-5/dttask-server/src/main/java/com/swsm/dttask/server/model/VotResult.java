package com.swsm.dttask.server.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author swsm
 * @date 2023-12-04
 */
@Data
public class VotResult {
    
    private Integer version = 0;
    private Map<Long, Integer> votMap = new HashMap<>();
    
}

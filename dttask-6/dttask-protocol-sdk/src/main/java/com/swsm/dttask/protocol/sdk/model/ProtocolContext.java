package com.swsm.dttask.protocol.sdk.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Data
public class ProtocolContext {
    
    private JSONObject param;
    private Long dttaskId;
    private Long dttaskJobId;
    private Long deviceId;
    private Integer linkType;
    
}

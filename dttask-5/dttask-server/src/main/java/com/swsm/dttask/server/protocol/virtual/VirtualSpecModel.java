package com.swsm.dttask.server.protocol.virtual;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Data
@Slf4j
public class VirtualSpecModel {
    
    private VirtualSpecModel() {}

    public static VirtualSpecModel getFromParam(JSONObject jsonObject) {
        log.debug("VirtualSpecModel.getFromParam param={}", jsonObject);
        return new VirtualSpecModel();
    }
    
}

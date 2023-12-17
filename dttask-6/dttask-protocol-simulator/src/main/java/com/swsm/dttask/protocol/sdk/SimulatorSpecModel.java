package com.swsm.dttask.protocol.sdk;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-12
 */
@Data
@Slf4j
public class SimulatorSpecModel {
    private SimulatorSpecModel() {}

    public static SimulatorSpecModel getFromParam(JSONObject jsonObject) {
        log.debug("SimulatorSpecModel.getFromParam param={}", jsonObject);
        return new SimulatorSpecModel();
    }
    
}

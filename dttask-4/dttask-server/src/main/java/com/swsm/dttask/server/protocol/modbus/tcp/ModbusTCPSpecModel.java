package com.swsm.dttask.server.protocol.modbus.tcp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ModbusTCPSpecModel {
    private Integer mode;
    private Long deviceId;
    private String ip;
    private Integer port;
    private Integer slaveId;
    private List<PointDetail> pointDetailList;

    @Data
    public static class PointDetail {
        private String key;
        private Integer offset;
        private Integer numOfRegisters;
        private Integer samplingInterval;
    }

    public static ModbusTCPSpecModel getFromParam(JSONObject param) {
        JSONObject linkSpec = param.getJSONObject("linkSpec");
        ModbusTCPSpecModel modbusTCPSpecModel = new ModbusTCPSpecModel();
        modbusTCPSpecModel.setMode(linkSpec.getInteger("mode"));
        modbusTCPSpecModel.setDeviceId(linkSpec.getLong("deviceId"));
        modbusTCPSpecModel.setIp(linkSpec.getString("ip"));
        modbusTCPSpecModel.setPort(linkSpec.getInteger("port"));
        modbusTCPSpecModel.setSlaveId(linkSpec.getInteger("slaveId"));

        JSONArray pointDetailJsonArray = linkSpec.getJSONArray("pointDetailList");
        
        List<PointDetail> tcpPointDetailList = new ArrayList<>();
        for (Object pointDetailObject  : pointDetailJsonArray) {
            JSONObject pointDetail = (JSONObject)pointDetailObject; 
            PointDetail tcpPointDetail = new PointDetail();
            tcpPointDetail.setKey(pointDetail.getString("key"));
            tcpPointDetail.setOffset(pointDetail.getInteger("offset"));
            tcpPointDetail.setNumOfRegisters(pointDetail.getInteger("numOfRegisters"));
            tcpPointDetail.setSamplingInterval(pointDetail.getInteger("samplingInterval"));
            tcpPointDetailList.add(tcpPointDetail);
        }
        modbusTCPSpecModel.setPointDetailList(tcpPointDetailList);
        return modbusTCPSpecModel;
    }
}

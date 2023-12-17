package com.swsm.dttask.server.protocol.modbus.rtu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ModbusRTUSpecModel {
    private Integer mode;
    private Integer unitId;
    private String portName;
    private Integer baudRate;
    private Integer databits;
    private String parity;
    private Integer stopbits;
    private String encoding = "RTU";
    private Boolean echo = false;
    private List<PointDetail> pointDetailList;

    @Data
    public static class PointDetail {
        private String key;
        private Integer offset;
        private Integer numOfRegisters;
        private Integer samplingInterval;
    }

    public static ModbusRTUSpecModel getFromParam(JSONObject param) {
        JSONObject linkSpec = param.getJSONObject("linkSpec");
        ModbusRTUSpecModel modbusRTUSpecModel = new ModbusRTUSpecModel();
        modbusRTUSpecModel.setMode(linkSpec.getInteger("mode"));
        modbusRTUSpecModel.setUnitId(linkSpec.getInteger("unitId"));
        modbusRTUSpecModel.setPortName(linkSpec.getString("portName"));
        modbusRTUSpecModel.setBaudRate(linkSpec.getInteger("baudRate"));
        modbusRTUSpecModel.setDatabits(linkSpec.getInteger("databits"));
        modbusRTUSpecModel.setParity(linkSpec.getString("parity"));
        modbusRTUSpecModel.setStopbits(linkSpec.getInteger("stopbits"));
        JSONArray pointDetailJsonArray = linkSpec.getJSONArray("pointDetailList");

        List<PointDetail> rtuPointDetailList = new ArrayList<>();
        for (Object pointDetailObject : pointDetailJsonArray) {
            JSONObject pointDetail = (JSONObject)pointDetailObject;
            PointDetail rtuPointDetail = new PointDetail();
            rtuPointDetail.setKey(pointDetail.getString("key"));
            rtuPointDetail.setOffset(pointDetail.getInteger("offset"));
            rtuPointDetail.setNumOfRegisters(pointDetail.getInteger("numOfRegisters"));
            rtuPointDetail.setSamplingInterval(pointDetail.getInteger("samplingInterval"));
            rtuPointDetailList.add(rtuPointDetail);
        }
        modbusRTUSpecModel.setPointDetailList(rtuPointDetailList);
        return modbusRTUSpecModel;
    }
}

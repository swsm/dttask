package com.swsm.dttask.common.model.entity;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Data
public class LinkSpec implements Serializable {
    
    private Integer mode;
    private Long deviceId;
    private String portName;
    private Integer baudRate;
    private Integer dataBits;
    private String parity;
    private Integer stopBits;
    private Integer unitId;
    private String ip;
    private Integer port;
    private Integer slaveId;
    private List<PointDetail> pointDetailList;
    
    @Data
    public static class PointDetail implements Serializable {
        private String key;
        private Integer offset;
        private Integer numOfRegisters;
        private Integer samplingInterval;
    }
    
}

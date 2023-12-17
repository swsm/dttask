package com.swsm.dttask.common.model.message;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author swsm
 * @date 2023-04-15
 */
@Data
public class DttaskMessage {

    public static final byte COMMON_RESP = 0X00;
    public static final byte PING = 0X01;
    public static final byte PONG = 0X02;

    // 类型
    private byte type;
    // 消息实际信息
    private String info;
    
    public static DttaskMessage buildPingMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(PING);
        dttaskMessage.setInfo(JSON.toJSONString(new PingMessage(serverId)));
        return dttaskMessage;
    }

    public static DttaskMessage buildPongMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(PONG);
        dttaskMessage.setInfo(JSON.toJSONString(new PongMessage(serverId)));
        return dttaskMessage;
    }
    
    
    public static DttaskMessage buildCommonRespMessage(String message, boolean successFlag) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(COMMON_RESP);
        dttaskMessage.setInfo(JSON.toJSONString(new CommonRespMessage(message, successFlag)));
        return dttaskMessage;
    }
    
}

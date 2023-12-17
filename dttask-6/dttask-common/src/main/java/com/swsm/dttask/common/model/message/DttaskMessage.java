package com.swsm.dttask.common.model.message;

import com.alibaba.fastjson.JSON;
import java.util.Set;
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
    public static final byte VOTING = 0X03;
    public static final byte VOT_RESP = 0X04;
    public static final byte NODE_OFFLINE = 0X05;
    public static final byte NODE_ONLINE = 0X06;
    public static final byte NODE_ONLINE_RESP = 0X07;
    public static final byte CONTROL_COLLECT = 0x08;

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

    public static DttaskMessage buildNodeOnlineRespMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(NODE_ONLINE_RESP);
        dttaskMessage.setInfo(JSON.toJSONString(new NodeOnlineRespMessage(serverId)));
        return dttaskMessage;
    }

    public static DttaskMessage buildNodeOnlineMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(NODE_ONLINE);
        dttaskMessage.setInfo(JSON.toJSONString(new NodeOnlineMessage(serverId)));
        return dttaskMessage;
    }

    public static DttaskMessage buildNodeOfflineMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(NODE_OFFLINE);
        dttaskMessage.setInfo(JSON.toJSONString(new VotRespMessage(serverId)));
        return dttaskMessage;
    }

    public static DttaskMessage buildVotRespMessage(long serverId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(VOT_RESP);
        dttaskMessage.setInfo(JSON.toJSONString(new VotRespMessage(serverId)));
        return dttaskMessage;
    }

    public static DttaskMessage buildVotRequestMessage(Long lastControllerServerId, long fromServerId, long serverId, int version) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(VOTING);
        dttaskMessage.setInfo(JSON.toJSONString(new VotRequestMessage(lastControllerServerId, fromServerId, serverId, version)));
        return dttaskMessage;
    }

    public static DttaskMessage buildControlCollectMessage(Set<Long> dttaskJobIds, boolean startFlag, long dttaskId) {
        DttaskMessage dttaskMessage = new DttaskMessage();
        dttaskMessage.setType(CONTROL_COLLECT);
        dttaskMessage.setInfo(JSON.toJSONString(
                new ControlCollectMessage(dttaskId, dttaskJobIds, startFlag)));
        return dttaskMessage;
    }
    
}

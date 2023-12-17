package com.swsm.dttask.server.model;

import com.swsm.dttask.common.model.ServerRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author swsm
 * @date 2023-12-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {

    private Long serverId;
    private String ip;
    private Integer port;
    private ServerRole serverRole;
    
}

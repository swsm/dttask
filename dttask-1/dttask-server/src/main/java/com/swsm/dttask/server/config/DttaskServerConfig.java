package com.swsm.dttask.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author swsm
 * @date 2023-04-15
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "dttask.server")
public class DttaskServerConfig {

    private Long serverId;

}

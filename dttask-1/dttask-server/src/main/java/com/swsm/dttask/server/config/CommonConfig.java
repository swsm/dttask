
package com.swsm.dttask.server.config;

import com.swsm.dttask.common.generator.DistributedIdGenerator;
import com.swsm.dttask.common.generator.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用配置bean
 */
@Configuration
public class CommonConfig {
    
    /**
     * 分布式id生成bean 声明
     */
    @Bean(name = {"idGenerator"})
    public IdGenerator idGenerator() {
        return new DistributedIdGenerator(1L);
    }

}

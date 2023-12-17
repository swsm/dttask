package com.swsm.dttask.server;

import cn.hutool.extra.spring.SpringUtil;
import com.swsm.dttask.common.service.EntityHelpService;
import com.swsm.dttask.common.util.RedisUtil;
import com.swsm.dttask.server.config.DttaskServerConfig;

/**
 * @author swsm
 * @date 2023-12-13
 */
public class BeanUseHelper {
    
    private BeanUseHelper() {}
    
    public static DttaskServerConfig dttaskServerConfig() {
        return SpringUtil.getBean(DttaskServerConfig.class);
    }
    
    public static RedisUtil redisUtil() {
        return SpringUtil.getBean(RedisUtil.class);
    }

    public static EntityHelpService entityHelpService() {
        return SpringUtil.getBean(EntityHelpService.class);
    }
    
}

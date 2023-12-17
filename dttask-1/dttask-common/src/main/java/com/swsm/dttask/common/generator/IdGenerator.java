package com.swsm.dttask.common.generator;

/**
 * id 生成接口
 */
public interface IdGenerator {
    
    /**
     * 生成下一个id 字符串
     */
    String next();

    /**
     * 生成下一个id long
     */
    Long nextLong();
    
}

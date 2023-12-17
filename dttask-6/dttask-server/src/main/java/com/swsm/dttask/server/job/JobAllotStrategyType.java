package com.swsm.dttask.server.job;

import cn.hutool.core.text.CharSequenceUtil;
import com.swsm.dttask.common.exception.BusinessException;

/**
 * @author swsm
 * @date 2023-12-08
 */
public enum JobAllotStrategyType {
    
    AVERAGE(0), WEIGHT(1), SPECIFIC(2);
    
    int code;

    JobAllotStrategyType(int code) {
        this.code = code;
    }
    
    public static JobAllotStrategyType from(int code) {
        for (JobAllotStrategyType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new BusinessException(CharSequenceUtil.format("code={}不在JobAllotStrategyType中", code));
    }
}

package com.swsm.dttask.common.exception;

import com.swsm.dttask.common.util.Constant;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 445445701327085003L;
    private final Integer code;

    public BusinessException() {
        this(Constant.ExceptionCode.SERVER_ERROR, "服务器内部错误");
    }

    public BusinessException(String message) {
        this(Constant.ExceptionCode.OTHER_ERROR, message);
    }

    public BusinessException(Constant.ExceptionCode exceptionCode, String message) {
        super(message);
        this.code = exceptionCode.getCode();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = Constant.ExceptionCode.OTHER_ERROR.getCode();
    }

    public int getCode() {
        return this.code;
    }

}

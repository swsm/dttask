package com.swsm.dttask.common.result;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    private String requestId = MDC.get("requestId");
    
    @JSONField( ordinal = 1)
    private int code = 0;
    
    @JSONField( ordinal = 2)
    private int state = 200;
    
    @JSONField(ordinal = 3)
    private String msg = "success";
    
    @JSONField(ordinal = 4)
    private String timestamp = String.valueOf(System.currentTimeMillis());
    
    @JSONField(ordinal = 5)
    private T data;

    /**
     * 构造函数
     */
    public Result() {
    }

    /**
     * 构造函数
     */
    public Result(T data) {
        this.data = data;
    }

    /**
     * 构造函数
     */
    public Result(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    /**
     * 构造函数
     */
    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数
     */
    public Result(Throwable throwable) {
        this.msg = throwable.getMessage();
        this.code = 1;
        this.state = 500;
    }

    /**
     * 构造函数
     */
    public Result(Throwable throwable, int state) {
        this.msg = throwable.getMessage();
        this.code = 1;
        this.state = state;
    }

    /**
     * 构造函数
     */
    public Result(Boolean success, String msg, T data) {
        if (Boolean.TRUE.equals(success)) {
            this.code = SUCCESS;
            this.state = 200;
        } else {
            this.code = FAIL;
            this.state = 500;
        }

        this.msg = msg;
        this.data = data;
    }

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return this.code == 0;
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> build(Boolean success, String msg, T data) {
        return new Result<>(success, msg, data);
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> buildSuccess(String msg, T data) {
        return build(Boolean.TRUE, msg, data);
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> buildSuccess(T data) {
        return buildSuccess((String) null, data);
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> buildFailure(String msg, T data) {
        return build(Boolean.FALSE, msg, data);
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> buildFailure(String msg) {
        return buildFailure(msg, null);
    }

    /**
     * 构造结果
     */
    public static <T> Result<T> buildFailure(T data) {
        return buildFailure("", data);
    }

    public int getCode() {
        return this.code;
    }

    public int getState() {
        return this.state;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * toString
     */
    public String toString() {
        return "RP(code=" + this.getCode() + ", state=" + this.getState() + "," 
                + " msg=" + this.getMsg() + ", timestamp=" + this.getTimestamp() 
                + ", data=" + this.getData() + ")";
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> res = new HashMap<>();
        res.put("code", this.getCode());
        res.put("state", this.getState());
        res.put("msg", this.getMsg());
        res.put("timestamp", this.getTimestamp());
        res.put("data", this.getData());
        return res;
    }
}

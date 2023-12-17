package com.swsm.dttask.common.util;


/**
 * 常量类
 */
public class Constant {
    
    public enum ExceptionCode {
        SERVER_ERROR(10000, "系统错误"), OTHER_ERROR(10001, "其它错误");
        int code;
        String desc;

        ExceptionCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public int getCode() {
            return code;
        }

    }
    

    private Constant() {
        throw new IllegalStateException(FAILED);
    }
    
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";

    /**
     * 删除标识
     */
    public static final Integer DELETE_FLAG_NORMAL = 0;
    public static final Integer DELETE_FLAG_DELETE = 1;

    public static final int ENABLE_FLAG_FALSE = 0;
    public static final int ENABLE_FLAG_TRUE = 1;
    
    
    public static class EntityConstants {
        private EntityConstants(){}
        public static final int STATUS_ONLINE = 0;
        public static final int STATUS_OFFLINE = 1;

        
        public static final int STATUS_NEW = 0;
        public static final int STATUS_RUN = 1;
        public static final int STATUS_STOP = 2;
        public static final int STATUS_ERROR = 3;
        
        public static final int LINK_TYPE_VIRTUAL = -1;
        public static final int LINK_TYPE_MODBUSRTU = 0;
        public static final int LINK_TYPE_MODBUSTCP = 1;
        
    }

}

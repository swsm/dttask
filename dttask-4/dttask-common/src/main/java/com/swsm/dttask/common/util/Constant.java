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

    public static final Integer VOTE_VERSION = 1;

    public static final Integer MESSAGE_MAX_SIZE = Integer.MAX_VALUE;
    public static final Integer MESSAGE_LENGTH_FILED_OFFSET = 0;
    public static final Integer MESSAGE_LENGTH_FILED_LENGTH = 4;
    public static final Integer MESSAGE_TOTAL_SIZE = 4;
    public static final Integer MESSAGE_TYPE_SIZE = 1;
    public static final Integer MESSAGE_INFO_SIZE = 4;
    

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

    public static class RedisConstants {
        private RedisConstants() {}
        public static final String DTTASK_CONTROLLER = "dttask_controller";
        public static final String DTTASK_NODE_INFO = "dttask_node_info";

    }

}

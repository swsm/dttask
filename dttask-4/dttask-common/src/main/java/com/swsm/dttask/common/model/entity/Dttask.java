package com.swsm.dttask.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author swsm
 * @date 2023-11-21
 */
@Data
@TableName("t_dttask")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dttask extends BaseEntity implements Serializable {
    
    @TableField("name")
    private String name;

    @TableField("code")
    private String code;
    
    @TableField("ip_address")
    private String ipAddress;
    
    @TableField("port")
    private Integer port;
    
    @TableField("status")
    private Integer status;
    
    
}

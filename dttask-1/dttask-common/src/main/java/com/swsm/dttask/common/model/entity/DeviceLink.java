package com.swsm.dttask.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
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
@TableName(value = "t_device_link", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceLink extends BaseEntity implements Serializable {
    
    @TableField("device_id")
    private Long deviceId;
    
    @TableField("link_type")
    private Integer linkType;
    
    @TableField(value = "link_spec", typeHandler = FastjsonTypeHandler.class)
    private LinkSpec linkSpec;
    

    
}

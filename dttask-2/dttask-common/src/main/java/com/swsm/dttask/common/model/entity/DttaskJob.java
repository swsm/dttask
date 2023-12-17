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
@TableName(value = "t_dttask_job", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DttaskJob extends BaseEntity implements Serializable {
    
    @TableField("dttask_id")
    private Long dttaskId;

    @TableField("device_id")
    private Long deviceId;
    
    @TableField("device_link_id")
    private Long deviceLinkId;
    
    @TableField("job_id")
    private Long jobId;
    
    @TableField("link_type")
    private Integer linkType;
    
    @TableField(value = "link_spec", typeHandler = FastjsonTypeHandler.class)
    private LinkSpec linkSpec;
    
    @TableField(value = "job_spec", typeHandler = FastjsonTypeHandler.class)
    private JobSpec jobSpec;
    
    @TableField("status")
    private Integer status;
    
    
}

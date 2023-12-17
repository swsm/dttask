package com.swsm.dttask.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public abstract class BaseEntity implements Serializable {

    @TableId(value = "id")
    private Long id;
    
    @TableField("delete_flag")
    private Integer deleteFlag;

    @TableField("remark")
    private String remark;


    @TableField("created_at")
    private Date createdAt;

    @TableField(value = "updated_at", update = "now()")
    private Date updatedAt;
    
}

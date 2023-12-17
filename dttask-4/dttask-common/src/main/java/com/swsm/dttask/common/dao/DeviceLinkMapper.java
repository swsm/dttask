package com.swsm.dttask.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsm.dttask.common.model.entity.DeviceLink;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author swsm
 * @date 2023-11-21
 */
@Mapper
@Repository
public interface DeviceLinkMapper extends BaseMapper<DeviceLink> {
}

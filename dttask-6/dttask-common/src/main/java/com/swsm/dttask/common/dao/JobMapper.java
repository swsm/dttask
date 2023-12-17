package com.swsm.dttask.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsm.dttask.common.model.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Mapper
@Repository
public interface JobMapper extends BaseMapper<Job> {
}

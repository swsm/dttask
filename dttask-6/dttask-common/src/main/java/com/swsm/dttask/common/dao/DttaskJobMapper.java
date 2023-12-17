package com.swsm.dttask.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsm.dttask.common.model.entity.DttaskJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author swsm
 * @date 2023-11-21
 */
@Mapper
@Repository
public interface DttaskJobMapper extends BaseMapper<DttaskJob> {

    void updateStatus(@Param("status") int status, @Param("id") long id);
    void updateStatusAndDttaskId(@Param("status") int status, @Param("dttaskId") long dttaskId, @Param("id") long id);

    void historyAllJob();

    void deleteAll();
    
}

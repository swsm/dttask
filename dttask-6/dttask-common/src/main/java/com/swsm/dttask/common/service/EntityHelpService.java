package com.swsm.dttask.common.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.swsm.dttask.common.dao.DeviceLinkMapper;
import com.swsm.dttask.common.dao.DeviceMapper;
import com.swsm.dttask.common.dao.DttaskJobMapper;
import com.swsm.dttask.common.dao.DttaskMapper;
import com.swsm.dttask.common.dao.JobMapper;
import com.swsm.dttask.common.generator.IdGenerator;
import com.swsm.dttask.common.model.entity.Device;
import com.swsm.dttask.common.model.entity.Dttask;
import com.swsm.dttask.common.model.entity.DttaskJob;
import com.swsm.dttask.common.model.entity.Job;
import com.swsm.dttask.common.util.Constant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author swsm
 * @date 2023-11-22
 */
@Service
@Slf4j
public class EntityHelpService {

    @Autowired
    private DttaskMapper dttaskMapper;
    @Autowired
    private DttaskJobMapper dttaskJobMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceLinkMapper deviceLinkMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private IdGenerator idGenerator;

    public void invalidAllDttaskJob() {
        dttaskJobMapper.historyAllJob();
        dttaskJobMapper.deleteAll();
    }

    public List<Long> saveAllDttaskJob(Map<Long, List<Job>> allotJobMap) {
        List<Long> dttaskJobIds = new ArrayList<>();
        allotJobMap.forEach((dttaskId, jobList) -> {
            for (Job job : jobList) {
                DttaskJob dttaskJob = new DttaskJob();
                Long dttaskJobId = idGenerator.nextLong();
                dttaskJobIds.add(dttaskJobId);
                dttaskJob.setId(dttaskJobId);
                dttaskJob.setDttaskId(dttaskId);
                dttaskJob.setDeviceId(job.getDeviceId());
                dttaskJob.setDeviceLinkId(job.getDeviceLinkId());
                dttaskJob.setJobId(job.getId());
                dttaskJob.setLinkType(job.getLinkType());
                dttaskJob.setLinkSpec(job.getLinkSpec());
                dttaskJob.setJobSpec(job.getJobSpec());
                dttaskJob.setStatus(Constant.EntityConstants.STATUS_NEW);
                dttaskJobMapper.insert(dttaskJob);
            }
        });
        return dttaskJobIds;
    }

    public Map<Long, List<DttaskJob>> queryDttaskJob() {
        Map<Long, List<DttaskJob>> res = new HashMap<>();
        List<DttaskJob> jobs = dttaskJobMapper.selectList(Wrappers.lambdaQuery(DttaskJob.class).eq(DttaskJob::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
        for (DttaskJob job : jobs) {
            if (res.get(job.getDttaskId()) == null) {
                res.put(job.getDttaskId(), new ArrayList<>());
            }
            res.get(job.getDttaskId()).add(job);
        }
        return res;
    }

    public void runDttaskJob(Set<Long> dttaskJobIds, long dttaskId) {
        for (Long id : dttaskJobIds) {
            this.dttaskJobMapper.updateStatusAndDttaskId(
                    Constant.EntityConstants.STATUS_RUN, dttaskId, id);
        }
    }

    public void stopDttaskJob(Set<Long> dttaskJobIds, long dttaskId) {
        for (Long id : dttaskJobIds) {
            this.dttaskJobMapper.updateStatusAndDttaskId(Constant.EntityConstants.STATUS_STOP, dttaskId, id);
        }
    }

    public List<DttaskJob> getAllDttaskJob() {
        return dttaskJobMapper.selectList(
                Wrappers.lambdaQuery(DttaskJob.class).eq(DttaskJob::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }

    public List<Job> getAllJob() {
        return jobMapper.selectList(Wrappers.lambdaQuery(Job.class).eq(Job::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }


    public List<DttaskJob> queryDttaskJob(long dttaskId) {
        return this.dttaskJobMapper.selectList(Wrappers.lambdaQuery(DttaskJob.class)
                .eq(DttaskJob::getDttaskId, dttaskId)
                .eq(DttaskJob::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }

    public DttaskJob queryDttaskJobById(long dttaskJobId) {
        return this.dttaskJobMapper.selectOne(Wrappers.lambdaQuery(DttaskJob.class)
                .eq(DttaskJob::getId, dttaskJobId)
                .eq(DttaskJob::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }

    public List<DttaskJob> queryDttaskJob(Set<Long> dttaskJobId) {
        return this.dttaskJobMapper.selectList(Wrappers.lambdaQuery(DttaskJob.class)
                .in(DttaskJob::getId, dttaskJobId)
                .eq(DttaskJob::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }

    public boolean existDevice(Long deviceId) {
        Device device = this.deviceMapper.selectById(deviceId);
        return device != null && Constant.DELETE_FLAG_NORMAL.equals(device.getDeleteFlag());
    }


    

    public Dttask getDttaskById(long id) {
        return this.dttaskMapper.selectOne(
                Wrappers.lambdaQuery(Dttask.class)
                        .eq(Dttask::getId, id)
                        .eq(Dttask::getDeleteFlag, Constant.DELETE_FLAG_NORMAL));
    }

}

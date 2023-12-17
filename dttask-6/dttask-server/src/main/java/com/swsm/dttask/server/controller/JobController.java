package com.swsm.dttask.server.controller;


import com.swsm.dttask.server.service.NetworkService;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author swsm
 * @date 2023-12-07
 */
@RestController
@Slf4j
public class JobController {
    
    @Autowired
    private NetworkService networkService;
    
    @GetMapping("/stopDttaskJob")
    public void stopJobId(@RequestParam("dttaskJobId") long dttaskJobId) {
        Set<Long> dttaskJobIds = new HashSet<>();
        dttaskJobIds.add(dttaskJobId);
        networkService.stopCollect(dttaskJobIds);
    }

    @GetMapping("/startDttaskJob")
    public void startJobId(@RequestParam("dttaskJobId") long dttaskJobId) {
        Set<Long> dttaskJobIds = new HashSet<>();
        dttaskJobIds.add(dttaskJobId);
        networkService.startCollect(dttaskJobIds);
    }
    
}

package com.swsm.dttask.server.model;

import com.swsm.dttask.server.job.JobAllotStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Slf4j
public class Controller {


    private final JobAllotStrategy strategy;

    private static Controller controller;

    private Controller(JobAllotStrategy strategy) {
        this.strategy = strategy;
    }

    public static synchronized Controller getInstance(JobAllotStrategy strategy) {
        if (controller == null) {
            controller = new Controller(strategy);
            return controller;
        }
        return controller;
    }

    public void allotJob() {
        strategy.allotJob();
    }

    
}

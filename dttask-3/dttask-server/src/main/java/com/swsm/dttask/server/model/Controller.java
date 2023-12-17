package com.swsm.dttask.server.model;

import lombok.extern.slf4j.Slf4j;

/**
 * @author swsm
 * @date 2023-12-07
 */
@Slf4j
public class Controller {
    
    
    private static Controller controller;
    
    private Controller() {
        
    }
    
    public static synchronized Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
            return controller;
        }
        return controller;
    }
    
}

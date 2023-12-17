package com.swsm.dttask.server;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author swsm
 * @date 2023-12-14
 */
@SpringBootApplication(scanBasePackages = {"com.swsm.dttask.*"})
@Slf4j
@MapperScan("com.swsm.dttask.common.dao.**")
public class DttaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DttaskServerApplication.class, args);
    }

}

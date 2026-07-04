package com.zhenyu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class DramaApplication {

    public static void main(String[] args) {

        SpringApplication.run(DramaApplication.class, args);
    }

}

// http://localhost:8080/admin/login
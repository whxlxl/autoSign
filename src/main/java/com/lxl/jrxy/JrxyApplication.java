package com.lxl.jrxy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync                    //开启异步支持
@EnableScheduling
@MapperScan(basePackages = "com.lxl.jrxy.mapper")
@SpringBootApplication
public class JrxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JrxyApplication.class, args);
    }

}

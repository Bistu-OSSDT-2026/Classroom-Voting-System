package com.cvs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cvs.mapper")
public class CvsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvsApplication.class, args);
    }
}

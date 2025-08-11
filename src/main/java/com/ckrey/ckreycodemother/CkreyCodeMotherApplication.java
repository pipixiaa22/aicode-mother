package com.ckrey.ckreycodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ckrey.ckreycodemother.mapper")
public class CkreyCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(CkreyCodeMotherApplication.class, args);
    }

}

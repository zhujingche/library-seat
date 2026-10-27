package com.zjc.libraryseat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.zjc.libraryseat.mapper")
@SpringBootApplication
@EnableScheduling
public class LibrarySeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySeatApplication.class, args);
    }

}

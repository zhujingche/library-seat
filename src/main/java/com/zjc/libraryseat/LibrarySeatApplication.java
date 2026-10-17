package com.zjc.libraryseat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.zjc.libraryseat.mapper")
@SpringBootApplication
public class LibrarySeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySeatApplication.class, args);
    }

}

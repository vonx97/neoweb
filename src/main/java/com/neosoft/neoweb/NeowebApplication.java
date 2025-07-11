package com.neosoft.neoweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.neosoft.neoweb", "controller"})
public class NeowebApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeowebApplication.class, args);
    }

}

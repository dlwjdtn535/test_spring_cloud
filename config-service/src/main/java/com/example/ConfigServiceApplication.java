package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableConfigServer
@SpringBootApplication
@RestController
public class ConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServiceApplication.class, args);
    }

    @RequestMapping("/")
    public String home() {
        System.out.println("called root URI!!!");
        return "config-service";
    }

//    @RequestMapping("/_ah/health")
//    public String healthy() {
//        return "Still surviving.";
//    }
}

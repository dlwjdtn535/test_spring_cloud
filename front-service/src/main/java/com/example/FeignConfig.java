package com.example;

import feign.Logger;
import feign.Logger.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Project : test_spring_cloud
 * @Date : 2017-08-04
 * @Author : nklee
 * @Description :
 */
@Configuration
public class FeignConfig {

    @Bean
    Level feignLoggerLevel() {
        return Level.FULL;
    }
}

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class FrontServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontServiceApplication.class, args);
	}
}

@RefreshScope
@RestController
class MessageRestController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${welcome.message:Hello default}")
    private String message;

    @Autowired
    private OrderServiceFeignClient orderServiceFeignClient;


    @RequestMapping("/front/message")
    String getMessage() {
        return this.message;
    }

    @RequestMapping("/front2/message")
        String getMessage2() {
        return "message2 : " + this.message;
    }

    /**
     * 클라이언트 사이드 로드밸런싱 처리 Ribbon + RestTemplate
     * @return
     */
    @RequestMapping("/load")
    String loadBalancer() {
        String result = restTemplate.exchange( "http://order-service/welcome", HttpMethod.GET, null, new ParameterizedTypeReference<String>() { }, 1).getBody();
        return result;
    }


    /**
     * feign 테스트
     * @return
     */
    @RequestMapping("/front/feign")
    String feign() {
        return orderServiceFeignClient.welcome("param1");
    }
}

@Configuration
class Config {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@FeignClient(value = "order-service")
interface OrderServiceFeignClient {

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    String welcome(@RequestParam("name") String param);

}
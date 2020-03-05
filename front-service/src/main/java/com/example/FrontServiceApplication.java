package com.example;

import java.util.List;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class FrontServiceApplication {

	public static void main(String[] args) {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "8888	");
		SpringApplication.run(FrontServiceApplication.class, args);
	}
}
@Controller
class ViewController {
   @RequestMapping("/viewTest")
   public String viewTest(Model model){
       model.addAttribute("greeting", "Hello Thymeleaf!");
       return "thymeleaf/test";
   }
   @RequestMapping("/viewTest2")
   public String viewTest2(Model model){
	   return "rect-test";
   }
}

@RefreshScope
@RestController
class MessageRestController {
	@Autowired
	CustomerRepository customerRepository;
	
    @Autowired
    private RestTemplate restTemplate;

    @Value("${welcome.message:Hello default}")
    private String message;

    @Autowired
    private OrderServiceFeignClient orderServiceFeignClient;


    @RequestMapping("/message")
    String getMessage() {
        return this.message;
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
    @RequestMapping("/feign")
    String feign() {
        return orderServiceFeignClient.welcome("param1");
    }
    
    /**
     * feign 테스트
     * @return
     */
    @RequestMapping("/test1")
    String test1() {
    	Customer customer = new Customer("크리스1", "010-7871-4443");
		customerRepository.save(customer);
		
		List<Customer> customerList = customerRepository.findAll();
		List<Customer> customerList2 = customerRepository.findUserByPhone("010-7871-4443");
		Customer chris = customerList.get(0);
		
		System.out.println(chris.getName());
		System.out.println(chris.getPhone());
		
		
		return "";
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
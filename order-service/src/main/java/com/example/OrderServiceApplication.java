package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableEurekaClient
@EnableCircuitBreaker
@SpringBootApplication
public class OrderServiceApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
	
	@RestController
	static class Controller {
		@GetMapping("/test")
		@HystrixCommand(fallbackMethod = "fallback")
	    String test(@RequestParam String path) {
			
			if(path.equals("1")){
				throw new RuntimeException("ERROR");
			}else{
				return "test1232123123123";
			}
	    }
		
		private String fallback(String path) {
			return "hello fallback";
		}
	}
	
	@Autowired
	private CustomerRepository repository;

	@Override
	public void run(String... args) throws Exception {
		this.repository.deleteAll();
		saveCustomers();
		fetchAllCustomers();
		fetchIndividualCustomers();
	}
	
	
	@RequestMapping("/getData")
	String getData() {
		return this.repository.findAll().toString();
	}
	
	private void saveCustomers() {
		this.repository.save(new Customer("Alice", "Smith"));
		this.repository.save(new Customer("Bob", "Smith"));
	}

	private void fetchAllCustomers() {
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : this.repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();
	}

	private void fetchIndividualCustomers() {
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(this.repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : this.repository.findByLastName("Smith")) {
			System.out.println(customer);
		}
	}
}

@RefreshScope
@RestController
class TestController {

    @RequestMapping("/welcome")
    String welcome() {
        return "welcome order service!454545! #1";
    }
}
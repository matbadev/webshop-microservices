package de.hska.iwi.vslab.webshopcompositeinventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableHystrixDashboard
@RibbonClient("inventory-service")
public class WebshopCompositeInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebshopCompositeInventoryServiceApplication.class, args);
	}

}

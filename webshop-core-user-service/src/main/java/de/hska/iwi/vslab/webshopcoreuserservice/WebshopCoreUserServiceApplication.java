package de.hska.iwi.vslab.webshopcoreuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebshopCoreUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebshopCoreUserServiceApplication.class, args);
	}

}

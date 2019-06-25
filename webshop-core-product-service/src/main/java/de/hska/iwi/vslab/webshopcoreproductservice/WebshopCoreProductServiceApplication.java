package de.hska.iwi.vslab.webshopcoreproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServer
public class WebshopCoreProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebshopCoreProductServiceApplication.class, args);
    }

}

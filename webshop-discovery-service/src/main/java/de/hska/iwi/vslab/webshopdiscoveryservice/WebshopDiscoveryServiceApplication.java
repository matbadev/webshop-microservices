package de.hska.iwi.vslab.webshopdiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WebshopDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebshopDiscoveryServiceApplication.class, args);
    }

}

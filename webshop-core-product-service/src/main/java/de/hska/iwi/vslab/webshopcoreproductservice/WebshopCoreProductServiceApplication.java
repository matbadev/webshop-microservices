package de.hska.iwi.vslab.webshopcoreproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@SpringBootApplication
@EnableDiscoveryClient
public class WebshopCoreProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebshopCoreProductServiceApplication.class, args);
    }

    @Configuration
    @EnableResourceServer
    public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    /**/.anyRequest().authenticated()
                    .and()
                    /**/.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        }

    }

    @Configuration
    @EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
    public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    }

}

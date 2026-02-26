package com.dify.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot application entry point for the Dify Java API.
 *
 * Assembles all modules: gateway (controllers, security), domain modules (JPA entities),
 * and worker (async tasks). Matches the Python Flask app in api/dify_app.py.
 */
@SpringBootApplication(scanBasePackages = "com.dify")
@EntityScan(basePackages = "com.dify")
@EnableJpaRepositories(basePackages = "com.dify")
public class DifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DifyApplication.class, args);
    }
}

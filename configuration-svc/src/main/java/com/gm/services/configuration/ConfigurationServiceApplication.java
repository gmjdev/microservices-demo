package com.gm.services.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigurationServiceApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConfigurationServiceApplication.class, args);
	}
}

package com.gm.services.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@Slf4j
public class ConfigurationServiceApplication {
	public static void main(String[] args) {
		log.debug("Hello");
		SpringApplication.run(ConfigurationServiceApplication.class, args);
	}
}

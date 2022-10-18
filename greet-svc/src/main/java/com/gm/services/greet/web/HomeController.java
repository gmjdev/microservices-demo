package com.gm.services.greet.web;

import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@GetMapping("/test")
	public String greet(JwtAuthenticationToken token) {
		return "Hello " + token.getTokenAttributes().get("preferred_username") + ", I am greet-service";
	}

	@GetMapping("/test/delayed")
	public String greetDelayed(JwtAuthenticationToken token) {
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Hello I am greet-service with delay";
	}
}

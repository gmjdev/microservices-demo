package com.gm.services.greet.web;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@GetMapping("/test")
	public String greet() {
		return "Hello I am test-service";
	}

	@GetMapping("/test/delayed")
	public String greetDelayed() {
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Hello I am test-service with delay";
	}
}

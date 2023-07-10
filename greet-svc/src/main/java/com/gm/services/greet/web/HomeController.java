package com.gm.services.greet.web;

import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.services.greet.GreetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HomeController {
	private final GreetService greetService;
	
	@GetMapping("/test")
	public String greet() {
		return "Hello I am greet-service";
	}

	@GetMapping("/test/delayed")
	public String greetDelayed() {
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Hello I am greet-service with delay";
	}

	@GetMapping("/search")
	public ResponseEntity<String> googleSearch(@RequestParam String q) {
		return ResponseEntity.ok(greetService.greet(q));
	}
}

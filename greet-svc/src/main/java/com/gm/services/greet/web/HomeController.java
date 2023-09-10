package com.gm.services.greet.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	private final MeterRegistry registry;

	@GetMapping("/greet")
	public String greet(@RequestParam(defaultValue = "World") String q,
						@RequestParam(defaultValue = "false") boolean highCpu) {
		if (highCpu) {
			while(highCpu) {
				try {
					Runnable r = () -> {
						while(true) {
							// left blank intentionally
						}
					};
					new Thread(r).start();
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.error("Interrupted exception");
					Thread.currentThread().interrupt();
				}
			}
		}
		registry.counter("greetings.total", "name", q).increment();
		return "Hello, " + q;
	}

	@GetMapping("/test/delayed")
	public String greetDelayed() {
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(3));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return "Hello I am greet-service with delay";
	}
}

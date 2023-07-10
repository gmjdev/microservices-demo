package com.gm.services.greet;

import org.springframework.stereotype.Component;

@Component
public class GreetService {

	public String greet(String name) {
		return "Hello " + name;
	}
}

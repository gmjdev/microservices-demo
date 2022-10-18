package com.gm.spring.boot.application;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

public class ApplicationEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventListener.class);
	private static final String INFO_MSG = """

			====================================================================================
			== 		Active Profiles: {0}
			== 		Application Running on port: {1}
			== 		Startup Duration: {2} seconds
			== 		Web Application Type: {3}
			====================================================================================
			""";

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		var port = String.valueOf(getWebServerPort(event.getApplicationContext()));
		var profiles = String.join(",", event.getApplicationContext().getEnvironment().getActiveProfiles());
		profiles = MessageFormat.format(INFO_MSG, profiles, port, event.getTimeTaken().toSeconds(),
				event.getSpringApplication().getWebApplicationType());
		LOG.debug("{}", profiles);
	}

	private int getWebServerPort(ConfigurableApplicationContext context) {
		var port = 8080;
		if (AnnotationConfigServletWebServerApplicationContext.class.isInstance(context)) {
			var webCntxt = (AnnotationConfigServletWebServerApplicationContext) context;
			port = webCntxt.getWebServer().getPort();
		} else if (AnnotationConfigReactiveWebServerApplicationContext.class.isInstance(context)) {
			var webCntxt = (AnnotationConfigReactiveWebServerApplicationContext) context;
			port = webCntxt.getWebServer().getPort();
		}
		return port;
	}

	@EventListener
	public void onApplicationEvent(ApplicationFailedEvent event) {
		LOG.debug("Application failed");
	}
}

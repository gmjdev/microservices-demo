package com.gm.services.undertow.autoconfigure;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.MetricsHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({HttpServerExchange.class})
public class UndertowServerCustomizerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper() {
        return new UndertowMetricsHandlerWrapper();
    }

    @Bean
    @ConditionalOnBean(UndertowMetricsHandlerWrapper.class)
    UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer(
            UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return deploymentInfo ->
                deploymentInfo.addOuterHandlerChainWrapper(undertowMetricsHandlerWrapper);
    }

    @Bean
    @ConditionalOnBean(UndertowMetricsHandlerWrapper.class)
    UndertowMeterBinder undertowMeterBinder(UndertowMetricsHandlerWrapper wrapper) {
        return new UndertowMeterBinder(wrapper);
    }
}

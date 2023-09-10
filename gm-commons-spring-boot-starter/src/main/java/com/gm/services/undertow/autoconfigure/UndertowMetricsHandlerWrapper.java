package com.gm.services.undertow.autoconfigure;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.MetricsHandler;
import lombok.Getter;

@Getter
public class UndertowMetricsHandlerWrapper implements HandlerWrapper {
    private MetricsHandler metricsHandler;

    @Override
    public HttpHandler wrap(HttpHandler handler) {
        metricsHandler = new MetricsHandler(handler);
        return metricsHandler;
    }
}

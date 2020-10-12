package com.github.psexpspace.micronaut.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.*;

import java.io.IOException;

public class CustomLoggingSink implements Sink {

    private final Logger logger;

    public CustomLoggingSink(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
        //noop
    }

    @Override
    public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
        logger.info("Request: endpoint {}, method {}",
                request.getRequestUri(),
                request.getMethod()
        );
    }
}

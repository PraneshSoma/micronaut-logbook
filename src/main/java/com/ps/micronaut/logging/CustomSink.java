package com.ps.micronaut.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.*;

import java.io.IOException;

public class CustomSink implements Sink {

    private final Logger logger;

    public CustomSink(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
        //noop
    }

    @Override
    public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
        String responsePayload = new String(response.getBody(), "UTF-8");

        System.out.println( request.getRequestUri() + " , "+request.getMethod()+ "," +  responsePayload);

    }
}

package com.ps.micronaut.logging;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.http.scope.RequestScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.*;

import javax.inject.Singleton;
import java.io.IOException;

@Factory
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
        System.out.println("******************************************************************************");
        System.out.println( request.getRequestUri() + " , "+request.getMethod()+ "," +  responsePayload);
        System.out.println("******************************************************************************");

    }
}

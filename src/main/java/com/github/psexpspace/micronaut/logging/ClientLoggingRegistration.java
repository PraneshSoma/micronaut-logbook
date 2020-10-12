package com.github.psexpspace.micronaut.logging;

import com.github.psexpspace.micronaut.customlogbook.LogbookClientHandler;
import com.github.psexpspace.micronaut.customlogbook.LogbookServerHandler;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.http.netty.channel.ChannelPipelineCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.Logbook;

import javax.inject.Singleton;

@Singleton
public class ClientLoggingRegistration implements BeanCreatedEventListener<ChannelPipelineCustomizer> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLoggingRegistration.class);

    Logbook logbook = Logbook.create();

    @Override
    public ChannelPipelineCustomizer onCreated(BeanCreatedEvent<ChannelPipelineCustomizer> event) {
        var customizer = event.getBean();
        if (customizer.isClientChannel()) {
            customizer.doOnConnect(pipeline -> {
                return pipeline.addAfter(ChannelPipelineCustomizer.HANDLER_HTTP_CLIENT_CODEC, "logbook", new LogbookClientHandler(logbook));
            });
        }
        if (customizer.isServerChannel()) {
            customizer.doOnConnect(pipeline -> {
                return pipeline.addAfter(ChannelPipelineCustomizer.HANDLER_HTTP_SERVER_CODEC, "logbook", new LogbookServerHandler(logbook));
            });
        }
        return customizer;
    }
}

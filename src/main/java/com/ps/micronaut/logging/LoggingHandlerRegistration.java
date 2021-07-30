package com.ps.micronaut.logging;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.http.netty.channel.ChannelPipelineCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.Logbook;
import com.ps.micronaut.logging.customlog.LogbookServerHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoggingHandlerRegistration implements BeanCreatedEventListener<ChannelPipelineCustomizer> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHandlerRegistration.class);

    private final Logbook logbookInbound = Logbook.builder()
            .sink(new CustomSink("Inbound"))
            .build();



    @Override
    public ChannelPipelineCustomizer onCreated(BeanCreatedEvent<ChannelPipelineCustomizer> event) {
        var customizer = event.getBean();
        if (customizer.isServerChannel()) {
            customizer.doOnConnect(pipeline -> {
                pipeline.addAfter(ChannelPipelineCustomizer.HANDLER_HTTP_SERVER_CODEC, "logbook", new LogbookServerHandler(logbookInbound));
                return pipeline;
            });
        }
        return customizer;
    }
}

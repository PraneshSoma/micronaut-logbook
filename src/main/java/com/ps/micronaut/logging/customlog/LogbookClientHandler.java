package com.ps.micronaut.logging.customlog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;
import org.zalando.logbook.Logbook;

import javax.annotation.concurrent.NotThreadSafe;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;
import static org.zalando.logbook.Logbook.RequestWritingStage;
import static org.zalando.logbook.Logbook.ResponseWritingStage;
import static org.zalando.logbook.Origin.LOCAL;
import static org.zalando.logbook.Origin.REMOTE;
import static com.ps.micronaut.logging.customlog.Conditionals.runIf;

@API(status = EXPERIMENTAL)
@NotThreadSafe
@RequiredArgsConstructor
public final class LogbookClientHandler extends ChannelDuplexHandler {

    private  Sequence sequence;

    private final Logbook logbook;

    private Request request;
    private Response response;
    private RequestWritingStage requestStage;
    private ResponseWritingStage responseStage;

    @Override
    public void write(
            final ChannelHandlerContext context,
            final Object message,
            final ChannelPromise promise) {

        runIf(message, DefaultHttpRequest.class, defaultHttpRequest -> {
            sequence = new Sequence(2);
        });

        runIf(message, HttpRequest.class, httpRequest -> {
            this.request = new Request(context, LOCAL, httpRequest);
            this.requestStage = logbook.process(request);
        });

        runIf(message, HttpContent.class, content -> request.buffer(content.content()));
        runIf(message, ByteBuf.class, request::buffer);

        runIf(message, LastHttpContent.class, content ->
                sequence.set(0, throwingRunnable(requestStage::write)));

        context.write(message, promise);
    }

    @Override
    public void channelRead(
            final ChannelHandlerContext context,
            final Object message) {

        runIf(message, HttpResponse.class, httpResponse -> {
            this.response = new Response(REMOTE, httpResponse);
            this.responseStage = requestStage.process(response);
        });

        runIf(message, HttpContent.class, content -> response.buffer(content.content()));
        runIf(message, ByteBuf.class, response::buffer);

        runIf(message, LastHttpContent.class, content ->
                sequence.set(1, throwingRunnable(responseStage::write)));

        context.fireChannelRead(message);
    }

}

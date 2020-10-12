package com.github.psexpspace.micronaut.customlogbook;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.Origin;
import com.github.psexpspace.micronaut.customlogbook.HeaderSupport;
import com.github.psexpspace.micronaut.customlogbook.State;
import com.github.psexpspace.micronaut.customlogbook.Unbuffered;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

@AllArgsConstructor
@Data
final class Response
        implements org.zalando.logbook.HttpResponse, HeaderSupport {

    private final AtomicReference<State> state =
            new AtomicReference<>(new Unbuffered());

    private final Origin origin;
    private final HttpResponse response;

    @Override
    public String getProtocolVersion() {
        return response.protocolVersion().text();
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    @Override
    public int getStatus() {
        return response.status().code();
    }

    @Override
    public HttpHeaders getHeaders() {
        return copyOf(response.headers());
    }

    @Nullable
    @Override
    public String getContentType() {
        return response.headers().get(CONTENT_TYPE);
    }

    @Override
    public Charset getCharset() {
        // TODO pick the real one
        return StandardCharsets.UTF_8;
    }

    @Override
    public org.zalando.logbook.HttpResponse withBody() {
        state.updateAndGet(State::with);
        return this;
    }

    @Override
    public org.zalando.logbook.HttpResponse withoutBody() {
        state.updateAndGet(State::without);
        return this;
    }

    void buffer(final HttpContent content) {
        state.updateAndGet(state -> state.buffer(response, content));
    }

    @Override
    public byte[] getBody() {
        return state.get().getBody();
    }

}

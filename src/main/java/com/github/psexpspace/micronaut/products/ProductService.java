package com.github.psexpspace.micronaut.products;

import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@Singleton
final class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private static final Map<String, Supplier<Product>> products = new ConcurrentHashMap<>();



    @Inject @Named(TaskExecutors.IO)
    ExecutorService ioExecutor;

    static {
        products.put("PROD-001", createProduct("PROD-001", "Micronaut in Action", 29.99, 1000));
        products.put("PROD-002", createProduct("PROD-002", "Netty in Action", 31.22, 190));
        products.put("PROD-003", createProduct("PROD-003", "Effective Java, 3rd edition", 31.22, 600));
        products.put("PROD-004", createProduct("PROD-004", "Clean Code", 31.22, 1200));
    }

    public Mono<Product> findProductById(final String id) {
        log.debug("Entering findby Product Id...", id);
        return Mono.just(id)
                .map(it -> {
                    log.debug("get product lambda ...", id);
                    return   products.getOrDefault(it, () -> null).get();
                });
    }

    public Mono<String> findProductMonoById(final String id) {
        log.debug("Entering findby Product Id...", id);
        Mono<Product> productMono =  Mono.just(id)
                .subscribeOn(Schedulers.fromExecutor(ioExecutor))
                .map(it -> {
                    log.debug("get product lambda ...", id);
                    return   products.getOrDefault(it, () -> null).get();
                });


        return Mono.just(String.format("Hello %s!", productMono.block().getPrice()));

    }

    private static Supplier<Product> createProduct(final String id, final String name, final Double price, final int latency) {
        log.debug("Entering createProduct ...", id);

        return () -> {
            simulateLatency(latency);
            log.debug("Product with id {} ready to return...", id);
            return new Product(id, name, BigDecimal.valueOf(price));
        };
    }

    private static void simulateLatency(final int millis) {
        log.debug("Entering simulateLatency ...");

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}

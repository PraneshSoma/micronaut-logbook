package com.github.psexpspace.micronaut.products;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micronaut.context.annotation.Factory;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * @author Pranesh Soma
 */
@Singleton

@Factory
public class ProductDataFetcher implements DataFetcher<CompletableFuture<String>> {

@Inject
ProductService productService;

    @Inject
    EventLoopGroup eventLoopGroup;

    private static final Logger log = LoggerFactory.getLogger(ProductDataFetcher.class);

    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment env) {
        return productService.findProductMonoById("PROD-001")
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .doOnSuccess( string -> {
                        log.debug("Product result...");
                }).toFuture();


    }



}
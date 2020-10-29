package com.ps.micronaut.products;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micronaut.context.annotation.Factory;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


@Singleton

@Factory
public class ProductDataFetcher implements DataFetcher<CompletableFuture<String>> {

@Inject
ProductService productService;



    private static final Logger log = LoggerFactory.getLogger(ProductDataFetcher.class);

    @Inject
    EventLoopGroup eventLoopGroup;

    @Inject @Named(TaskExecutors.IO)
    ExecutorService ioExecutor;


    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment env) {
        return productService.findProductMonoById("PROD-001")
                .publishOn(Schedulers.fromExecutor(ioExecutor))
                .doOnSuccess( string -> {
                        log.debug("Product result...");
                }).toFuture();
    }

}

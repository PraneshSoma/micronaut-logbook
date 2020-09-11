package com.github.psexpspace.micronaut.products;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Controller("/product")
final class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

   @Inject
   ProductService productService;


    @Get("/{id}")
    public Observable<Product> getProduct(String id) {
        log.debug("ProductController.getProduct({}) executed...", id);

        return productService.findProductById(id)
                .subscribeOn(Schedulers.io()).doOnNext(
                result -> {
                    log.debug("Result" + result.getId().toLowerCase());
                }
        );
    }
}

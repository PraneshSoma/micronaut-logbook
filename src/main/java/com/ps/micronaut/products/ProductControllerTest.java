/*
package com.ps.micronaut.products;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProductControllerTest {
    private EmbeddedServer server;
    private ProductController client;

    @Before
    public void setup()
    {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().getBean(ProductController.class);
    }

    @After
    public void cleanup()
    {
        server.stop();
    }

    @Test
    public void testGreeting() {

       Product product = client.getProduct("PROD-001");
        System.out.println(product.getId());
    }



    }




*/

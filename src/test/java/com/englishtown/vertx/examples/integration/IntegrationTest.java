package com.englishtown.vertx.examples.integration;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.promises.WhenFactory;
import com.englishtown.vertx.examples.WhenExampleVerticle;
import com.englishtown.vertx.promises.WhenContainer;
import com.englishtown.vertx.promises.WhenEventBus;
import com.englishtown.vertx.promises.WhenHttpClient;
import com.englishtown.vertx.promises.impl.DefaultWhenContainer;
import com.englishtown.vertx.promises.impl.DefaultWhenEventBus;
import com.englishtown.vertx.promises.impl.DefaultWhenHttpClient;
import com.englishtown.vertx.promises.impl.VertxExecutor;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;

import java.net.URI;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Created by adriangonzalez on 3/13/15.
 */
public class IntegrationTest extends TestVerticle {

    private When when;
    private WhenContainer whenContainer;
    private WhenEventBus whenEventBus;
    private WhenHttpClient whenHttpClient;

    @Override
    public void start() {

        when = WhenFactory.createFor(() -> new VertxExecutor(getVertx()));
        whenContainer = new DefaultWhenContainer(getContainer(), when);
        whenEventBus = new DefaultWhenEventBus(getVertx().eventBus(), when);
        whenHttpClient = new DefaultWhenHttpClient(getVertx(), when);

        super.start();

    }

    private Promise<String> deployVerticle() {

        String main = "groovy:" + WhenExampleVerticle.class.getName();
        return whenContainer.deployVerticle(main);

    }

    private Promise<Void> onRejected(Throwable t) {
        t.printStackTrace();
        fail();
        return null;
    }

    @Test
    public void testWhenEventBus() throws Exception {

        deployVerticle()
                .then(id -> whenEventBus.<String>send(WhenExampleVerticle.ADDRESS, "ping"))
                .then(reply -> {
                    assertEquals("pong", reply.body());
                    testComplete();
                    return null;
                })
                .otherwise(this::onRejected);

    }

    @Test
    public void testWhenHttpClient() throws Exception {

        deployVerticle()
                .then(id -> whenHttpClient.requestResponseBody("GET", URI.create("http://localhost:8080")))
                .then(responseAndBody -> {
                    String response = responseAndBody.getBody().toString();
                    assertEquals("pong", response);
                    testComplete();
                    return null;
                })
                .otherwise(this::onRejected);

    }

}

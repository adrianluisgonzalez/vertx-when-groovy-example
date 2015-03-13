package com.englishtown.vertx.examples

import com.englishtown.promises.When
import com.englishtown.promises.WhenFactory
import com.englishtown.vertx.promises.WhenContainer
import com.englishtown.vertx.promises.WhenEventBus
import com.englishtown.vertx.promises.WhenHttpClient
import com.englishtown.vertx.promises.impl.DefaultWhenContainer
import com.englishtown.vertx.promises.impl.DefaultWhenEventBus
import com.englishtown.vertx.promises.impl.DefaultWhenHttpClient
import com.englishtown.vertx.promises.impl.VertxExecutor
import org.vertx.groovy.platform.Verticle

class WhenExampleVerticle extends Verticle {

    private WhenContainer whenContainer;
    private WhenEventBus whenEventBus;
    private WhenHttpClient whenHttpClient;

    public static final String ADDRESS = "when.eb.example";

    def start() {

        initWhen();

        getVertx().getEventBus().registerHandler(ADDRESS, { msg -> msg.reply("pong") });

        getVertx().createHttpServer()
                .requestHandler({ request -> request.getResponse().end("pong") })
                .listen(8080, "localhost");

    }

    def initWhen() {

        // Get instances of java Vertx and Container objects
        org.vertx.java.core.Vertx jVertx = getVertx().toJavaVertx();
        org.vertx.java.platform.Container jContainer = getContainer().jContainer;

        // Create the vert.x executor to queue callbacks on the vert.x event loop
        When when = WhenFactory.createFor({
            return new VertxExecutor(jVertx);
        });

        // when.java vert.x wrappers
        whenContainer = new DefaultWhenContainer(jContainer, when)
        whenEventBus = new DefaultWhenEventBus(jVertx.eventBus(), when);
        whenHttpClient = new DefaultWhenHttpClient(jVertx, when);

    }

}
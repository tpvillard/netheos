package com.biffbangpow.faq;

import com.biffbangpow.faq.app.App;
import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.http.HttpServer;
import com.biffbangpow.faq.http.JettyHttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/**
 * Main entry point.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Main.class);

    private final Configuration config;
    private final ResourceConfig app;
    private HttpServer server;
    private volatile boolean started;


    public Main(Configuration config, ResourceConfig app) throws InterruptedException {
        this.config = config;
        this.app = app;
    }

    public void start() {

        try {
            server = JettyHttpServer.newInstance(config, app);
        } catch (Exception e) {
            // No use for this checked exception, just wrap it.
            throw new RuntimeException(e);
        }
        LOGGER.info("Application started on url {}", server.getBaseURI());
        started = true;
    }

    public void shutdown() {
        if (started) {
            server.shutdown();
            started = false;
        }
        LOGGER.info("Main is shutdown");
    }

    public static void main(String[] args) throws IOException, ConfigAccessException, InterruptedException {

        Thread.currentThread().setName("Main");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception", e));

        try {

            Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
            FaqDAO dao = new FaqDAO(config);
            App app = new App(config, dao);
            final Main exec = new Main(config, app);
            exec.start();
            final CountDownLatch shutdown = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(
                    new Thread("Main ShutdownHook") {
                        @SuppressWarnings("synthetic-access")
                        @Override
                        public void run() {
                            LOGGER.info("Shutdown request is received");
                            exec.shutdown();
                            shutdown.countDown();
                        }
                    });
            shutdown.await();
        } catch (Exception e) {
            LOGGER.error("Error in main", e);
            System.exit(1);
        }
    }

    public URI getBaseURI() {
        return server.getBaseURI();
    }

    public boolean isStarted() {
        return started;
    }
}

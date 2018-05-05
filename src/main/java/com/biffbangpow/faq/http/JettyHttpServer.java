package com.biffbangpow.faq.http;

import com.biffbangpow.faq.config.Configuration;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * A Jetty based http server
 */
public class JettyHttpServer implements HttpServer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JettyHttpServer.class);

    private final Server server;
    private final URI baseUri;


    private JettyHttpServer(Server server, URI baseUri) {
        this.server = server;
        this.baseUri = baseUri;
    }

    @Override
    public void shutdown() {
        server.setStopTimeout(0);
        try {
            server.stop();
        } catch (Exception e) {
            LOGGER.error("Shutdown failed", e);
        }
    }

    @Override
    public URI getBaseURI() {
        return baseUri;
    }

    public static HttpServer newInstance(Configuration config, ResourceConfig app) throws Exception {

        URI baseUri = httpBaseUri(config.getPort());
        Server server = new Server();

        server.setConnectors(new Connector[]{newHttpConnector(config, server)});

        Handler mainHandler = addResources(app);
        Handler resourceHandler = addResourceHandler(config.getDocRoot());

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{resourceHandler, mainHandler});
        server.setHandler(handlers);

        server.start();
        server.setDumpAfterStart(true);
        return new JettyHttpServer(server, baseUri);
    }

    private static URI httpBaseUri(int port) {
        return UriBuilder.fromUri("http://0.0.0.0").port(port).path("faq-api").build();
    }

    private static Handler addResources(ResourceConfig resourceConfig) throws Exception {

        // Setup proxy handler to handle CONNECT methods
        ConnectHandler handler = new ConnectHandler();
        ServletContextHandler context = new ServletContextHandler(handler, "/", ServletContextHandler.SECURITY);

        // Setup jax-rs resources
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(servletContainer);

        String mapping;
        final ApplicationPath ap = resourceConfig.getClass().getAnnotation(ApplicationPath.class);
        if (ap != null) {
            mapping = ap.value() + "/*";
        } else {
            throw new IllegalArgumentException("Invalid application path in resource configuration");
        }
        context.addServlet(servletHolder, mapping);
        return handler;
    }

    private static ResourceHandler addResourceHandler(String docRoot) {

        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers.
        ResourceHandler resourceHandler = new ResourceHandler();
        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(docRoot);
        return resourceHandler;
    }


    private static ServerConnector newHttpConnector(Configuration config, Server server) {

        ServerConnector http = new ServerConnector(server);
        http.setPort(config.getPort());
        return http;
    }
}

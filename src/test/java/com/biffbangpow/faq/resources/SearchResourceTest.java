package com.biffbangpow.faq.resources;


import com.biffbangpow.faq.Main;
import com.biffbangpow.faq.app.App;
import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.model.Faq;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class SearchResourceTest {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(SearchResourceTest.class
            .getName());

    private Main main;
    private WebTarget searchTarget;

    @BeforeClass
    public void setUp() throws ConfigAccessException, JAXBException, IOException, InterruptedException {

        Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
        FaqDAO dao = new FaqDAO(config);
        App app = new App(config, dao);
        main = new Main(config, app);
        main.start();

        // Client configuration
        Client client = getClient();
        searchTarget = client.target(main.getBaseURI()).path("search");
    }

    @AfterClass
    public void tearDown() {
        main.shutdown();
    }

    @Test
    public void should_return_one_faq_for_hennit() {

        List<Faq> faqs = searchTarget
                .queryParam("query", "hennit")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<Faq>>() {});
        Assert.assertEquals(faqs.size(), 1);
    }

    private Client getClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new LoggingFilter(LOGGER, true));
        return ClientBuilder.newClient(clientConfig);
    }
}

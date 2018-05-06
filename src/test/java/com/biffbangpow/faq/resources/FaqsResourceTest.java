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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class FaqsResourceTest {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FaqsResourceTest.class
            .getName());

    private Main main;
    private WebTarget faqsTarget;

    @BeforeClass
    public void setUp() throws ConfigAccessException, JAXBException, IOException, InterruptedException {

        Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
        FaqDAO dao = new FaqDAO(config);
        App app = new App(config, dao);
        main = new Main(config, app);
        main.start();

        // Client configuration
        Client client = getClient();
        faqsTarget = client.target(main.getBaseURI()).path("faqs");
    }

    @AfterClass
    public void tearDown() {
        main.shutdown();
    }

    @Test
    public void should_state_server_is_started() {
        Assert.assertTrue(main.isStarted());
    }

    @Test
    public void should_get_faqs_in_xml_format() {
        getFaqs(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void should_get_faqs_in_json_format() {
        getFaqs(MediaType.APPLICATION_JSON_TYPE);
    }

    public void should_create_a_faq() throws InterruptedException {

        Faq faq = Faq.of("How are you?", "Not bad", "");
        Response response = faqsTarget.request().post(Entity.json(faq));
        if (response.getStatus() != 201) {
            Assert.fail("Should have returned 201 Created");
        }
    }

    private void getFaqs(MediaType mediaType) {
        List<Faq> faqs = faqsTarget.request(mediaType).get(new GenericType<List<Faq>>() {
        });
        Assert.assertEquals(faqs.size(), 3);
    }

    private Client getClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new LoggingFilter(LOGGER, true));
        return ClientBuilder.newClient(clientConfig);
    }
}

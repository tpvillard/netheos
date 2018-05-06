package com.biffbangpow.faq.resources;


import com.biffbangpow.faq.Main;
import com.biffbangpow.faq.app.AppWithAuthentication;
import com.biffbangpow.faq.auth.AuthService;
import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.db.FaqDAO;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public class FaqsResourceWithAuthTest {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FaqsResourceWithAuthTest.class
            .getName());

    private Main main;
    private AuthService authService;
    private WebTarget faqsTarget;
    private WebTarget searchTarget;

    @BeforeClass
    public void setUp() throws ConfigAccessException, JAXBException, IOException, InterruptedException {

        Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
        FaqDAO dao = new FaqDAO(config);
        authService = new AuthService(config);
        AppWithAuthentication app = new AppWithAuthentication(config, dao, authService);
        main = new Main(config, app);
        main.start();

        // Client configuration
        Client client = getClient();
        faqsTarget = client.target(main.getBaseURI()).path("faqs");
        searchTarget = client.target(main.getBaseURI()).path("search");
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
    public void should_return_401_when_no_authorization_header() {

        Response resp = faqsTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        Assert.assertEquals(401, resp.getStatus());
    }

    @Test
    public void should_return_401_when_invalid_access_token() {

        Response resp = faqsTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jhgjfhfjhfgfdh.jghfjhjdhdj.hjhjhgsjhgj").get();
        Assert.assertEquals(401, resp.getStatus());
    }

    @Test
    public void should_return_200_when_admin_get_all_faqs() {

        String jwt = authService.generateToken("bob", true);

        Response resp = faqsTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).get();
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void should_return_200_when_user_search_faqs() {

        String jwt = authService.generateToken("alice", false);

        Response resp = searchTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).get();
        Assert.assertEquals(200, resp.getStatus());
    }


    @Test
    public void should_return_403_when_admin_search_faqs() {

        String jwt = authService.generateToken("bob", true);

        Response resp = searchTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).get();
        Assert.assertEquals(403, resp.getStatus());
    }

    @Test
    public void should_return_403_when_user_get_all_faqs() {

        String jwt = authService.generateToken("alice", false);

        Response resp = faqsTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).get();
        Assert.assertEquals(403, resp.getStatus());
    }

    private Client getClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new LoggingFilter(LOGGER, true));
        return ClientBuilder.newClient(clientConfig);
    }
}

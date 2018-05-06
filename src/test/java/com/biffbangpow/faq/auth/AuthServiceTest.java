package com.biffbangpow.faq.auth;


import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class AuthServiceTest {

    private AuthService authService;

    @BeforeClass
    public void setUp() throws ConfigAccessException, JAXBException, IOException, InterruptedException {

        Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
        authService = new AuthService(config);
    }

    @Test
    public void should_generate_valid_token() {

        String jwt = authService.generateToken("bob", true);
        User bob = authService.validateToken(jwt);
        Assert.assertEquals(bob.getUsername(), "bob");
        Assert.assertEquals(bob.isAdmin(), true);

    }
}

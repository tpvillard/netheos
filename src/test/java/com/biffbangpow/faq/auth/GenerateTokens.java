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

public class GenerateTokens {

    private AuthService authService;

    @BeforeClass
    public void setUp() throws ConfigAccessException, JAXBException, IOException, InterruptedException {

        Configuration config = (new ConfigAccess()).getConf("src/main/resources/config.yaml");
        authService = new AuthService(config);
    }

    @Test
    public void should_generate_valid_token() {

        authService.generateToken("bob", true);
        authService.generateToken("alice", false);
    }
}

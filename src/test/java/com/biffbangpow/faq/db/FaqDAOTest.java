package com.biffbangpow.faq.db;


import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.Faq;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

public class FaqDAOTest {

    private FaqDAO dao;

    @BeforeClass
    public void setup() throws ConfigAccessException {
        ConfigAccess access = new ConfigAccess();
        Configuration config = access.getConf("src/main/resources/config.yaml");
        dao = new FaqDAO(config);
    }


    @Test
    public void should_return_all_faqs() {
        Assert.assertEquals(dao.getFaqs().size(), 3);
    }
}

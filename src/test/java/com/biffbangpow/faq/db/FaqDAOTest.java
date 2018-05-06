package com.biffbangpow.faq.db;

import com.biffbangpow.faq.config.ConfigAccess;
import com.biffbangpow.faq.config.ConfigAccessException;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.Faq;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FaqDAOTest {

    private FaqDAO dao;

    @BeforeClass
    public void setup() throws ConfigAccessException {
        ConfigAccess access = new ConfigAccess();
        Configuration config = access.getConf("src/main/resources/config.yaml");
        dao = new FaqDAO(config);
    }

    public void should_create_a_faq() {
        Faq faq = Faq.of("Quel jour sommes nous?", "Dimanche", "");
        dao.create(faq);
    }

    @Test
    public void should_return_all_faqs() {
        Assert.assertEquals(dao.getFaqs().size(), 3);
    }

    @Test
    public void should_return_1_faq() {
        Assert.assertEquals(dao.searchFaqs("hennit").size(), 1);
    }

    @Test
    public void should_return_3_faq() {
        Assert.assertEquals(dao.searchFaqs("cheval").size(), 3);
    }

    @Test
    public void should_fix_simple_quotes_in_query() {
        Assert.assertEquals(dao.searchFaqs("l'hotel").size(), 0);
    }
}

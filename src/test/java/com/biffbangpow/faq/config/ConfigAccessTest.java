package com.biffbangpow.faq.config;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigAccessTest {

    private ConfigAccess configAccess = new ConfigAccess();

    @Test
    public void should_read_config_from_file() throws ConfigAccessException {
        Configuration config = configAccess.getConf("src/main/resources/config.yaml");
        Assert.assertEquals(config.getPort(), 677);
    }
}

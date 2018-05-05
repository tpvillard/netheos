package com.biffbangpow.faq.config;


import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigAccess {

    /**
     * Returns the application configuration.
     *
     * @return the application configuration
     * @throws ConfigAccessException when configuration file could not be read.
     */
    public Configuration getConf(String fileName) throws ConfigAccessException {
        return getConf(fileName, Configuration.class);
    }

    private <T> T getConf(String fileName, Class<T> confClazz) throws ConfigAccessException {
        try {
            String configFile = System.getProperty("config.file");
            if (configFile != null) {
                fileName = configFile;
            }
            Representer representer = new Representer();
            representer.getPropertyUtils().setSkipMissingProperties(true);
            Yaml yaml = new Yaml(new Constructor(confClazz), representer);
            return (T) yaml.load(readFile(fileName));
        } catch (IOException e) {
            throw new ConfigAccessException(e);
        }
    }

    private String readFile(String fileName) throws IOException {

        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}


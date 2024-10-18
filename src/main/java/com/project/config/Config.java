package com.project.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String environment = "dev";
    // private static final String environment = "prod";
    private static final String envFile = environment == ("prod") ? "config.prod.properties" : "config.dev.properties";

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(envFile)) {
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}

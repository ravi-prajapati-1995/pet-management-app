package org.pet.management.config;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class AppConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (final InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, config.properties not found in classpath");
            } else {
                PROPERTIES.load(input);
            }
        } catch (final Exception ex) {
            log.error("Error while loading properties: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getProp(final String key) {
        return PROPERTIES.getProperty(key);
    }

}

package org.gksx.quipu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigurationTest {
    
    @Test
    public void testBuilder() {
        Configuration configuration = QuipuConfiguration
            .configurationBuilder()
            .port(6378)
            .uri("localhost")
            .poolSize(100)
            .build();

        assertEquals("localhost", configuration.getUri());
        assertEquals(6378, configuration.getPort());
        assertEquals(100, configuration.poolSize());

    }

    @Test
    public void defaultConfiguration() {

        Configuration configuration = QuipuConfiguration.defaultConfiguration();

        assertEquals("127.0.0.1", configuration.getUri());
        assertEquals(6379, configuration.getPort());
        assertEquals(10, configuration.poolSize());
    }
}

package org.gksx;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.gksx.quipu.Configuration;
import org.gksx.quipu.QuipuConfiguration;
import org.gksx.quipu.Quipu;
import org.junit.Test;

public class ConfigurationTest {
    
    @Test
    public void testBuilder() throws IOException{
        Configuration configuration = QuipuConfiguration
            .builder()
            .withPort(6379)
            .withUri("localhost")
            .build();

        assertEquals("localhost", configuration.getUri());
        assertEquals(6379, configuration.getPort());

        var quipu = new Quipu(configuration);

        quipu.close();

    }
}

package org.gksx;

import java.io.IOException;

import org.gksx.quipu.PubSubQuipu;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuException;
import org.junit.Test;

public class PubSubQuipuTest {

    @Test
    public void test_subscrie() throws IOException, QuipuException {
        PubSubQuipu pubSubQuipu = new Quipu()
            .subscribe("channel");

        pubSubQuipu.onEvent((a) -> {
            System.out.println(a);
        });
    }
}

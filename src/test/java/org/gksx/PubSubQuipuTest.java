package org.gksx;

import java.io.IOException;

import org.gksx.quipu.PubSubQuipu;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuException;
import org.junit.Test;

public class PubSubQuipuTest {

    @Test
    public void test_subscrie() {
        PubSubQuipu pubSubQuipu = Quipu
            .pubsub()
            .subscribe("channel");

        pubSubQuipu.close();
    }
}

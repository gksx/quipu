package org.gksx;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.gksx.quipu.Quipu;
import org.junit.Test;

public class QuipuStreamTest {

    @Test
    public void xadd(){
        Quipu qs = new Quipu();
        Map<String,String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        String key = qs.xAdd("mystream", "*", map);
        assertTrue(key != null);
        qs.close();

    }
}

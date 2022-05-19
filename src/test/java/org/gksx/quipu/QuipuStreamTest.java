package org.gksx.quipu;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuipuStreamTest {

    Quipu qs;
    @Before
    public void init(){
        qs = new Quipu();
    }

    @After
    public void destroy(){
        qs.close();
    }


    @Test
    public void xadd(){
        qs.del("mystream");
        Map<String,String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        String key = qs.xAdd("mystream", "*", map);
        assertTrue(key != null);

    }

    @Test
    public void xRange(){
        Map<String,String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        String key = qs.xAdd("mystream", "*", map);
        var resp = qs.xRange("mystream", "-", "+");
    }
}

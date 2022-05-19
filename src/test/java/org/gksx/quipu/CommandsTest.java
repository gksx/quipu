package org.gksx.quipu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandsTest 
{
    Quipu quipu;

    @Before
    public void createConnection() throws IOException{
        quipu = new Quipu();
    }

    @After
    public void closeConnection() throws IOException{
        quipu.close();
    }

    @Test
    public void incr() throws IOException, QuipuException {
        Long expected = 11L;

        quipu.set("mykey", "10");

        Long actual = quipu.incr("mykey");
        
        Assert.assertEquals(expected, actual);        
    }

    @Test
    public void set_and_get() throws IOException, QuipuException {
        quipu.set("hej", "tja");

        var resp = quipu.get("hej");
        assertEquals("tja", resp);
    }

    @Test
    public void setex_and_ttl() throws IOException, QuipuException{
        quipu.setEx("mykey", 10L,"value");

        Long ttl = quipu.ttl("mykey");

        assertTrue(ttl <= 10);
    }

    @Test
    public void set_and_incrBy() throws IOException, QuipuException{
        Long expected = 15L;
        Long value = 10L;
        quipu.set("incrby", value);

        Long actual = quipu.incrBy("incrby", 5L);

        assertEquals(expected, actual);
    }

    @Test
    public void get_nonExisting() throws IOException, QuipuException{
        String nullExpected = quipu.get("nonexisting");

        assertNull(nullExpected);
    }

    @Test 
    public void del_and_append() throws IOException, QuipuException {
        Long expected1 = 5L;
        Long expected2 = 11L;

        quipu.del("append");
        
        Long resp = quipu.append("append", "hello");

        assertEquals(expected1, resp);
        Long resp2 = quipu.append("append", " world");
        assertEquals(expected2, resp2);
    }

    @Test
    public void hset() throws IOException, QuipuException{
        Map<String, String> map = new HashMap<>();

        map.put("hello", "world");
        quipu.hset("mymap", map);
    }

    @Test
    public void multi_and_exec() {
        quipu.multi();
        quipu.set("hello", "world");
        quipu.set("foo", "bar");
        var resp = quipu.exec();

        assertEquals(2, resp.length);
        for (String retval : resp) {
            assertEquals("OK", retval);
        }

        quipu.close();
    }

    @Test
    public void del_and_lpush(){
        quipu.del("mylist");
        Long expected = 1L;
        var count = quipu.lpush("mylist", "element");

        assertEquals(expected, count);
    }

    @Test
    public void del_and_lpush_with_list(){
        quipu.del("mylist1");
        Long expected = 3L;

        List<String> elements = Arrays.asList("first", "second", "third");

        var count = quipu.lpush("mylist1", elements);

        assertEquals(expected, count);
    }

    @Test
    public void lpush_and_lpop(){
        quipu.del("mylist2");
        List<String> elements = Arrays.asList("first", "second", "third");

        quipu.lpush("mylist2", elements);

        var response = quipu.lpop("mylist2");
        assertEquals("third", response);
    }

    @Test
    public void lpush_with_range_and_lpop(){
        quipu.del("mylist3");
        List<String> elements = Arrays.asList("first", "second", "third");

        quipu.lpush("mylist3", elements);

        var response = quipu.lpop("mylist3", 2L);
        assertTrue(response.length == 2);
    }

    @Test
    public void lpush_and_llen(){
        quipu.del("mylist4");
        Long expected = 1L;

        quipu.lpush("mylist4", "element");

        var len = quipu.llen("mylist4");
        assertEquals(expected, len);
    }

    @Test
    public void setRange(){
        quipu.del("setrange");
        var response = quipu.setRange("setrange", 6L, "world");
        assertTrue(response == 11);
    }

    @Test
    public void hset_and_hgetAll(){
        quipu.del("hash");

        Map<String, String> map = new HashMap<>();

        map.put("hello", "world");
        map.put("world", "hello");

        quipu.hset("hash", map);
        var resp = quipu.hgetAll("hash");

        assertTrue(resp.size() == 2);
    }

    @Test
    public void hset_and_hget(){
        quipu.del("hget");

        Map<String, String> map = new HashMap<>();

        var expected = "world";

        map.put("hello", "world");
        map.put("world", "hello");

        quipu.hset("hget", map);

        var actual = quipu.hget("hget", "hello");

        assertEquals(expected, actual);
    }

    @Test
    public void get_set_uft8(){
        var expected = "äää";
        quipu.set("utf8", expected);
        var actual = quipu.get("utf8");

        assertEquals(expected, actual);
    }
}
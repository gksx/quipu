package org.gksx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gksx.quipu.CommandFactory;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuException;
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
    public void shouldBeFormmatted() {

        String[] args = {"get", "tja", "tjä"};

        var bytesExpected = "*3\r\n$3\r\nget\r\n$3\r\ntja\r\n$3\r\ntjä\r\n".getBytes();

        var command = CommandFactory.build(args);

        for (int i = 0; i < command.length; i++) {
            Assert.assertEquals(bytesExpected[i], command[i]);
        }
    }

    @Test
    public void parser() {

        var bytesToParse = "$10\r\nmylisthejs\r\n".getBytes();

        int len = 0;

        for (int i = 1; (char)bytesToParse[i] != '\r'; i++){
            
            len = (len*10) + ((char)bytesToParse[i] -'0');
        }

        Assert.assertEquals(10, len);
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
    }
}
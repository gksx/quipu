package org.gksx;



import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuException;
import org.junit.Assert;
import org.junit.Test;

public class QuipuTest 
{
    @Test
    public void shouldBeFormmatted() {

        String[] args = {"get", "tja", "tjä"};

        var bytesExpected = "*3\r\n$3\r\nget\r\n$3\r\ntja\r\n$3\r\ntjä\r\n".getBytes();

        var q = Quipu.commandBuilder(args);

        for (int i = 0; i < q.length; i++) {
            Assert.assertEquals(bytesExpected[i], q[i]);
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

        Quipu q = new Quipu();

        q.call("set", "mykey", "10");

        var resp = q.call("incr", "mykey");

        q.close();

        Assert.assertEquals(11, Integer.parseInt((String)resp));
    }

    @Test
    public void set_and_get() throws IOException, QuipuException {

        Quipu q = new Quipu();

        q.call("set", "hej", "tja");

        var resp = q.call("get", "hej");
        assertEquals("tja", resp);

        q.close();

    }
}

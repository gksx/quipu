package org.gksx;



import org.gksx.quipu.Quipu;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void shouldBeFormmatted() {

        String[] args = {"get", "tja", "tjä"};

        var bytesExpected = new String("*3\r\n$3\r\nget\r\n$3\r\ntja\r\n$3\r\ntjä\r\n").getBytes();

        var q = Quipu.commandBuilder(args);

        for (int i = 0; i < q.length; i++) {
            System.out.println((char)q[i]);
            Assert.assertEquals(bytesExpected[i], q[i]);
        }
    }

    @Test
    public void parser() {

        var bytesToParse = new String("$10\r\nmylisthejs\r\n").getBytes();

        int len = 0;

        for (int i = 1; (char)bytesToParse[i] != '\r'; i++){
            System.out.println(bytesToParse[i]);
            len = (len*10) + ((char)bytesToParse[i] -'0');
        }

        System.out.println(len);

    }
}

package org.gksx;



import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void shouldBeFormmatted()
    {
        String[] args = {"LLEN", "mylist"};

        var bytesExpected = new String("*2\r\n$4\r\nLLEN\r\n$6\r\nmylist\r\n").getBytes();

        var q = Xedis.commandBuilder(args);

        for (int i = 0; i < q.length; i++) {
            Assert.assertEquals(bytesExpected[i], q[i]);
        }
        
    }
}

package org.gksx;

import java.io.IOException;

import org.gksx.quipu.ConnectionPool;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuException;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionPoolTest {

    @Test
    public void test_connection(){
        try {
            var pool = ConnectionPool.create("localhost", 6379);

            Assert.assertTrue(pool.getConnection().isOpen());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_connection_with_quipu() throws IOException, QuipuException{
        var pool = ConnectionPool.create("localhost", 6379);

        var qs = new Quipu(pool.getConnection());

        qs.call("set", "tja", "hej");
    }
}

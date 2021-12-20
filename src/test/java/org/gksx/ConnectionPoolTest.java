package org.gksx;

import org.gksx.quipu.ConnectionPool;
import org.gksx.quipu.Quipu;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionPoolTest {

    // @Test
    public void test_connection(){
       
        var pool = ConnectionPool.create("localhost", 6379);

        Assert.assertTrue(pool.getConnection().isOpen());
    }

    // @Test
    public void test_connection_with_quipu() {
        var pool = ConnectionPool.create("localhost", 6379);

        var qs = new Quipu(pool.getConnection());

        qs.call("set", "tja", "hej");
    }
}

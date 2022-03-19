package org.gksx;

import org.gksx.quipu.pool.ConnectionPool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionPoolTest {

    private ConnectionPool pool;

    @Before
    public void createPool() {
        pool = ConnectionPool.create();
    }

    @After
    public void closeConnection() {
        pool.releaseAll();
    }

    @Test
    public void test_get_client_and_release(){
        var client = pool.getClient();
        client.set("connection_pool", "test");
        pool.release(client);
    }

    @Test
    public void test_1000_requests() throws InterruptedException{

        var q = pool.getClient();
        q.set("1000steps", 0L);
        pool.release(q);

        for (int i = 0; i < 1000; i++) {
            var client = pool.getClient();
            client.incr("1000steps");
            pool.release(client);
        }

        var q2 = pool.getClient();
        var expected = "1000";
        var actual = q2.get("1000steps");
        Assert.assertEquals(expected, actual);
        pool.release(q2);
    }
}

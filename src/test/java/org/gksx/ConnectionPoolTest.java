package org.gksx;

import org.gksx.quipu.Pool.ConnectionPool;
import org.junit.After;
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
    public void test_get_client(){
        var client = pool.getClient();
        client.set("connection_pool", "test");
        pool.release(client);
    }
}

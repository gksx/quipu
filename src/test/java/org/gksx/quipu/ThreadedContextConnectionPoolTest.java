package org.gksx.quipu;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import org.gksx.quipu.pool.QuipuPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadedContextConnectionPoolTest {

    private QuipuPool pool;

    @Before
    public void createPool() {
        pool = QuipuPool.create();
    }

    @After
    public void closeConnection() {
        pool.releaseAll();
    }


    @Test
    public void test() throws InterruptedException, ExecutionException{
        int threads = 1000;
        List<CompletableFuture<String>> completableFutures = new ArrayList<>();
        var key = "threaded_pool";
        var client = pool.getClient();
        client.del(key);
        pool.release(client);

        for (int i = 0; i < threads; i++) {
            completableFutures.add(CompletableFuture.supplyAsync(
                () -> {
                    var inThreadClient = pool.getClient();
                    inThreadClient.incr(key);
                    var ret = inThreadClient.get(key);
                    pool.release(inThreadClient);
                    return ret;
                })

            );
            
        }
        var cfs = completableFutures.toArray(new CompletableFuture[completableFutures.size()]);

        CompletableFuture.allOf(cfs).join();
                
        var quipu = new Quipu();
        var expected = "1000";
        var actual = quipu.get(key);
        quipu.close();
        assertEquals(expected, actual);
    }
}
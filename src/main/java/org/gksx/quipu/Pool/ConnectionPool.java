package org.gksx.quipu.Pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gksx.quipu.Configuration;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuConfiguration;

public class ConnectionPool {
    private List<QuipuInstance> clientPool;
    
    private static int INITIAL_POOL_SIZE = 10;

    private ConnectionPool(List<QuipuInstance> pool) {
        this.clientPool = Collections.synchronizedList(pool);
    }

    public static ConnectionPool create(){
        return create(QuipuConfiguration.defaultConfiguration());
    }

    public static ConnectionPool create(Configuration configuration) {
        List<QuipuInstance> pool = new ArrayList<>(INITIAL_POOL_SIZE);

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(configuration));
        }
        return new ConnectionPool(pool);
    }

    private static QuipuInstance createConnection(Configuration configuration) {
        return new QuipuInstance(new Quipu(configuration));
    }

    public Quipu getClient(){
        QuipuInstance q = this.clientPool.stream()
            .filter(x -> !x.getIsInUse().get())
            .findFirst()
            .orElseThrow();
        q.setIsInUse();
        return q.getQuipu();
    }

    public void releaseAll() {
        for (QuipuInstance quipu : clientPool) {
            quipu.getQuipu().close();
        }
    }

    public void release(Quipu client) {
        clientPool.stream()
            .filter(q -> q.getQuipu().equals(client))
            .findFirst()
            .orElseThrow()
            .setIsNotInuser();
    }

    
}
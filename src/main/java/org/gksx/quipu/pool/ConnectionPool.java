package org.gksx.quipu.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gksx.quipu.Configuration;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuConfiguration;

public class ConnectionPool implements AutoCloseable {
    private List<QuipuInstance> clientPool;
    
    private static int INITIAL_POOL_SIZE = 10;

    private ConnectionPool(List<QuipuInstance> pool) {
        this.clientPool = Collections.synchronizedList(pool);
    }

    public static ConnectionPool create(){
        return create(QuipuConfiguration.defaultConfiguration());
    }

    public static ConnectionPool create(Configuration configuration) {
        List<QuipuInstance> pool = new ArrayList<>(configuration.poolSize());

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
            if (quipu.getIsInUse().get()){
                throw new QuipuPoolException("client is in use");
            }
            quipu.getQuipu().close();
        }
    }

    public void release(Quipu client) {
        clientPool.stream()
            .filter(q -> q.getQuipu().equals(client))
            .findFirst()
            .orElseThrow()
            .setIsNotInUse();
    }

    @Override
    public void close() {
        releaseAll();   
    }
}
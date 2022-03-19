package org.gksx.quipu.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gksx.quipu.Configuration;
import org.gksx.quipu.Quipu;
import org.gksx.quipu.QuipuConfiguration;

public class QuipuPool implements AutoCloseable {
    private List<PoolInstance> clientPool;
    
    private static int INITIAL_POOL_SIZE = 10;

    private QuipuPool(List<PoolInstance> pool) {
        this.clientPool = Collections.synchronizedList(pool);
    }

    public static QuipuPool create(){
        return create(QuipuConfiguration.defaultConfiguration());
    }

    public static QuipuPool create(Configuration configuration) {
        List<PoolInstance> pool = new ArrayList<>(configuration.poolSize());

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(configuration));
        }
        return new QuipuPool(pool);
    }

    private static PoolInstance createConnection(Configuration configuration) {
        return new PoolInstance(new Quipu(configuration));
    }

    public Quipu getClient(){
        PoolInstance q = this.clientPool.stream()
            .filter(x -> !x.getIsInUse().get())
            .findFirst()
            .orElseThrow();

        q.setIsInUse();
        return q.getQuipu();
    }

    public void releaseAll() {
        for (PoolInstance quipu : clientPool) {
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
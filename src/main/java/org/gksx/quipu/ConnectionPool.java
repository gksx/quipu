package org.gksx.quipu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private List<QuipuStream> connectionPool;
    private List<QuipuStream> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 10;

    public ConnectionPool(List<QuipuStream> pool) {
        this.connectionPool = pool;
    }


    public static ConnectionPool create(String uri, int port) throws IOException{
        List<QuipuStream> pool = new ArrayList<>(INITIAL_POOL_SIZE);

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(uri, port));
        }

        return new ConnectionPool(pool);
    }

    private static QuipuStream createConnection(String uri, int port) throws IOException {
        return new QuipuStream(uri, port);
    }

    public QuipuStream getConnection(){
        QuipuStream qs = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(qs);
        return qs;
    }
}

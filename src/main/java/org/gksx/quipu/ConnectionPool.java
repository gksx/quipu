package org.gksx.quipu;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 10;

    public ConnectionPool(List<Connection> pool) {
        this.connectionPool = pool;
    }


    public static ConnectionPool create(String uri, int port) {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(uri, port));
        }

        return new ConnectionPool(pool);
    }

    private static Connection createConnection(String uri, int port) {
        return new Connection(uri, port);
    }

    public Connection getConnection(){
        Connection qs = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(qs);
        return qs;
    }
}

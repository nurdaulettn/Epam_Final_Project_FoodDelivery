package kz.nurdaulet.config.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomConnectionPool {
    private final BlockingQueue<Connection> pool;

    public CustomConnectionPool(String url, String user, String password, int size)
            throws SQLException {
        pool = new LinkedBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(url, user, password));
        }
    }

    public Connection getConnection() throws InterruptedException {
        Connection real = pool.take();

        return new ProxyConnection(real, this);
    }

    public void releaseConnection(Connection connection) {
        if (connection instanceof ProxyConnection) {
            pool.offer(((ProxyConnection) connection).getRealConnection());
        } else {
            pool.offer(connection);
        }
    }
}

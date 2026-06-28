package kz.nurdaulet.config.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(CustomConnectionPool.class);
    private static final String LOG_CONNECTION_POOL_CREATED = "Created connection pool";
    private static final String LOG_CONNECTION_BORROWED = "Connection borrowed from the pool";
    private static final String LOG_CONNECTION_RETURNED = "Connection returned to the pool";
    private final BlockingQueue<Connection> pool;

    public CustomConnectionPool(String url, String user, String password, int size)
            throws SQLException {
        pool = new LinkedBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(url, user, password));
        }

        log.info(LOG_CONNECTION_POOL_CREATED);
    }

    public Connection getConnection() throws InterruptedException {
        Connection real = pool.take();

        log.debug(LOG_CONNECTION_BORROWED);
        return new ProxyConnection(real, this);
    }

    public void releaseConnection(Connection connection) {
        if (connection instanceof ProxyConnection) {
            pool.offer(((ProxyConnection) connection).getRealConnection());
        } else {
            pool.offer(connection);
        }

        log.debug(LOG_CONNECTION_RETURNED);
    }
}

package kz.nurdaulet.config.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class DatabaseConfig {
    private static final int CONNECTION_POOL_SIZE = 5;

    @Value("${database.driver}")
    private String DB_DRIVER;
    @Value("${database.url}")
    private String DB_URL;
    @Value("${database.username}")
    private String DB_USERNAME;
    @Value("${database.password}")
    private String DB_PASSWORD;

    @Bean
    public CustomConnectionPool connectionPool() throws SQLException {
        return new CustomConnectionPool(DB_URL, DB_USERNAME, DB_PASSWORD, CONNECTION_POOL_SIZE);
    }

    @Bean
    public DataSource dataSource(CustomConnectionPool pool) throws SQLException {
        return new CustomDataSource(pool);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

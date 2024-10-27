package com.project.provider;

import com.project.config.Config;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBConnectionProvider {

    private final JdbcTemplate databaseConnection;

    public DBConnectionProvider(){
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(Config.get("DBDRIVER"));
        basicDataSource.setUrl(Config.get("DBURL"));
        basicDataSource.setUsername(Config.get("DBUSER"));
        basicDataSource.setPassword(Config.get("DBPASSWORD"));

        databaseConnection = new JdbcTemplate(basicDataSource);
    }

    public JdbcTemplate getDatabaseConnection(){
        return databaseConnection;
    }
}

package com.project.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBConnectionProvider {

    private final JdbcTemplate databaseConnection;

    public DBConnectionProvider(){
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(Config.get("DB.DRIVER"));
        basicDataSource.setUrl(Config.get("DB.URL"));
        basicDataSource.setUsername(Config.get("DB.USER"));
        basicDataSource.setPassword(Config.get("DB.PASSWORD"));

        databaseConnection = new JdbcTemplate(basicDataSource);
    }

    public JdbcTemplate getDatabaseConnection(){
        return databaseConnection;
    }
}

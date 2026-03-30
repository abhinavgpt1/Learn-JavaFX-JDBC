package com.example.dbconnectionutil.dbconnection;

import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class PostgreSQLConnection extends DBConnection {
    // Ensure this file exists in dbconfigs directory
    public final static String DB_CONFIG_FILE_NAME = "postgresql-dbconfig.json";

    // Singleton connection pool (lazy loaded)
    private static volatile DataSource dataSource;
    public static Connection getConnection() {
        // Initialise connection pool if null
        if (dataSource == null) {
            synchronized (MySQLConnection.class) {
                if(dataSource == null) {
                    DatabaseConfig dbConfig = DBConnection.getDatabaseConfig(DB_CONFIG_FILE_NAME);
                    PGPoolingDataSource pgDataSource = new PGPoolingDataSource(); // PGConnectionPoolDataSource can be used instead, but then DataSource would be changed to BaseDataSource.
                    pgDataSource.setUrl(dbConfig.url);
                    pgDataSource.setUser(dbConfig.username);
                    pgDataSource.setPassword(dbConfig.password);

                    PostgreSQLConnection.dataSource = pgDataSource;
                }
            }
        }
        return DBConnection.getConnection(dataSource);
    }
}

package com.example.dbconnectionutil.dbconnection;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class SQLServerConnection extends DBConnection {
    // Note: use trustServerCertificate=true in jdbc connection string for local and not for production.
    // eg: jdbc:sqlserver://localhost;encrypt=true;trustServerCertificate=true;databaseName=<databaseName>;

    // Ensure this file exists in dbconfigs directory
    public final static String DB_CONFIG_FILE_NAME = "sqlserver-dbconfig.json";

    // Singleton connection pool (lazy loaded)
    private static volatile DataSource dataSource;
    public static Connection getConnection() {
        // Initialise connection pool if null
        if (dataSource == null) {
            synchronized (MySQLConnection.class) {
                if(dataSource == null) {
                    DatabaseConfig dbConfig = DBConnection.getDatabaseConfig(DB_CONFIG_FILE_NAME);
                    SQLServerDataSource sqlserverDataSource = new SQLServerDataSource(); // PGConnectionPoolDataSource can be used instead, but then DataSource would be changed to BaseDataSource.
                    sqlserverDataSource.setURL(dbConfig.url);
                    sqlserverDataSource.setUser(dbConfig.username);
                    sqlserverDataSource.setPassword(dbConfig.password);

                    SQLServerConnection.dataSource = sqlserverDataSource;
                }
            }
        }
        return DBConnection.getConnection(dataSource);
    }
}

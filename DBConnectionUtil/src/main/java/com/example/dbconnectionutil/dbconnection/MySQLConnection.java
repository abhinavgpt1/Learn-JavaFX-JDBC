package com.example.dbconnectionutil.dbconnection;

// Q - Can MySQLConnection (and others) be made singleton?
// A - No, since this class should be able to make multiple Connection. If it was used in single-user, single-instane app, then it could be singleton.
// Also, by making it singleton, and returning same Connection is problematic. Say, multiple instance of your app are live, then closing one would close the connection which will cause crashes for others, or NPE.
// (above statement is based on fact that on closing app, one should gracefully close all db connections. Going forward we'll see this in all jdbc apps where the connection is shared by Main & Controller, and closed in stop lifecycle of JavaFX app)
//
// Q - Why connection pool? The normal pattern where you provide fileName in DBConnectionFactory getConnection impl. and call DBConnection's getConnection for every database, should be good?
// A - For a small scale app, yes. But, DriverManager.getConnection spins up new connection everytime. Since creating a new connection is costly, we should use connection pool to reuse connections, hence less overheads.
// Also, first one is legacy approach, other is industrial approach. eg. javax.sql.DataSource

// FYI, every java code is vulnerable to Reflection API. Attacker can convert accessibility of datasource from private to public, then it can be altered.
// Check this to understand why singleton as Enum is better, and why eagerly loading with private final field would help.
// - https://github.com/abhinavgpt1/Design-Patterns/blob/master/creational/singleton/impl/SingletonThreadSafe.java

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class MySQLConnection extends DBConnection {
    // Ensure this file exists in dbconfigs directory
    public final static String DB_CONFIG_FILE_NAME = "mysql-xampp-dbconfig.json";

    // Singleton connection pool (lazy loaded)
    private static volatile DataSource dataSource;

    public static Connection getConnection() {
        // Initialise connection pool if null
        if (dataSource == null) {
            synchronized (MySQLConnection.class) {
                if(dataSource == null) {
                    DatabaseConfig dbConfig = DBConnection.getDatabaseConfig(DB_CONFIG_FILE_NAME);
                    MysqlDataSource mysqlDataSource = new MysqlDataSource();
                    mysqlDataSource.setUrl(dbConfig.url);
                    mysqlDataSource.setUser(dbConfig.username);
                    mysqlDataSource.setPassword(dbConfig.password);

                    MySQLConnection.dataSource = mysqlDataSource;
                }
            }
        }
        return DBConnection.getConnection(dataSource);
    }
}

package com.example.dbconnectionutil;

import java.sql.Connection;

enum Database {
    MYSQL, POSTGRES, SQLSERVER
}
public class DBConnectionFactory {
    public static Connection getConnection(Database database) {
        String dbConfigFilePathRootRelative;
        switch (database) {
            case MYSQL:
                dbConfigFilePathRootRelative = MySQLConnection.DB_CONFIG_RELATIVE_FILE_PATH;
                break;
            case POSTGRES:
                dbConfigFilePathRootRelative = PostgreSQLConnection.DB_CONFIG_RELATIVE_FILE_PATH;
                break;
            case SQLSERVER:
                dbConfigFilePathRootRelative = SQLServerConnection.DB_CONFIG_RELATIVE_FILE_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown database: " + database);
        }
        return DBConnection.getConnection(dbConfigFilePathRootRelative);
    }
}

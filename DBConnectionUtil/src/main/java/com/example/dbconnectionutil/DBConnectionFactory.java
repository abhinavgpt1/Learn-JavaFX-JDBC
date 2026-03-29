package com.example.dbconnectionutil;

import com.example.dbconnectionutil.dbconnection.DBConnection;
import com.example.dbconnectionutil.dbconnection.MySQLConnection;
import com.example.dbconnectionutil.dbconnection.PostgreSQLConnection;
import com.example.dbconnectionutil.dbconnection.SQLServerConnection;

import java.sql.Connection;

public class DBConnectionFactory {
    public static Connection getConnection(Database database) {
        String dbConfigFileName;
        switch (database) {
            case MYSQL:
                dbConfigFileName = MySQLConnection.DB_CONFIG_FILE_NAME;
                break;
            case POSTGRES:
                dbConfigFileName = PostgreSQLConnection.DB_CONFIG_FILE_NAME;
                break;
            case SQLSERVER:
                dbConfigFileName = SQLServerConnection.DB_CONFIG_FILE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown database: " + database);
        }
        return DBConnection.getConnection(dbConfigFileName);
    }
}

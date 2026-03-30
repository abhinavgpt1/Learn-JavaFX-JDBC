package com.example.dbconnectionutil;

import com.example.dbconnectionutil.dbconnection.MySQLConnection;
import com.example.dbconnectionutil.dbconnection.PostgreSQLConnection;
import com.example.dbconnectionutil.dbconnection.SQLServerConnection;

import java.sql.Connection;

public class DBConnectionFactory {
    public static Connection getConnection(Database database) {
        switch (database) {
            case MYSQL:
                return MySQLConnection.getConnection();
            case POSTGRES:
                return PostgreSQLConnection.getConnection();
            case SQLSERVER:
                return SQLServerConnection.getConnection();
            default:
                throw new IllegalArgumentException("Unknown database: " + database);
        }
    }
}

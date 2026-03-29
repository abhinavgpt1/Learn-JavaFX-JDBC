package com.example.dbconnectionutil.dbconnection;

// Not making MySQLConnection singleton since we can have multiple different connection with same db.
public class MySQLConnection extends DBConnection {
    // Ensure this file exists in dbconfigs directory
    public final static String DB_CONFIG_FILE_NAME = "mysql-xampp-dbconfig.json";
}

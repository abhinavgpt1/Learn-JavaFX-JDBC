package com.example.dbconnectionutil;

// Not making MySQLConnection singleton since we can have multiple different connection with same db.
public class MySQLConnection extends DBConnection {
    // This path is w.r.t. root i.e. learn-javafx.git
    public final static String DB_CONFIG_RELATIVE_FILE_PATH = "dbconfigs\\mysql-xampp-dbconfig.json";
}

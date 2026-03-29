package com.example.dbconnectionutil.dbconnection;

public class SQLServerConnection extends DBConnection {
    // Note: use trustServerCertificate=true in jdbc connection string for local and not for production.
    // eg: jdbc:sqlserver://localhost;encrypt=true;trustServerCertificate=true;databaseName=<databaseName>;

    // Ensure this file exists in dbconfigs directory
    public final static String DB_CONFIG_FILE_NAME = "sqlserver-dbconfig.json";
}

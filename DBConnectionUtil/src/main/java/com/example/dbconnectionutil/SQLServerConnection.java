package com.example.dbconnectionutil;

public class SQLServerConnection extends DBConnection {
    // Note: use trustServerCertificate=true in jdbc connection string for local and not for production.
    // eg: jdbc:sqlserver://localhost;encrypt=true;trustServerCertificate=true;databaseName=<databaseName>;

    // This path is w.r.t. root i.e. learn-javafx.git
    public final static String DB_CONFIG_RELATIVE_FILE_PATH = "dbconfigs\\sqlserver-dbconfig.json";
}

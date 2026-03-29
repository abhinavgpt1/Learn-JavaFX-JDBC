package com.example.dbconnectionutil.dbconnection;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection {
    public static Connection getConnection(String dbConfigFileName) {
        // Here we've used .json for storing and retrieving jdbc creds
        // Other formats like XML, .properties (Java), YAML can be used too following a Strategy Pattern.
        // ref: https://medium.com/@cillateme/java-database-connections-using-yaml-json-java-properties-and-xml-e9b91bcd22c9

        String dbConfigFilePath = "";
        ObjectMapper mapper = new ObjectMapper();
        DatabaseConfigMapper dbConfig;
        try {
            dbConfigFilePath = "../dbconfigs/" + dbConfigFileName;
            URL dbConfigFileURL = DBConnection.class.getResource(dbConfigFilePath);
            if (dbConfigFileURL == null) {
                System.out.println("File not found: " + dbConfigFilePath);
                throw new FileNotFoundException("File not found: " + dbConfigFilePath);
            }
            // PTR:
            // - getResource() returns a path w.r.t. target folder.
            // - Use maven clean & compile lifecycle/plugin to check whether dbconfigs folder exist in target.
            // - Check dbconfigs/Readme.md to understand why dbconfigs is in resources and not in java.

            File dbConfigFile = new File(dbConfigFileURL.toURI()); // toString returns file:/C:/Users/... (file:/ is ambigous in our case)
            // FYI, File expects a System File Path, but toString() gives it a Network-style URL.
            dbConfig = mapper.readValue(dbConfigFile, DatabaseConfigMapper.class);
        } catch (IOException e) {
            System.out.println("Cannot connect to database with file: " + dbConfigFilePath);
            e.printStackTrace();
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // Note: we can separate dbConfig loading under classes, DBCreds, MySQLCreds, PgCreds, SQLServerCreds.
        // These must not be singleton to be flexible enough to connect with other db creds or db connection/site entirely.

        Connection connection;
        try {
            connection = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
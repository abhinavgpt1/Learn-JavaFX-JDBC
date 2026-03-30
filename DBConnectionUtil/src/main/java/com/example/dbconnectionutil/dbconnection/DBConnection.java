package com.example.dbconnectionutil.dbconnection;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DBConnection {
    public static DatabaseConfig getDatabaseConfig(String dbConfigFileName) {
        // Here we've used .json for storing and retrieving jdbc creds
        // Other formats like XML, .properties (Java), YAML can be used too following a Strategy Pattern.
        // ref: https://medium.com/@cillateme/java-database-connections-using-yaml-json-java-properties-and-xml-e9b91bcd22c9

        String dbConfigFilePath = "";
        ObjectMapper mapper = new ObjectMapper();
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
            return mapper.readValue(dbConfigFile, DatabaseConfig.class);
        } catch (IOException e) {
            System.out.println("Cannot map dbConfig file: " + dbConfigFilePath);
            e.printStackTrace();
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getConnection(DataSource dataSource) {
        // PTR: legacy way to create any db connection
        // Connection connection = DriverManager.getConnection(url, username, password); return connection;
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
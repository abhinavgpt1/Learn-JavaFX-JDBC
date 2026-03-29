package com.example.dbconnectionutil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConfigMapper {
    // Fields need to be public for successful mapping
    // JsonProperty can be used to map customField name in .json to POJO. If not present, then field with same name is searched in json
    // Note: You can't have extra fields in .json (By Default). It should have same number of fields as in POJO unless ObjectMapper is configured to not fail on unknown property discovery.
    @JsonProperty("jdbcConnection")
    public String url;
    public String username;
    public String password;
}

public abstract class DBConnection {
    public static Connection getConnection(String dbConfigRelativeFilePath) {
        // Here we've used .json for storing and retrieving jdbc creds
        // Other formats like XML, .properties (Java), YAML can be used too following a Strategy Pattern.
        // ref: https://medium.com/@cillateme/java-database-connections-using-yaml-json-java-properties-and-xml-e9b91bcd22c9

        ObjectMapper mapper = new ObjectMapper();
        DatabaseConfigMapper dbConfig;
        try {
            File file = new File(dbConfigRelativeFilePath);
            dbConfig = mapper.readValue(file, DatabaseConfigMapper.class);
        } catch (IOException e) {
            System.out.println("Cannot connect to database for config on path: " + dbConfigRelativeFilePath);
            e.printStackTrace();
            return null;
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
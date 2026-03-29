package com.example.application9studentrecordsusingjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost/learnjavafx";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection doConnect(){
        Connection connection;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createStudentsTableIfNotExists(Connection connection){
        /**
         * CREATE TABLE IF NOT EXISTS Students (
         *  rollnumber int PRIMARY key,
         *  name varchar(40) NOT NULL,
         *  percentage float DEFAULT NULL,
         *  dateofadmission DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
         * )
         */
        String createStudentsTableIfNotExists = "CREATE TABLE IF NOT EXISTS Students (rollnumber int PRIMARY key, name varchar(40) NOT NULL, percentage float DEFAULT NULL, dateofadmission DATE NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        try {
            int rowsAffected = connection.prepareStatement(createStudentsTableIfNotExists).executeUpdate();
            if (rowsAffected != 0)
                throw new SQLException("ERROR: Unknown: Table couldn't be created. Please check the issue.");
            else
                System.out.println("INFO: Student table exists or is created");
        } catch (SQLException e) {
            System.out.println("ERROR: Couldn't create Students table" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

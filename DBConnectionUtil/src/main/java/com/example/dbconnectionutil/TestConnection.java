package com.example.dbconnectionutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * In Java's JDBC API, execute(), executeUpdate(), and executeQuery() are methods of the Statement interface used to execute SQL statements, each with a specific purpose and return type:
 *
 * executeQuery(String sql):
 *  Purpose: Exclusively used for executing SELECT statements, which retrieve data from the database.
 *  Return Type: Returns a ResultSet object, which contains the data retrieved by the query. It will never return null, even if no records match the query.
 *  Usage: When you expect to get a set of results back from the database.
 *
 * executeUpdate(String sql):
 *  Purpose: Used for executing Data Manipulation Language (DML) statements like INSERT, UPDATE, and DELETE, as well as Data Definition Language (DDL) statements like CREATE TABLE and DROP TABLE.
 *  Return Type: Returns an int representing the number of rows affected by the SQL statement (for DML operations) or 0 (for DDL operations or DML operations that affected zero rows).
 *  Usage: When you are modifying the database and need to know how many rows were changed.
 *
 * execute(String sql):
 *  Purpose: A general-purpose method that can execute any type of SQL statement, including SELECT, INSERT, UPDATE, DELETE, and DDL statements. It is particularly useful when the type of SQL statement is not known at compile time or when executing stored procedures that might return a ResultSet or an update count.
 *  Return Type: Returns a boolean value:
 *      true if the first result is a ResultSet object (typically from a SELECT statement).
 *      false if the first result is an update count or if there are no results (e.g., from an INSERT, UPDATE, DELETE, or DDL statement).
 *  Usage: After calling execute(), you need to use getResultSet() to retrieve the ResultSet if true was returned, or getUpdateCount() to retrieve the update count if false was returned. This method is more flexible but requires additional logic to handle the different potential outcomes.
 *
 * We learnt:
 * ----------
 * 1. How to connect to a database using JDBC.
 * 2. Different database connections, yet same code to connect.
 * 3. How to execute a query and get results.
 *
 * PTR: We need maven dependencies for different databases.
 * Check dependencies in pom.xml of this project with artifact
 * - mysql-connector-j
 * - postgresql
 * - mssql-jdbc
 * Following is needed to convert json to POJO (XML->POJO can be done too)
 * - jackson-databind
 *
 * FYI: this module was created as JavaFX project (in order to have pom dependencies). Later, javafx plugin and libraries were removed.
 */

public class TestConnection {
    public static void main(String[] args) {
        /**
         * [IMP] Make sure to gracefully disconnect db connection(s).
         * - Resource Exhaustion: Every open connection consumes system resources, including memory (RAM), network sockets, and file descriptors on both the application and database servers.
         * - "Too Many Connections" Errors: Databases have a hard limit on simultaneous connections (e.g., the max_connections setting in MySQL). Once this limit is reached, the database will reject all new connection requests, effectively crashing your application's ability to interact with data.
         * - Database Locks: Unclosed connections can hold onto active transactions or table locks. This can prevent other users from updating data, leading to "deadlocks" or hanging processes across your entire system.
         *
         * Best practices:
         * ---------------
         * Use Try-With-Resources (Java 8+): This automatically closes the connection, statement, and result set when the block finishes, even if an error occurs.
         * Explicitly Close in finally Blocks: If not using try-with-resources, always use a finally block to ensure close() is called regardless of whether the code succeeded or threw an exception.
         * Use Connection Pooling: While pools can still leak, they often include "leak detection" features that log a warning or force-close connections that have been open for too long.
         */

        // Class.forName("com.mysql.cj.jdbc.Driver"); // no need to load drivers
        // No need to load class `com.mysql.jdbc.Driver' because
        // 1. The driver is automatically registered via the SPI (Service Provider Mechanism) and manual loading of the driver class is generally unnecessary.
        // - JDBC 4.0 compliant drivers contain a specific configuration file in their JAR file (META-INF/services/java.sql.Driver). This file tells the JVM which Driver class to load.
        // 2. also, the new driver class is `com.mysql.cj.jdbc.Driver'

        // QQ- what's the point of Class.forName()
        // Ans- loads the class - https://stackoverflow.com/questions/15039265/what-exactly-does-this-do-class-fornamecom-mysql-jdbc-driver-newinstance

        // QQ- how to add drivers?
        // Ans- Maven dependency. Check pom.xml for this, it contains all 3 db's maven dependency.
        // eg. <dependency><groupId>com.mysql</groupId><artifactId>mysql-connector-j</artifactId><version>8.0.33</version></dependency>

        // QQ- PreparedStatement vs CallableStatement?
        // Ans- PreparedStatement is used to execute parameterized SQL queries, while CallableStatement is used to execute stored procedures in the database with an add-on functionality of IN, OUT, INOUT params.
        // ref: https://www.geeksforgeeks.org/java/difference-between-preparedstatement-and-callablestatement/

        // FYI, declare connection, PreparedStatement and Result all in try-with-resources.
        // Reason: Generally, closing connection closes child statements (as seen in File handling BIS/BR), but sometimes this can fail or delay cleanup.
        // This results in Cursor Exhaustion in your database, causing your queries to randomly fail with "Maximum open cursors exceeded" errors.
        try (Connection connection = DBConnectionFactory.getConnection(Database.POSTGRES)) {
            if (connection == null) {
                System.out.println("Connection is null.");
                return;
            }
            PreparedStatement pst = connection.prepareStatement("select * from trainees");
            ResultSet table = pst.executeQuery();
            while (table.next()) {
                System.out.print(table.getFloat("per") + ", ");
                System.out.print(table.getString("sname") + ", ");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Not needed since there's try-with-resources
        // } finally {
        // connection.close();
        // }
    }
}

/**
 * Output (MySQL):
 * ---------------
 * 65.0, Aman,
 * 80.0, Raman,
 * 99.0, Chaman,
 * 98.0, Daman,
 * 79.0, Param,
 *
 * Output (PostgreSQL):
 * --------------------
 * 65.0, Rahul,
 * 80.0, Raghav,
 */
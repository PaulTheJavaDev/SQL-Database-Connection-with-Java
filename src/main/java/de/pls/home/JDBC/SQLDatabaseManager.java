package de.pls.home.JDBC;

import java.sql.*;
import java.util.logging.*;

public class SQLDatabaseManager {

    private final Logger logger = Logger.getLogger(SQLDatabaseManager.class.getName());

    private final String DB_FILE = "sample.db";
    private final String URL = "jdbc:sqlite:" + DB_FILE;

    // A way to call Methods outside methods; very useful for configuration of properties
    {
        configureLogger();
    }

    /**
     * Customizes the logger output format.
     * Removes the default timestamp/class info and shows only the message.
     */
    private void configureLogger() {

        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();

        // Remove default handlers (so we can apply our own format)
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Create a new console handler with a simple format
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {

            // Write into the Console as: e.g. [INFORMATION] Connection was established.
            @Override
            public String format(final LogRecord logRecord) {
                return String.format("[%s] %s%n",
                        logRecord.getLevel().getLocalizedName(),
                        logRecord.getMessage());
            }
        });

        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.INFO);
    }

    /**
     * Entry point for demonstration.
     */
    public static void main(String[] args) {
        SQLDatabaseManager manager = new SQLDatabaseManager();
        manager.runDemo();
    }

    /**
     * Demonstrates connecting to the database, listing users, and inserting a new one.
     */
    public void runDemo() {

        try (Connection connection = DriverManager.getConnection(URL)) {
            logger.info("Connection to the database established successfully.");

            resetUsersTable(connection);

            addUser(connection, "Paul-Ludwig Simon", "paul-Ludwig.Simon@t-online.de");
            addUser(connection, "Michael Müller", "m-müller@cutomMails.com");

            listUsers(connection);

        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }

    }

    private void resetUsersTable(Connection connection) throws SQLException {

        final String SQL_Statement = """
        DROP TABLE IF EXISTS users;
        CREATE TABLE users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE
        );
        INSERT INTO users (name, email) VALUES ('Anna Example', 'anna@example.com');
        INSERT INTO users (name, email) VALUES ('Another User', 'another@example.com');
        """;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_Statement);
        }
    }

    /**
     * Retrieves and logs all users from the 'users' table.
     *
     * @param conn active SQL connection
     * @throws SQLException if any SQL operation fails
     */
    private void listUsers(Connection conn) throws SQLException {

        String SQL_Statement_Selection = "SELECT id, name, email FROM users";

        try (Statement stmt = conn.createStatement();

             ResultSet resultSet = stmt.executeQuery(SQL_Statement_Selection)) {

            logger.info("Users in the database:");
            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                logger.info(String.format("ID: %d | Name: %s | E-Mail: %s", id, name, email));

            }

        }

    }

    /**
     * Inserts a new user into the 'users' table.
     *
     * @param connection  active SQL connection
     * @param name  user name
     * @param email user email
     */
    @SuppressWarnings("unused")
    private void addUser(
            final Connection connection,
            final String name,
            final String email
    ) {

        final String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);

            int rows = preparedStatement.executeUpdate();
            logger.info("User inserted. Rows affected: " + rows);

        } catch (SQLException sqlException) {

            logger.severe(handleSQLError(sqlException.getMessage()));

        }
    }

    /**
     * Custom SQL-Error Handler
     * @param errorMessage SQL Statement-Error to check for the proper custom Error Message
     * @return Custom Error Message
     */
    private String handleSQLError(String errorMessage) {

        errorMessage = errorMessage.toLowerCase();

        String returnValue;

        final String nullErrorMessage = "A NOT NULL constraint failed".toLowerCase();
        final String uniqueFailingErrorMessage = "UNIQUE constraint failed".toLowerCase();

        if (errorMessage.contains(nullErrorMessage)) {
            returnValue = "SQL Statement failed due to an Argument being NULL";
        } else if (errorMessage.contains(uniqueFailingErrorMessage)) {
            returnValue = "SQL Statement failed due to an Argument, which is set to UNIQUE, already being in the database.";
        } else {
            returnValue = "SQL Statement failed: " + errorMessage;
        }

        return returnValue;

    }

    @SuppressWarnings("unused")
    private String getMetaData() {

        return "Database file:\t" + DB_FILE +
                          "\nUrl:\t" + URL;

    }

}

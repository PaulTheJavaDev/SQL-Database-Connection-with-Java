package de.pls.home.utils;

import java.sql.*;
import java.util.logging.Logger;

public class SQLUtils {

    /**
     * Custom SQL-Error Handler
     * @param errorMessage SQL Statement-Error to check for the proper custom Error Message
     * @return Custom Error Message
     */
    public static String handleSQLError(String errorMessage) {

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
    public static void resetUsersTable(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(Settings.SqlStatements.RESET_DATABASE_SQL_STATEMENT);
        }

    }

    /**
     * Retrieves and logs all users from the 'users' table.
     *
     * @param connection active SQL connection
     * @throws SQLException if any SQL operation fails
     */
    @SuppressWarnings("unused")
    private void listUsers(
            final Connection connection,
            final Logger logger
    ) throws SQLException {

        try (Statement stmt = connection.createStatement();

             ResultSet resultSet = stmt.executeQuery(Settings.SqlStatements.GET_ALL_DATA_FROM_USERS)) {

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
     * @param connection active SQL connection
     * @param name       user name
     * @param email      user email
     */
    @SuppressWarnings("unused")
    public static void addUsrIntoDatabase(
            final Connection connection,
            final String name,
            final String email,
            final Logger logger
    ) {

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(Settings.SqlStatements.INSERT_USER_INTO_DATABASE_SQL_STATEMENT)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);

            int rows = preparedStatement.executeUpdate();
            logger.info("User inserted. Rows affected: " + rows);

        } catch (SQLException sqlException) {

            logger.severe(handleSQLError(sqlException.getMessage()));

        }
    }

}

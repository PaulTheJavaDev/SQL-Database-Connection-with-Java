package de.pls.home.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.pls.home.utils.SQLUtils.handleSQLError;

public class MenuOptionUtils {

    public void updateUser(
            final Connection connection,
            final Scanner scanner,
            final Logger logger,
            UserUtils users
    ) {

        try {
            logger.info("User ID:");
            int id = Integer.parseInt(scanner.nextLine().trim());

            logger.info("New name:");
            String name = scanner.nextLine().trim();

            logger.info("New email:");
            String email = scanner.nextLine().trim();

            users.updateUser(connection, id, name, email);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user", e);
        }

    }

    public void deleteUser(
            final Connection connection,
            final Scanner scanner,
            final Logger logger,
            final UserUtils users
    ) {
        try {
            logger.info("User ID to delete:");
            int id = Integer.parseInt(scanner.nextLine().trim());
            users.deleteUser(connection, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete user", e);
        }
    }

    public void deleteDatabase(
            final Connection connection,
            final Scanner scanner,
            final Logger logger
    ) {

        logger.info("Reset database? Type 'yes' to confirm:");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) return;
        try {
            SQLUtils.resetUsersTable(connection);
            logger.info("Database reset complete.");
        } catch (SQLException e) {
            logger.severe(handleSQLError(e.getMessage()));
        }

    }

    public void listAllUsers(
            final Connection connection,
            final Logger logger
    ) {

        try {
            SQLUtils.listUsers(connection, logger);
        } catch (SQLException e) {
            logger.severe(handleSQLError(e.getMessage()));
        }

    }

    public void addUser(
            final Connection connection,
            final Scanner scanner,
            final Logger logger
    ) {

        logger.info("Name:");
        String name = scanner.nextLine().trim();

        logger.info("Email:");
        String email = scanner.nextLine().trim();

        SQLUtils.addUserIntoDatabase(connection, name, email, logger);

    }

}

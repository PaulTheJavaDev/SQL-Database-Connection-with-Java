package de.pls.home.JDBC;

import de.pls.home.utils.Settings;
import de.pls.home.utils.UserUtils;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

import static de.pls.home.utils.SQLUtils.handleSQLError;

public class SQLDatabaseManager {

    private boolean programIsRunning = false;

    private final Logger logger = Logger.getLogger(SQLDatabaseManager.class.getName());

    {
        configureLogger();
    }

    /**
     * Customizes the logger output format.
     * Removes the default timestamp/class info and shows only the message.
     */
    private void configureLogger() {

        // Remove default handlers (so we can apply our own format)
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        // Create a new console handler with a simple format
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            // Write into the Console as: e.g. [INFORMATION] Connection was established.
            @Override
            public String format(final LogRecord recordOfTheLog) {
                return String.format("[%s] %s%n",
                        recordOfTheLog.getLevel().getLocalizedName(),
                        recordOfTheLog.getMessage());
            }
        });

        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
    }

    /**
     * Demonstrates connecting to the database, listing users, and inserting a new one.
     */
    public void runDemo() {

        try (Connection connection = DriverManager.getConnection(Settings.DatabaseSettings.URL);
             Scanner scanner = new Scanner(System.in)) {

            logger.info("Connection to the database established successfully.");
            programIsRunning = true;

            UserUtils userUtils = new UserUtils();

            while (programIsRunning) {

                logger.info("---------------Menu---------------");
                logger.info("0 - Exit");
                logger.info("1 - Update an existing User");
                logger.info("2 - Delete an existing User");
                logger.info("3 - Search by a user's name");
                logger.info("4 - Get the Count of all users");
                logger.info("----------------------------------");
                logger.info("Choose an option:");

                int chosenOption;
                try {
                    chosenOption = Integer.parseInt(scanner.next().trim());
                } catch (NumberFormatException _) {
                    logger.warning("Invalid input. Please enter a number.");
                    continue;
                }

                handleMenuOption(
                        chosenOption,
                        scanner,
                        connection,
                        userUtils
                );

            }

        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }
    }

    private void handleMenuOption(
            final int option,
            final Scanner scanner,
            final Connection connection,
            final UserUtils userUtils
    )
    {

        switch (option) {

            case 0:
                logger.info("Exiting program..");
                programIsRunning = false;
                break;

            case 1:
                try {
                    logger.info("Enter user ID to update:");
                    int id = Integer.parseInt(scanner.next().trim());

                    logger.info("Enter new name:");
                    String name = scanner.next();

                    scanner.nextLine();

                    logger.info("Enter new email:");
                    String email = scanner.nextLine().trim();
                    userUtils.updateUser(connection, id, name, email);
                } catch (Exception e) {
                    logger.severe("Failed to update user: " + e.getMessage());
                }
                break;

            case 2:
                try {
                    logger.info("Enter user ID to delete:");
                    int id = Integer.parseInt(scanner.next().trim());
                    userUtils.deleteUser(connection, id);
                } catch (Exception e) {
                    logger.severe("Failed to delete user: " + e.getMessage());
                }
                break;

            case 3:
                logger.info("Enter name to search:");
                String searchTerm = scanner.next().trim();
                userUtils.searchUserByName(connection, searchTerm);
                break;

            case 4:
                int count = userUtils.getUserCount(connection);
                logger.info("Total users in database: " + count);
                break;

            default:
                logger.warning("Invalid option. Try again.");
                break;
        }

    }

}

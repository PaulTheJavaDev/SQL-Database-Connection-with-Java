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

    private final int EXIT = 0;
    private final int UPDATE = 1;
    private final int DELETE = 2;
    private final int SEARCH_BY = 3;
    private final int COUNT_ALL = 4;

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
            // Write into the console as: e.g. [INFORMATION] Connection was established.
            @Override
            public String format(final LogRecord recordOfTheLog) {
                return String.format("[%s] %s%n",
                        recordOfTheLog.getLevel().getLocalizedName(),
                        recordOfTheLog.getMessage());
            }
        });

        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.INFO);
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

                int chosenOption = Integer.parseInt(scanner.next().trim());

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

            case EXIT:
                logger.info("Exiting program..");
                programIsRunning = false;
                break;

            case UPDATE:
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

            case DELETE:
                try {
                    logger.info("Enter user ID to delete:");
                    int id = Integer.parseInt(scanner.next().trim());
                    userUtils.deleteUser(connection, id);
                } catch (Exception e) {
                    logger.severe("Failed to delete user: " + e.getMessage());
                }
                break;

            case SEARCH_BY:
                logger.info("Enter name to search:");
                String searchTerm = scanner.next().trim();
                userUtils.searchUserByName(connection, searchTerm);
                break;

            case COUNT_ALL:
                int count = userUtils.getUserCount(connection);
                logger.info("Total users in database: " + count);
                break;

            default:
                logger.warning("Invalid option. Try again.");
                break;
        }

    }

}

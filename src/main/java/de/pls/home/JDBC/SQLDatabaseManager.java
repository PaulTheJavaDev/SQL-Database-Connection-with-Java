package de.pls.home.JDBC;

import de.pls.home.utils.MenuOptionUtils;
import de.pls.home.utils.Settings;
import de.pls.home.utils.UserUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.*;

import static de.pls.home.utils.SQLUtils.handleSQLError;

@SuppressWarnings("all")
public class SQLDatabaseManager {

    private boolean programIsRunning = false;

    private final Logger logger = Logger.getLogger(SQLDatabaseManager.class.getName());

    private final int EXIT = 0;
    private final int UPDATE = 1;
    private final int DELETE_USER = 2;
    private final int SEARCH_BY = 3;
    private final int COUNT_ALL = 4;

    private final int RESET_DATABASE = 5;
    private final int LIST_ALL_USERS = 6;
    private final int ADD_USER = 7;


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

                // Not initialising cuz if we get false Input, we are continueing anyways
                int chosenOption;
                try {
                    chosenOption = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
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
            final UserUtils users
    ) {

        MenuOptionUtils menuOptionUtils = new MenuOptionUtils();

        switch (option) {

            case EXIT -> {
                logger.info("Exiting program..");
                programIsRunning = false;
            }

            case UPDATE -> {

                menuOptionUtils.updateUser(
                        connection,
                        scanner,
                        logger,
                        users
                );

            }

            case DELETE_USER -> {

                menuOptionUtils.deleteUser(
                        connection,
                        scanner,
                        logger,
                        users
                );

            }

            case SEARCH_BY -> {
                logger.info("Enter name to search:");
                users.searchUserByName(connection, scanner.nextLine().trim());
            }

            case COUNT_ALL -> {
                int count = users.getUserCount(connection);
                logger.info("Total users in database: " + count);
            }

            case RESET_DATABASE -> {

                menuOptionUtils.deleteDatabase(
                        connection,
                        scanner,
                        logger
                );

            }

            case LIST_ALL_USERS -> {

                menuOptionUtils.listAllUsers(
                        connection,
                        logger
                );

            }

            case ADD_USER -> {

                menuOptionUtils.addUser(
                        connection,
                        scanner,
                        logger
                );

            }

            default -> logger.warning("Invalid option. Try again.");
        }
    }
}

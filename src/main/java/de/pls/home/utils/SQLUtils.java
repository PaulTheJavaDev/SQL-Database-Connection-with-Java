package de.pls.home.utils;

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

}

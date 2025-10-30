package de.pls.home.utils;

public final class Settings {

    public static final class DatabaseSettings {

        // No class needs the File; better readability for the URL
        private static final String DB_FILE = "sample.db";
        public static final String URL = "jdbc:sqlite:" + DB_FILE;

    }

    public static final class SqlStatements {

        public static final String RESET_DATABASE_SQL_STATEMENT = """
        DROP TABLE IF EXISTS users;
        CREATE TABLE users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE
        );
        INSERT INTO users (name, email) VALUES ('Anna Example', 'anna@example.com');
        INSERT INTO users (name, email) VALUES ('Another User', 'another@example.com');
        """;

        public static final String INSERT_USER_INTO_DATABASE_SQL_STATEMENT =
                "INSERT INTO users (name, email) VALUES (?, ?)";

        public static final String GET_ALL_DATA_FROM_USERS =
                "SELECT id, name, email FROM users";

    }

}

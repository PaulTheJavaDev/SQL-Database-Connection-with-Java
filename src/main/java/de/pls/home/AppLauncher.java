package de.pls.home;

import de.pls.home.JDBC.SQLDatabaseManager;

public class AppLauncher {

    public static void main(String[] args) {

        SQLDatabaseManager manager = new SQLDatabaseManager();
        manager.runDemo();

    }

}

package de.pls.home.utils;

import java.sql.*;
import java.util.logging.Logger;

import static de.pls.home.utils.SQLUtils.handleSQLError;

@SuppressWarnings("unused")
public class UserUtils {

    private final Logger logger = Logger.getLogger(this.toString());

    /**
     * Updates an existing user's name and email<br>
     * The user doesn't necessarily need to change both values at the same time.
     *
     * @param connection active SQL connection
     * @param id user-id to update
     * @param newName new name for the user
     * @param newEmail new email for the user
     */
    public void updateUser(
            final Connection connection,
            final int id,
            final String newName,
            final String newEmail
    ) {

        final String Update_User_SQL_Statement = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(Update_User_SQL_Statement)) {

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setInt(3, id);

            final int rowsAffectedByChange = preparedStatement.executeUpdate();

            if (rowsAffectedByChange > 0) {
                logger.info("User updated successfully. Rows affected: " + rowsAffectedByChange);
            } else {
                logger.warning("No user found with ID: " + id);
            }


        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }

    }

    /**
     * Deletes a user from the database by id
     *
     * @param connection active SQL connection
     * @param id user's id to delete
     */
    public void deleteUser(
            final Connection connection,
            final int id
    ) {

        final String Delete_User_SQL_Statement = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(Delete_User_SQL_Statement)) {

            preparedStatement.setInt(1, id);

            updateUserOnSQLStatement(preparedStatement, id);

        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }

    }

    /**
     * Searches for users by the ame using a partial match (case-insensitive).
     *
     * @param connection active SQL connection
     * @param searchTerm term to search for in user's names
     */
    public void searchUserByName(
            final Connection connection,
            final String searchTerm
    ) {

        final String Search_User_By_Name_SQL_Statement =
                "SELECT id, name, email FROM users WHERE LOWER(name) LIKE LOWER(?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(Search_User_By_Name_SQL_Statement)) {

            preparedStatement.setString(1, "%" + searchTerm + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                logger.info("Search results for '" + searchTerm + "':");
                boolean foundAny = false;

                while (resultSet.next()) {
                    foundAny        = true;
                    int id          = resultSet.getInt("id");
                    String name     = resultSet.getString("name");
                    String email    = resultSet.getString("email");
                    logger.info(String.format("ID: %d | Name: %s | E-Mail: %s", id, name, email));

                }

                if (!foundAny) {
                    logger.info("No users found matching '" + searchTerm + "'");
                }

            }

        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }

    }

    /**
     * Returns the total number of users in the database.
     *
     * @param connection active SQL connection
     * @return number of users, or -1 if an error occurs
     */
    public int getUserCount(
            final Connection connection
    ) {

        final String Count_All_Users_In_Database = "SELECT COUNT(*) AS total FROM users";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(Count_All_Users_In_Database)) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }

        } catch (SQLException sqlException) {
            logger.severe(handleSQLError(sqlException.getMessage()));
        }

        return -1;

    }

    private void updateUserOnSQLStatement(final PreparedStatement preparedStatement, final int id) throws SQLException {

        final int rowsAffectedByChange = preparedStatement.executeUpdate();

        if (rowsAffectedByChange > 0) {
            logger.info("User deleted successfully. Rows affected: " + rowsAffectedByChange);
        } else {
            logger.warning("No user found with ID: " + id);
        }

    }

}

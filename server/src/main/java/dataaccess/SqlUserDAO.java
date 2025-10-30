package dataaccess;

import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlUserDAO implements UserDAO{

    //Constructor
    public SqlUserDAO() throws DataAccessException, ResponseException {
        configureUserDatabase();
    }

    public void clear() throws DataAccessException, ResponseException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    //Gets the user data given a username
    public UserData getUser(String username) throws ResponseException {

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    //Places the given UserData in the database
    public void createUser(UserData userData) throws DataAccessException, ResponseException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        var encryptedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        executeUpdate(statement, userData.username(), encryptedPassword, userData.email());
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException, ResponseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> {
                    }
                }
            }
            ps.executeUpdate();
            //ResultSet rs = ps.getGeneratedKeys();

        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureUserDatabase() throws DataAccessException, ResponseException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


}

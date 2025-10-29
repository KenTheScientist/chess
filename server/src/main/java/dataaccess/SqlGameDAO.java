package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGameDAO implements GameDAO{

    //Constructor
    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, " +
                "gameName, game) VALUES (?, ?, ?, ?, ?)";

        String json = new Gson().toJson(gameData.game());

        executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), json);

    }


    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return null;
    }


    public ArrayList<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return result;
    }


    public void updateGame(int gameID, GameData replacingGameData) throws DataAccessException {
        try {
            var statement = "UPDATE game " +
                    "SET gameID = ?, " +
                    "whiteUsername = ?, " +
                    "blackUsername = ?, " +
                    "gameName = ?, " +
                    "game = ? " +
                    "WHERE gameID = ?;";
            String json = new Gson().toJson(replacingGameData.game());
            executeUpdate(statement,
                    replacingGameData.gameID(),
                    replacingGameData.whiteUsername(),
                    replacingGameData.blackUsername(),
                    replacingGameData.gameName(),
                    json,
                    gameID);
        } catch (Exception e) {
            throw new DataAccessException();
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameString = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername,
                gameName, game);
    }

    private void executeUpdate(String statement, Object... parameters) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < parameters.length; i++) {
                    Object param = parameters[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }

                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DataAccessException();
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException();
        }
    }


}

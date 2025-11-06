package chess.server;

public record ClientGameData(int gameID, String whiteUsername, String blackUsername,
                             String gameName) {}

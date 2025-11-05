package ui;

import chess.server.ServerFacade;

public class ChessClient {

    public ChessClient(String serverUrl) {
        var server = new ServerFacade(serverUrl);
    }

}

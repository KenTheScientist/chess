package server.websocket;

import chess.InvalidMoveException;
import chess.ResponseException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.io.InvalidClassException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();


    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
            String name = UserService.authDAO.getAuth(command.getAuthToken()).username();
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, name, wsMessageContext.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(makeMoveCommand, name, wsMessageContext.session);
                }
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        } catch (Exception ex) {
            handleError(ex, wsMessageContext.session);
        }
    }

    public void handleError(Exception ex, Session session) throws IOException {
        //An error occurred, so we should tell whoever sent the message something
        var msg = "";
        if(ex.getClass() == InvalidMoveException.class){
            msg = "Invalid move! Try another move.";
        }
        else {
            msg = ex.getClass().toString();
        }
        connections.broadcastToSession(new ErrorMessage(msg), session);
    }

    public void connect(UserGameCommand command, String name, Session session) throws IOException, ResponseException, DataAccessException {
        //Update the sessions data structure
        connections.add(command.getGameID(), session);

        //Load the game for the player
        var game = GameService.gameDAO.getGame(command.getGameID()).game();
        connections.broadcastToSession(new LoadGameMessage(game), session);

        //Notify all people associated with this game
        var message = String.format("%s joined the game", name);
        connections.broadcastToGame(command.getGameID(), new NotificationMessage(message), null);

    }

    public void makeMove(MakeMoveCommand command, String name, Session session) throws ResponseException, DataAccessException, InvalidMoveException, IOException {
        //Change the game
        var currentGameData = GameService.gameDAO.getGame(command.getGameID());
        currentGameData.game().makeMove(command.getMove());

        GameService.gameDAO.updateGame(command.getGameID(), currentGameData);

        //Load everyone's boards
        connections.broadcastToGame(command.getGameID(),
                new LoadGameMessage(currentGameData.game()), null);

        //Notify everyone else
        var message = String.format("%s made the move %s", name, "FIX ME");
        connections.broadcastToGame(command.getGameID(),
                new NotificationMessage(message), null);


    }

    public void leave(){
    }

    public void resign(){
    }
}

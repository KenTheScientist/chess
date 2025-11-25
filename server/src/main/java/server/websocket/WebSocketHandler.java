package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import chess.ResponseException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.GameData;
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
import java.util.Objects;

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
                case LEAVE -> leave(command, name, wsMessageContext.session);
                case RESIGN -> resign(command, name, wsMessageContext.session);
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
            msg = ex.getMessage();
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
        var message = String.format("%s joined the game.", name);
        connections.broadcastToGame(command.getGameID(), new NotificationMessage(message), session);

    }

    public void makeMove(MakeMoveCommand command, String name, Session session) throws Exception {
        //Change the game
        var currentGameData = GameService.gameDAO.getGame(command.getGameID());
        var move = command.getMove();
        ChessGame.TeamColor currentTeam = null;
        if(Objects.equals(currentGameData.whiteUsername(), name)) {
            currentTeam = ChessGame.TeamColor.WHITE;
        }
        if(Objects.equals(currentGameData.blackUsername(), name)) {
            currentTeam = ChessGame.TeamColor.BLACK;
        }

        //If we're an observer, we shouldn't be making any moves!
        if(currentTeam == null) {
            throw new Exception("You are observing the game. You cannot make any moves.");
        }

        //If the game is over
        if(!currentGameData.game().gameInProgress){
            throw new Exception("The game is over. You cannot make any moves.");
        }

        //If it's not our turn, we shouldn't be making any moves!
        if(currentTeam != currentGameData.game().getTeamTurn()){
            throw new Exception("Please wait for your turn.");
        }

        if(!currentGameData.game().getBoard().hasPiece(move.getStartPosition())){
            throw new InvalidMoveException();//Tried to move an empty square
        }
        else if(currentGameData.game().getBoard().getPiece(move.getStartPosition()).getTeamColor() != currentTeam){
            throw new InvalidMoveException();//Tried to move an opponent's piece
        }

        //No errors? Success!
        currentGameData.game().makeMove(move);

        //Switch the current team
        if(currentTeam == ChessGame.TeamColor.WHITE){
            currentGameData.game().setTeamTurn(ChessGame.TeamColor.BLACK);
        }
        if(currentTeam == ChessGame.TeamColor.BLACK){
            currentGameData.game().setTeamTurn(ChessGame.TeamColor.WHITE);
        }

        //Update the database
        GameService.gameDAO.updateGame(command.getGameID(), currentGameData);

        //Load everyone's boards
        connections.broadcastToGame(command.getGameID(),
                new LoadGameMessage(currentGameData.game()), null);

        //Notify everyone else
        var columnLabels = "abcdefgh";
        var rowLabels = "12345678";
        var col1 = columnLabels.charAt(move.getStartPosition().getColumn()-1);
        var row1 = rowLabels.charAt(move.getStartPosition().getRow()-1);
        var col2 = columnLabels.charAt(move.getEndPosition().getColumn()-1);
        var row2 = rowLabels.charAt(move.getEndPosition().getRow()-1);

        var message = String.format("%s just made a move from %s%s to %s%s.", name, col1, row1, col2, row2);
        connections.broadcastToGame(command.getGameID(),
                new NotificationMessage(message), session);


    }

    public void leave(UserGameCommand command, String name, Session session) throws IOException, ResponseException, DataAccessException {
        //Leave the websocket session dataset
        connections.remove(command.getGameID(),session);

        //Leave the SQL database entry
        var currentGameData = GameService.gameDAO.getGame(command.getGameID());
        ChessGame.TeamColor currentTeam = null;
        if(Objects.equals(currentGameData.whiteUsername(), name)) {
            //Set whiteUsername to null
            currentGameData = new GameData(
                    currentGameData.gameID(),
                    null,
                    currentGameData.blackUsername(),
                    currentGameData.gameName(),
                    currentGameData.game()
            );
        }
        else if(Objects.equals(currentGameData.blackUsername(), name)) {
            currentGameData = new GameData(
                    currentGameData.gameID(),
                    currentGameData.whiteUsername(),
                    null,
                    currentGameData.gameName(),
                    currentGameData.game()
            );
        }

        GameService.gameDAO.updateGame(command.getGameID(), currentGameData);

        //Notify the leaving to everyone else
        var message = String.format("%s has left the game.", name);
        connections.broadcastToGame(command.getGameID(), new NotificationMessage(message), session);
    }

    public void resign(UserGameCommand command, String name, Session session) throws Exception {

        var currentGameData = GameService.gameDAO.getGame(command.getGameID());
        ChessGame.TeamColor currentTeam = null;
        if(Objects.equals(currentGameData.whiteUsername(), name)) { currentTeam = ChessGame.TeamColor.WHITE; }
        if(Objects.equals(currentGameData.blackUsername(), name)) { currentTeam = ChessGame.TeamColor.BLACK; }

        //If we're an observer, we shouldn't be resigning!
        if(currentTeam == null) {
            throw new Exception("You are observing the game. You cannot resign.");
        }

        //If the game is already over, we can't resign!
        if(!currentGameData.game().gameInProgress){
            throw new Exception("The game is over. You cannot resign.");
        }

        //Mark the game as over
        currentGameData.game().gameInProgress = false;

        GameService.gameDAO.updateGame(command.getGameID(), currentGameData);

        //Notify the resignation to everyone!!
        var message = String.format("%s has resigned!", name);
        connections.broadcastToGame(command.getGameID(), new NotificationMessage(message), null);

    }
}

package ui;

import chess.*;
import chess.server.ClientCreateGameResult;
import chess.server.ClientGameData;
import chess.server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient implements NotificationHandler {

    private final ServerFacade serverFacade;
    private final WebsocketFacade websocketFacade;
    private String authToken;
    private int currentGameID;
    private ChessGame currentGame = new ChessGame();
    private String currentColor = "WHITE";

    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME
    }

    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        websocketFacade = new WebsocketFacade(serverUrl, this);
    }

    public void run(){
        System.out.println("Welcome to Chess! Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            if(state != State.INGAME) { System.out.print(">>>"); }
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.println(msg);
                System.out.print("\n");
            }
        }

    }

    // Command functions
    public String register(String[] params) throws ResponseException {
        //register(username, password, email)
        assertSignedOut();
        if(params.length >= 3) {
            var loginResult = serverFacade.register(params[0], params[1], params[2]);
            state = State.SIGNEDIN;
            authToken = loginResult.authToken();
            return String.format("You are now registered and logged in as %s", loginResult.username());
        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }

    }
    public String login(String[] params) throws ResponseException {
        //login(username, password)
        assertSignedOut();
        if(params.length >= 2) {
            var loginResult = serverFacade.login(params[0], params[1]);
            state = State.SIGNEDIN;
            authToken = loginResult.authToken();
            return String.format("You are now logged in as %s", loginResult.username());

        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD>");
        }
    }

    public String listGames() throws ResponseException {
        //listgames()
        assertSignedIn();
        var listGamesResult = serverFacade.listGames(authToken);
        StringBuilder out = new StringBuilder();
        var list = listGamesResult.games;
        if(list.isEmpty()) {
            return "No games available.";
        }
        for(int i = 0; i < list.size(); i++){
            ClientGameData data = list.get(i);
            out.append(i + 1);
            out.append(" - ");
            out.append(data.gameName());
            out.append(" - White: ");
            out.append(data.whiteUsername());
            out.append(" - Black: ");
            out.append(data.blackUsername());
            out.append("\n");
        }
        return out.toString();
    }

    public String playGame(String[] params) throws ResponseException {
        //playGame(gameNumber, color)
        assertSignedIn();
        if(params.length >= 2) {
            var attemptedColor = params[1];
            if(!(attemptedColor.equalsIgnoreCase("white") || attemptedColor.equalsIgnoreCase("black"))){
                throw new ResponseException(ResponseException.Code.ClientError, "Please put 'white' or 'black' as your color.");
            }
            ClientGameData foundGameData = findGame(params[0]);
            serverFacade.joinGame(authToken,params[1].toUpperCase(),foundGameData.gameID());
            currentGameID = foundGameData.gameID();
            state = State.INGAME;
            currentColor = attemptedColor.toUpperCase();
            websocketFacade.connect(authToken, currentGameID);


            return String.format("Successfully joined game %s as %s!",
                    foundGameData.gameName(), params[1].toUpperCase());

        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <GAME NUMBER> <PLAYER COLOR>");
        }
    }

    public String createGame(String[] params) throws ResponseException {
        //createGame(gameName)
        assertSignedIn();
        if(params.length >= 1) {
            ClientCreateGameResult result = serverFacade.createGame(params[0],authToken);
            return String.format("Successfully created game %s", params[0]);
        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <GAME NAME>");
        }
    }

    public String observeGame(String[] params) throws ResponseException {
        //createGame(gameName)
        assertSignedIn();
        if(params.length >= 1) {
            ClientGameData foundGameData = findGame(params[0]);
            state = State.INGAME;
            currentGameID = foundGameData.gameID();
            currentColor = "WHITE";
            return String.format("Successfully observing game %s", params[0]);
        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <GAME NAME>");
        }
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        serverFacade.logout(authToken);
        state = State.SIGNEDOUT;
        authToken = null;
        return "Successfully signed out.";
    }

    //Websocket (phase 6)

    public String redraw() {
        return BoardRenderer.render(currentGame.getBoard(), currentColor);
    }

    public String leave() {
        websocketFacade.leave(authToken, currentGameID);
        state = State.SIGNEDIN;
        return "You left the game.";
    }

    public String move(String[] params) throws ResponseException {
        //Validate move parameter
        if(params.length == 0){
            throw new ResponseException(ResponseException.Code.ClientError, "Please input a valid move (e.g. move e5f6)");
        }
        var moveText = params[0];
        if(moveText.length() != 4){
            throw new ResponseException(ResponseException.Code.ClientError, "Please input a valid move (e.g. move e5f6)");
        }
        var columnLabels = "abcdefgh";
        var rowLabels = "12345678";
        int col1 = 1;
        int row1 = 1;
        int col2 = 1;
        int row2 = 1;
        try {
            col1 = columnLabels.indexOf(moveText.charAt(0)) + 1;
            row1 = rowLabels.indexOf(moveText.charAt(1)) + 1;
            col2 = columnLabels.indexOf(moveText.charAt(2)) + 1;
            row2 = rowLabels.indexOf(moveText.charAt(3)) + 1;
            if(col1 == 0 || row1 == 0 || col2 == 0 || row2 == 0){
                throw new IndexOutOfBoundsException();
            }
        }
        catch(IndexOutOfBoundsException e){
            throw new ResponseException(ResponseException.Code.ClientError, "Please input a valid move (e.g. move e5f6)");
        }

        //Validate promotion piece
        String promotionPiece = "";
        ChessPiece.PieceType promotionPieceType = null;
        if(params.length > 1){
            promotionPiece = params[1];
            //The user wants to select a promotion piece
            if(promotionPiece.equalsIgnoreCase("QUEEN")){
                promotionPieceType = ChessPiece.PieceType.QUEEN;
            }
            else if(promotionPiece.equalsIgnoreCase("ROOK")){
                promotionPieceType = ChessPiece.PieceType.ROOK;
            }
            else if(promotionPiece.equalsIgnoreCase("KNIGHT")){
                promotionPieceType = ChessPiece.PieceType.KNIGHT;
            }
            else if(promotionPiece.equalsIgnoreCase("BISHOP")){
                promotionPieceType = ChessPiece.PieceType.BISHOP;
            }
            else {
                throw new ResponseException(ResponseException.Code.ClientError,
                        "If you are promoting, please input queen, rook, knight, or bishop as the promotion piece.");
            }

        }

        ChessMove outMove = new ChessMove(
                new ChessPosition(row1,col1),
                new ChessPosition(row2,col2),
                promotionPieceType
                );

        websocketFacade.makeMove(authToken, currentGameID, outMove);

        return "Processing move...";
    }

    public String resign() {
        return "Resign! (fix this ken)";
    }

    public String showMoves(String[] params) {
        return "Show moves! (fix this ken)";
    }

    @Override
    public void notify(ServerMessage message){
        try {
            switch (message.getServerMessageType()) {
                case ERROR:
                    System.out.println(((ErrorMessage) message).getErrorMessage());
                    break;
                case LOAD_GAME:
                    System.out.println(
                            BoardRenderer.render(
                                    ((LoadGameMessage) message).getGame().getBoard(), currentColor
                            )
                    );
                    currentGame = ((LoadGameMessage) message).getGame();
                    break;
                case NOTIFICATION:
                    System.out.println(((NotificationMessage) message).getMessage());
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //Helper functions

    public ClientGameData findGame(String gameNumber) throws ResponseException {
        var listGameResult = serverFacade.listGames(authToken);
        try {
            int searchingIndex = Integer.parseInt(gameNumber) - 1;
            return listGameResult.games.get(searchingIndex);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(ResponseException.Code.ClientError, "Please input a number.");
        }
        catch (IndexOutOfBoundsException e) {
            throw new ResponseException(ResponseException.Code.ClientError, "Invalid game number.");
        }

    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "help" -> help();

                case "listgames" -> listGames();
                case "playgame" -> playGame(params);
                case "creategame" -> createGame(params);
                case "observegame" -> observeGame(params);
                case "logout" -> logout();

                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "showmoves" -> showMoves(params);

                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to log in to your account
                    - quit - quit the application
                    - help - get help with possible commands
                    """;
        }
        else if(state == State.SIGNEDIN) {
            return """
                    - listgames - get a list of active games
                    - playgame <GAME NUMBER> <PLAYER COLOR> - join an online game
                    - creategame <GAME NAME> - create a new online game
                    - observegame - observe a game
                    - logout - log out of account
                    - help - get help with possible commands
                    """;
        }
        else if(state == State.INGAME) {
            return """
                    - redraw - redraw the chess board
                    - move <MOVE> (PROMOTION PIECE) - make a move. Examples: move e5f6, move a7a8 queen
                    - showmoves <POSITION> - highlight the legal moves of a chess piece. Example: showmoves e5
                    - resign - forfeit the game
                    - leave - leave the game
                    - help - get help with possible commands
                    """;
        }

        return "";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must be sign out first");
        }
    }


}

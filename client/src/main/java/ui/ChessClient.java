package ui;

import chess.ChessGame;
import chess.ResponseException;
import chess.server.ClientCreateGameResult;
import chess.server.ClientGameData;
import chess.server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade serverFacade;
    private String authToken;
    private int currentGameID;
    private ChessGame currentGame = new ChessGame();
    private String currentColor = "WHITE";

    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME,
        OBSERVING
    }

    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to Chess! Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            if(state == State.INGAME || state == State.OBSERVING) {
                //System.out.print(BoardRenderer.render(currentGame.gameBoard, currentColor));
                System.out.print("\n");
            }
            System.out.print(">>>");
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

    //Game functions


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
            state = State.OBSERVING;
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

                case "redraw" -> help();
                case "leave" -> help();
                case "makemove" -> help();
                case "resign" -> help();
                case "highlightlegalmoves" -> help();

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
                    - listgames - get a list of active games
                    - playgame <GAME NUMBER> <PLAYER COLOR> - join an online game
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

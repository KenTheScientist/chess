package ui;

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

    public enum State {
        SIGNEDOUT,
        SIGNEDIN
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
            System.out.print(">>>");
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
            } catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
                e.printStackTrace();
                System.out.print("\n");
            }

        }
    }

    // Command functions
    public String register(String[] params) throws ResponseException {
        //register(username, password, email)
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
        var listGamesResult = serverFacade.listGames(authToken);
        StringBuilder out = new StringBuilder();
        var list = listGamesResult.games;
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
        if(params.length >= 2) {
            var listGameResult = serverFacade.listGames(authToken);
            int searchingIndex = Integer.parseInt(params[0])-1;
            ClientGameData foundGameData = listGameResult.games.get(searchingIndex);
            serverFacade.joinGame(authToken,params[1].toUpperCase(),foundGameData.gameID());
            currentGameID = foundGameData.gameID();
            return String.format("Successfully joined game %s as %s!",
                    foundGameData.gameName(), params[1].toUpperCase());

        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <GAME NUMBER> <PLAYER COLOR>");
        }
    }

    public String createGame(String[] params) throws ResponseException {
        //createGame(gameName)
        if(params.length >= 1) {
            ClientCreateGameResult result = serverFacade.createGame(params[0],authToken);
            return String.format("Successfully created game %s", params[0]);
        }
        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <GAME NAME>");
        }
    }





    //Helper functions

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
                case "observegame" -> help();
                case "logout" -> help();

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
        return """
                - listgames - get a list of active games
                - playgame - join an online game
                - creategame - create a new online game
                - observegame - observe a game
                - logout - log out of account
                - help - get help with possible commands
                """;
    }
}

package ui;

import chess.ResponseException;
import chess.server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade serverFacade;


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
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
            } catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }

        }
    }

    // Command functions
    public String register(String[] params) throws ResponseException {
        //register(username, password, email)
        serverFacade.register(params[0],params[1],params[2]);
        return "Done";
    }
    public String login(String[] params) {
        //login(username, password)

        return "Done";
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

                case "listgames" -> help();
                case "playgame" -> help();
                case "creategame" -> help();
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

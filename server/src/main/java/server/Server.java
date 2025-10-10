package server;

import handlers.*;
import dataaccess.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //Register a user
        javalin.post("/user", context -> (new RegisterHandler()).handle(context));
        //Log in a user
        javalin.post("/session", context -> (new RegisterHandler()).handle(context));
        //Logs out an authenticated user
        javalin.delete("/session", context -> (new RegisterHandler()).handle(context));
        //Lists all the games in the database
        javalin.get("/game", context -> (new RegisterHandler()).handle(context));
        //Create a new Chess game
        javalin.post("/game", context -> (new RegisterHandler()).handle(context));
        //Join a Chess Game
        javalin.put("/game", context -> (new RegisterHandler()).handle(context));
        //Clear ALL data from the database
        javalin.delete("/db", context -> (new RegisterHandler()).handle(context));



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

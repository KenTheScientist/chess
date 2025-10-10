package handlers;

//My packages

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.CreateGameRequest;
import request.ListGamesRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.GameService;


public class CreateGameHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        String body = context.body();
        var serializer = new Gson();
        try {
            //First we're going to deserialize the body
            var authToken = context.header("Authorization");
            var createGameRequest = serializer.fromJson(body, CreateGameRequest.class);

            if(authToken == null)
            {
                throw new DataAccessException();
            }

            //Now we're going to call for service
            CreateGameResult createGameResult = GameService.createGame(createGameRequest, authToken);
            //We convert the result to a JSON string
            var result = serializer.toJson(createGameResult);

            //We output the JSON string
            context.result(result);
        }
        catch (DataAccessException e)
        {
            context.status(401);
            context.result("{\"message\": \"Error: unauthorized\"}");
        }
        catch (JsonParseException e)
        {
            context.status(400);
            context.result("{\"message\": \"Error: bad request\"}");
        }
        catch (Exception e) {
            context.status(500);
            context.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
        finally{

        }
    }


}

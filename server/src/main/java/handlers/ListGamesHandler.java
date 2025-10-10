package handlers;

//My packages

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import datamodel.ListGameData;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.ListGamesResult;
import service.GameService;
import service.UserService;

import java.util.ArrayList;


public class ListGamesHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        String body = context.body();
        var serializer = new Gson();
        try {
            //First we're going to deserialize the body
            var authToken = context.header("Authorization");

            if(authToken == null)
            {
                throw new DataAccessException();
            }

            //Now we're going to call for service
            ListGamesResult gameList = GameService.listGames(new ListGamesRequest(authToken));

            //We convert the result to a JSON string
            var result = serializer.toJson(gameList);

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

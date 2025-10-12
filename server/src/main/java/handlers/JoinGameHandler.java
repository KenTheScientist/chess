package handlers;

//My packages

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import service.GameService;


public class JoinGameHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        String body = context.body();
        var serializer = new Gson();
        try {
            //First we're going to deserialize the body
            var authToken = context.header("Authorization");
            var joinGameRequest = serializer.fromJson(body, JoinGameRequest.class);

            if(authToken == null)
            {
                throw new DataAccessException();
            }

            //Now we're going to call for service
            GameService.joinGame(joinGameRequest, authToken);

            //We output the JSON string
            context.result("{ }");


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

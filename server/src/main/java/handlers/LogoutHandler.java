package handlers;

//My packages

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.LoginRequest;
import request.LogoutRequest;
import result.LoginResult;
import service.UserService;


public class LogoutHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        String body = context.body();
        var serializer = new Gson();
        try {
            //First we're going to deserialize the body
            var request = serializer.fromJson(body, LogoutRequest.class);

            //Now we're going to call for service
            UserService.logout(request);

            //We convert the result to a JSON string
            var result = "{ }";

            //We output the JSON string
            context.result(result);
        }
        catch (UnauthorizedException e)
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

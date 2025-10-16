package handlers;

//My packages
import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.LogoutRequest;
import service.UserService;


public class LogoutHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        try {
            //First we're going to deserialize the body
            var authToken = context.header("Authorization");

            if(authToken == null)
            {
                throw new DataAccessException();
            }

            //Now we're going to call for service
            UserService.logout(new LogoutRequest(authToken));

            //We convert the result to a JSON string
            var result = "{ }";

            //We output the JSON string
            context.result(result);
        }
        catch (DataAccessException exe)
        {
            context.status(401);
            context.result("{\"message\": \"Error: unauthorized\"}");
        }
        catch (JsonParseException exe)
        {
            context.status(400);
            context.result("{\"message\": \"Error: bad request\"}");
        }
        catch (Exception exe) {
            context.status(500);
            context.result("{ \"message\": \"Error: " + exe.getMessage() + "\" }");
        }

    }


}

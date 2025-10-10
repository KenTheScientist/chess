package handlers;

//My packages
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import dataaccess.AlreadyTakenException;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

//External Dependencies
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;


public class RegisterHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String body = context.body();
        var serializer = new Gson();
        try {
            //First we're going to deserialize the body
            var request = serializer.fromJson(body, RegisterRequest.class);

            //Now we're going to call for service
            RegisterResult registerResult = UserService.register(request);

            //We convert the result to a JSON string
            var result = serializer.toJson(registerResult);

            //We output the JSON string
            context.result(result);
        }
        catch (AlreadyTakenException e)
        {
            context.status(403);
            context.result("{\"message\": \"Error: already taken\"}");
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

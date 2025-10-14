package handlers;

//My packages

import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;


public class ClearApplicationHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        try {
            UserService.clearApplication();
            context.result("{ }");
        }
        catch (Exception e) {
            context.status(500);
            context.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }



}

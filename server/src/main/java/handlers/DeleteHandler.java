package handlers;

//My packages

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.AlreadyTakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;


public class DeleteHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        UserService.clearApplication();
    }


}

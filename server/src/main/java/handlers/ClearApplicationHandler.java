package handlers;

//My packages

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;


public class ClearApplicationHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        UserService.clearApplication();
        context.result("{ }");
    }


}

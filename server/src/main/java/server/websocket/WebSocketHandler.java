package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();


    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
            String name = UserService.authDAO.getAuth(command.getAuthToken()).username();
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, name, wsMessageContext.session);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connect(UserGameCommand command, String name, Session session){
        //Load the game for the player
        connections.add(session);
        var message = String.format("%s joined the game", name);
        //Notify all players


        //Notify all observers
        connections.broadcast();
    }

    public void makeMove(){
        //Change the game

        //

    }

    public void leave(){
    }

    public void resign(){
    }
}

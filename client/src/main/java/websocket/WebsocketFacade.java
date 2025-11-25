package websocket;

import chess.ChessMove;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler){
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case ERROR:
                            notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                        case LOAD_GAME:
                            notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                            break;
                        case NOTIFICATION:
                            notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                            break;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String authToken, int gameID) {
        //Sends a CONNECT message via websocket
        var connectMessage = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        send(connectMessage);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        //Sends a MAKE_MOVE message via websocket
        var moveMessage = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        send(moveMessage);
    }

    public void leave(String authToken, int gameID) {
        //Sends a LEAVE message via websocket
        var leaveMessage = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(leaveMessage);
    }

    public void resign(String authToken, int gameID) {
        //Sends a LEAVE message via websocket
        var resignMessage = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(resignMessage);
    }

    public void send(Object obj) {
        //Sends a message with the given object
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(obj));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}

package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

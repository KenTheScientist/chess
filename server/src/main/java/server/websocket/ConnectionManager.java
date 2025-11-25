package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new CopyOnWriteArrayList<>())
                .add(session);

    }

    public void remove(int gameID, Session session) {
        List<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);

            // Optional: Clean up empty games to save memory
            if (sessions.isEmpty()) {
                connections.remove(gameID, sessions);
            }
        }
    }

    public void broadcastToGame(int gameID, ServerMessage notification, Session excludeSession) throws IOException {
        Gson gson = new Gson();
        var msg = gson.toJson(notification);
        var currentGameConnections = connections.get(gameID);
        for (Session c : currentGameConnections) {
            if (c.isOpen()) {
                //We're sending it to everyone except excludeSession
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastToSession(ServerMessage notification, Session session) throws IOException {
        var msg = new Gson().toJson(notification);
        if(session.isOpen()){
            session.getRemote().sendString(msg);
        }
    }
}
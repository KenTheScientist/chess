package client;

import chess.ResponseException;
import chess.server.ServerFacade;
import server.Server;
import org.junit.jupiter.api.*;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void beforeEach() throws ResponseException {
        serverFacade.clearData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Register Positive")
    public void registerPositive() throws ResponseException {
        var result = serverFacade.register("username","password","email");
        Assertions.assertEquals("username", result.username());

    }

    @Test
    @Order(2)
    @DisplayName("Register Negative")
    public void registerNegative() throws ResponseException {
        serverFacade.register("username","password","email");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register("username","password","email"));
    }

    @Test
    @Order(3)
    @DisplayName("Login Positive")
    public void loginPositive() throws ResponseException {
        serverFacade.register("username","password","email");
        var result = serverFacade.login("username","password");
        Assertions.assertEquals("username", result.username());
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login("username2","password"));
    }

    @Test
    @Order(5)
    @DisplayName("Logout Positive")
    public void logoutPositive() throws ResponseException {
        var result = serverFacade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(result.authToken()));
    }

    @Test
    @Order(6)
    @DisplayName("Logout Negative")
    public void logoutNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("authToken"));
    }

    @Test
    @Order(7)
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws ResponseException {
        var result = serverFacade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> serverFacade.listGames(result.authToken()));
    }

    @Test
    @Order(8)
    @DisplayName("List Games Negative")
    public void listGamesNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames("authToken"));
    }

    @Test
    @Order(9)
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws ResponseException {
        var result = serverFacade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame("gameName", result.authToken()));
    }

    @Test
    @Order(10)
    @DisplayName("Create Game Negative")
    public void createGameNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame("gameName", "authToken"));
    }

    @Test
    @Order(11)
    @DisplayName("Join Game Positive")
    public void joinGamePositive() throws ResponseException {
        var result = serverFacade.register("username","password","email");
        var game = serverFacade.createGame("gameName",result.authToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(result.authToken(),"WHITE", game.gameID()));
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Negative")
    public void joinGameNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame("authToken","WHITE",1234));
    }

    @Test
    @Order(13)
    @DisplayName("Clear Data Positive")
    public void clearDataPositive() {
        Assertions.assertDoesNotThrow(serverFacade::clearData);
    }

    


}

package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import chess.ResponseException;
import dataaccess.UnauthorizedException;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {


    @BeforeEach
    void setup() throws DataAccessException, ResponseException {
        UserService.userDAO.clear();
        UserService.authDAO.clear();
        GameService.gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("UserService - Clear Positive")
    public void userServiceClearPositive() {
        try {
            UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            UserService.clearApplication();
            Assertions.assertNull(UserService.userDAO.getUser("username"));
        }
        catch(AlreadyTakenException e) {
            Assertions.fail("Username taken");
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(2)
    @DisplayName("UserService - Register Positive")
    public void userServiceRegisterPositive() {
        try {
            UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            Assertions.assertEquals(new UserData("username", "password",  "email@email.com"),
                    UserService.userDAO.getUser("username"));
        }
        catch(AlreadyTakenException e) {
            Assertions.fail("Username taken");
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(3)
    @DisplayName("UserService - Register Negative")
    public void userServiceRegisterNegative() {
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            UserService.register(new RegisterRequest("username", "password2", "email2@email.com"));
        });
    }

    @Test
    @Order(4)
    @DisplayName("UserService - Login Positive")
    public void userServiceLoginPositive() {
        try {
            UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            LoginResult result = UserService.login(new LoginRequest("username","password"));
            Assertions.assertEquals(new AuthData(result.authToken(),result.username()), UserService.authDAO.getAuth(result.authToken()));
        }
        catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("UserService - Login Negative")
    public void userServiceLoginNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.login(new LoginRequest("username", "password")));
    }

    @Test
    @Order(6)
    @DisplayName("UserService - Logout Positive")
    public void userServiceLogoutPositive() {
        try {
            RegisterResult result = UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            UserService.logout(new LogoutRequest(result.authToken()));

            Assertions.assertNull(UserService.authDAO.getAuth(result.authToken()));
        }
        catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("UserService - Logout Negative")
    public void userServiceLogoutNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.logout(new LogoutRequest("1234")));
    }

    @Test
    @Order(8)
    @DisplayName("GameService - CreateGame Positive")
    public void gameServiceCreateGamePositive() {
        try {
            RegisterResult result = UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            CreateGameResult createResult = GameService.createGame(new CreateGameRequest("gameName"), result.authToken());

            Assertions.assertNotNull(GameService.gameDAO.getGame(createResult.gameID()));
        }
        catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("GameService - CreateGame Negative")
    public void gameServiceCreateGameNegative() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> GameService.createGame(new CreateGameRequest("gameName"), "9876"));
    }

    @Test
    @Order(10)
    @DisplayName("GameService - ListGames Positive")
    public void gameServiceListGamesPositive() {
        try {
            RegisterResult result = UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            GameService.createGame(new CreateGameRequest("gameName"), result.authToken());
            ListGamesResult listGamesResult = GameService.listGames(new ListGamesRequest(result.authToken()));
            Assertions.assertEquals("gameName", listGamesResult.games().getFirst().gameName());
        }
        catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("GameService - ListGames Negative")
    public void gameServiceListGamesNegative() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                GameService.listGames(new ListGamesRequest("9876")));
    }

    @Test
    @Order(12)
    @DisplayName("GameService - JoinGame Positive")
    public void gameServiceJoinGamePositive() {
        try {
            RegisterResult result = UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            CreateGameResult createResult = GameService.createGame(new CreateGameRequest("gameName"), result.authToken());
            GameService.joinGame(new JoinGameRequest("WHITE", createResult.gameID()), result.authToken());
            Assertions.assertEquals("username", GameService.gameDAO.getGame(createResult.gameID()).whiteUsername());
        }
        catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("GameService - JoinGame Negative")
    public void gameServiceJoinGameNegative() {
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            RegisterResult result = UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            CreateGameResult createResult = GameService.createGame(new CreateGameRequest("gameName"), result.authToken());
            GameService.joinGame(new JoinGameRequest("WHITE", createResult.gameID()), result.authToken());
            GameService.joinGame(new JoinGameRequest("WHITE", createResult.gameID()), result.authToken());
        });
    }









}

package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    @BeforeEach
    void setup() throws DataAccessException, ResponseException {
        UserService.userDAO.clear();
        UserService.authDAO.clear();
        GameService.gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("AuthDao - Clear Positive")
    public void authDaoClearPositive() {
        try {

            UserService.authDAO.createAuth(new AuthData("1234", "username"));
            UserService.authDAO.clear();
            Assertions.assertNull(UserService.authDAO.getAuth("1234"));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(2)
    @DisplayName("UserDao - Clear Positive")
    public void userDaoClearPositive() {
        try {
            UserService.userDAO.createUser(new UserData("username", "pwd", "email.com"));
            UserService.userDAO.clear();
            Assertions.assertNull(UserService.userDAO.getUser("username"));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(3)
    @DisplayName("GameDao - Clear Positive")
    public void gameDaoClearPositive() {
        try {
            GameService.gameDAO.createGame(new GameData(1234, null, null, "name", new ChessGame()));
            GameService.gameDAO.clear();
            Assertions.assertNull(GameService.gameDAO.getGame(1234));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(3)
    @DisplayName("AuthDao - getAuth Positive")
    public void authDaoGetAuthPositive() {
        try {
            UserService.authDAO.createAuth(new AuthData("1234", "username"));
            AuthData result = UserService.authDAO.getAuth("1234");
            Assertions.assertEquals(new AuthData("1234", "username"), result);
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(4)
    @DisplayName("AuthDao - getAuth Negative")
    public void authDaoGetAuthNegative() {
        try{
            Assertions.assertNull(UserService.authDAO.getAuth("1234"));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(5)
    @DisplayName("AuthDao - createAuth Positive")
    public void authDaoCreateAuthPositive() {
        try {
            UserService.authDAO.createAuth(new AuthData("12345", "username"));
            AuthData result = UserService.authDAO.getAuth("12345");
            Assertions.assertEquals(new AuthData("12345", "username"), result);
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(6)
    @DisplayName("AuthDao - createAuth Negative")
    public void authDaoCreateAuthNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                UserService.authDAO.createAuth(new AuthData("1234", null)));
    }

    @Test
    @Order(7)
    @DisplayName("AuthDao - deleteAuth Positive")
    public void authDaoDeleteAuthPositive() {
        try {
            UserService.authDAO.createAuth(new AuthData("1234", "username"));
            UserService.authDAO.deleteAuth("1234");
            Assertions.assertNull(UserService.authDAO.getAuth("1234"));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(8)
    @DisplayName("AuthDao - deleteAuth Negative")
    public void authDaoDeleteAuthNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.authDAO.deleteAuth(null));
    }

    @Test
    @Order(9)
    @DisplayName("UserDao - createUser Positive")
    public void userDaoCreateUserPositive() {
        try {
            UserService.userDAO.createUser(new UserData("username", "password", "email@email.com"));
            UserData result = UserService.userDAO.getUser("username");
            Assertions.assertEquals("username", result.username());
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(10)
    @DisplayName("UserDao - createUser Negative")
    public void userDaoCreateUserNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                UserService.userDAO.createUser(new UserData("username", "password", null)));
    }

    @Test
    @Order(11)
    @DisplayName("UserDao - getUser Positive")
    public void userDaoGetUserPositive() {

    }

    @Test
    @Order(12)
    @DisplayName("UserDao - getUser Negative")
    public void userDaoGetUserNegative() throws ResponseException, DataAccessException {
        Assertions.assertNull(UserService.userDAO.getUser("username"));
    }

    @Test
    @Order(13)
    @DisplayName("GameDao - createGame Positive")
    public void gameDaoCreateGamePositive() {
        try {
            GameService.gameDAO.createGame(new GameData(1234, null, null, "name", new ChessGame()));
            GameData result = GameService.gameDAO.getGame(1234);
            Assertions.assertEquals(new GameData(1234, null, null, "name", new ChessGame()), result);
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(14)
    @DisplayName("GameDao - createGame Negative")
    public void gameDaoCreateGameNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                GameService.gameDAO.createGame(new GameData(1234, null, null, null, null)));
    }

    @Test
    @Order(15)
    @DisplayName("GameDao - getGame Positive")
    public void gameDaoGetGamePositive() {
        try {
            GameService.gameDAO.createGame(new GameData(12345, null, null, "name", new ChessGame()));
            GameData result = GameService.gameDAO.getGame(12345);
            Assertions.assertEquals(new GameData(12345, null, null, "name", new ChessGame()), result);
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(16)
    @DisplayName("GameDao - getGame Negative")
    public void gameDaoGetGameNegative() throws ResponseException, DataAccessException {
        Assertions.assertNull(GameService.gameDAO.getGame(1234));
    }

    @Test
    @Order(17)
    @DisplayName("GameDao - listGames Positive")
    public void gameDaoListGamesPositive() {
        try {
            GameService.gameDAO.createGame(new GameData(1234, null, null, "name", new ChessGame()));
            GameService.gameDAO.createGame(new GameData(1235, null, null, "name2", new ChessGame()));
            ArrayList<GameData> result = GameService.gameDAO.listGames();
            Assertions.assertEquals(2, result.size());
            Assertions.assertEquals(new GameData(1234, null, null, "name", new ChessGame()), result.get(0));
            Assertions.assertEquals(new GameData(1235, null, null, "name2", new ChessGame()), result.get(1));
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(18)
    @DisplayName("GameDao - listGames Negative")
    public void gameDaoListGamesNegative() throws ResponseException, DataAccessException {
        Assertions.assertFalse(!GameService.gameDAO.listGames().isEmpty());

    }

    @Test
    @Order(19)
    @DisplayName("GameDao - updateGame Positive")
    public void gameDaoUpdateGamePositive() {
        try {
            GameService.gameDAO.createGame(new GameData(1234, null, null, "name", new ChessGame()));
            GameService.gameDAO.updateGame(1234, new GameData(1234, null, null, "name2", new ChessGame()));
            GameData result = GameService.gameDAO.getGame(1234);
            Assertions.assertEquals(new GameData(1234, null, null, "name2", new ChessGame()), result);
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess error");
        } catch (ResponseException e) {
            Assertions.fail("Server error");
        }
    }

    @Test
    @Order(20)
    @DisplayName("GameDao - updateGame Negative")
    public void gameDaoUpdateGameNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                GameService.gameDAO.updateGame(1234, null));
    }

}

package service;

import chess.ResponseException;
import dataaccess.*;
import datamodel.AuthData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    public static UserDAO userDAO;

    static {
        try {
            userDAO = new SqlUserDAO();
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthDAO authDAO;

    static {
        try {
            authDAO = new SqlAuthDAO();
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearApplication() throws DataAccessException, ResponseException {
        userDAO.clear();
        authDAO.clear();
        GameService.gameDAO.clear();
    }

    public static RegisterResult register(RegisterRequest request) throws AlreadyTakenException, DataAccessException, ResponseException {

        UserData searchedUser = userDAO.getUser(request.username());

        if(searchedUser != null)
        {
            //The user already exists!!!!
            throw new AlreadyTakenException();
        }
        else{
            //Let's place a new UserData in there
            userDAO.createUser(new UserData(request.username(), request.password(), request.email()));

            //Let's generate the new auth token
            String generatedAuthtoken = UUID.randomUUID().toString();

            //Let's place the new AuthData in there
            authDAO.createAuth(new AuthData(generatedAuthtoken, request.username()));

            return new RegisterResult(request.username(), generatedAuthtoken);
        }
    }

    public static LoginResult login(LoginRequest request) throws UnauthorizedException, DataAccessException, ResponseException {

        UserData searchedUser = userDAO.getUser(request.username());

        if(searchedUser == null) {
            throw new DataAccessException();
        }

        if(!BCrypt.checkpw(request.password(),searchedUser.password()))
        {
            //This user doesn't exist or the password is wrong
            System.out.println(searchedUser.password());
            throw new UnauthorizedException();
        }
        else{
            //Let's generate the new auth token
            String generatedAuthtoken = UUID.randomUUID().toString();

            //Let's place the new AuthData in there
            authDAO.createAuth(new AuthData(generatedAuthtoken, request.username()));

            return new LoginResult(request.username(), generatedAuthtoken);
        }
    }

    public static void logout(LogoutRequest request) throws DataAccessException, ResponseException {
        AuthData searchedAuthData = authDAO.getAuth(request.authToken());
        if(searchedAuthData != null) {
            //Found a match
            authDAO.deleteAuth(request.authToken());
        }
        else{
            //Can't delete something that's not there
            throw new DataAccessException();
        }
    }
}

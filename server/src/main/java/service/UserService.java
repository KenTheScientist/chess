package service;

import dataaccess.*;
import datamodel.AuthData;
import datamodel.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    static MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    static MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();

    public static void clearApplication(){
        memoryUserDAO.clear();
        memoryAuthDAO.clear();
        GameService.memoryGameDAO.clear();
    }

    public static RegisterResult register(RegisterRequest request) throws AlreadyTakenException {

        UserData searchedUser = memoryUserDAO.getUser(request.username());

        if(searchedUser != null)
        {
            //The user already exists!!!!
            throw new AlreadyTakenException();
        }
        else{
            //Let's place a new UserData in there
            memoryUserDAO.createUser(new UserData(request.username(), request.password(), request.email()));

            //Let's generate the new auth token
            String generatedAuthtoken = UUID.randomUUID().toString();

            //Let's place the new AuthData in there
            memoryAuthDAO.createAuth(new AuthData(generatedAuthtoken, request.username()));

            return new RegisterResult(request.username(), generatedAuthtoken);
        }
    }

    public static LoginResult login(LoginRequest request) throws UnauthorizedException, DataAccessException {

        UserData searchedUser = memoryUserDAO.getUser(request.username());

        if(searchedUser == null) {
            throw new DataAccessException();
        }
        if(!searchedUser.password().equals(request.password()))
        {
            //This user doesn't exist or the password is wrong

            throw new UnauthorizedException();
        }
        else{
            //Let's generate the new auth token
            String generatedAuthtoken = UUID.randomUUID().toString();

            //Let's place the new AuthData in there
            memoryAuthDAO.createAuth(new AuthData(generatedAuthtoken, request.username()));

            return new LoginResult(request.username(), generatedAuthtoken);
        }
    }

    public static void logout(LogoutRequest request) throws DataAccessException {
        AuthData searchedAuthData = memoryAuthDAO.getAuth(request.authToken());
        if(searchedAuthData != null) {
            //Found a match
            memoryAuthDAO.deleteAuth(request.authToken());
        }
        else{
            //Can't delete something that's not there
            throw new DataAccessException();
        }
    }
}

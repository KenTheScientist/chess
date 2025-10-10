package service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datamodel.AuthData;
import datamodel.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    static MemoryUserDAO memoryUserDAO = new MemoryUserDAO();

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
            memoryUserDAO.createAuth(new AuthData(generatedAuthtoken, request.username()));

            return new RegisterResult(request.username(), generatedAuthtoken);
        }





    }
}

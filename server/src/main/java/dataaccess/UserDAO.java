package dataaccess;

import datamodel.UserData;

public interface UserDAO {
    void clear() throws DataAccessException, ResponseException;
    void createUser(UserData userData) throws DataAccessException, ResponseException;
    UserData getUser(String username) throws DataAccessException, ResponseException;
}

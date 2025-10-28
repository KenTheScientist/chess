package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface UserDAO {
    public void clear() throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
}

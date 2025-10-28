package dataaccess;

import datamodel.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public void createAuth(AuthData authData) throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;

}

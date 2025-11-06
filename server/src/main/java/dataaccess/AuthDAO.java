package dataaccess;

import chess.ResponseException;
import datamodel.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException, ResponseException;
    public void createAuth(AuthData authData) throws DataAccessException, ResponseException;
    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException;
    public void deleteAuth(String authToken) throws DataAccessException, ResponseException;

}

package dataaccess;

import datamodel.AuthData;

public interface AuthDAO {
    public void clear();
    public void createAuth(AuthData authData);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);

}

package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface UserDAO {
    public UserData getUser(String username);
    public void createUser(UserData userData);
    public void createAuth(AuthData authData);
}

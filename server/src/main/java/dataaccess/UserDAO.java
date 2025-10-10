package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface UserDAO {
    public void clear();
    public void createUser(UserData userData);
    public UserData getUser(String username);
}

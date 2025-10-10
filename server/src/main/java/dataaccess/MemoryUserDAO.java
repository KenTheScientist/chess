package dataaccess;

import datamodel.*;

import java.util.ArrayList;


public class MemoryUserDAO implements UserDAO{
    private static ArrayList<UserData> userDataDatabase = new ArrayList<>();
    private static ArrayList<GameData> gameDataDatabase = new ArrayList<>();
    private static ArrayList<AuthData> authDataDatabase = new ArrayList<>();


    //Gets the user data given a username
    public UserData getUser(String username) {
        for (UserData userData : userDataDatabase) {
            if (userData.username().equals(username)) {
                //Found a match!
                return userData;
            }
        }
        return null;
    }

    //Places the given UserData in the database
    public void createUser(UserData userData){
        userDataDatabase.add(userData);
    }

    //Places the given AuthData in the database
    public void createAuth(AuthData authData){
        authDataDatabase.add(authData);
    }

}

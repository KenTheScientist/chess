package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;


public class MemoryAuthDAO implements AuthDAO{
    private static ArrayList<AuthData> authDataDatabase = new ArrayList<>();

    public void clear() {
        authDataDatabase.clear();
    }

    //Gets the AuthData given the authToken
    public AuthData getAuth(String authToken) {
        for (AuthData authData : authDataDatabase) {
            if (authData.authToken().equals(authToken)) {
                //Found a match!
                return authData;
            }
        }
        return null;
    }

    //Places the given UserData in the database
    public void createAuth(AuthData authData){
        authDataDatabase.add(authData);
    }

    //Deletes the AuthData given the authToken
    public void deleteAuth(String authToken) {
        for (AuthData authData : authDataDatabase) {
            if (authData.authToken().equals(authToken)) {
                //Found a match!
                authDataDatabase.remove(authData);
                return;
            }
        }
        return;
    }

}

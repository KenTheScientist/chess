package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;


public class MemoryGameDAO implements GameDAO{
    private static ArrayList<GameData> gameDataDatabase = new ArrayList<>();

    public void clear(){
        gameDataDatabase.clear();
    }

    public void createGame(GameData gameData) {
        gameDataDatabase.add(gameData);
    }

    public GameData getGame(int gameID){

    }

    public ArrayList<GameData> listGames(){
        return gameDataDatabase;
    }

    public void updateGame(int gameID, String chessGame) {

    }

}

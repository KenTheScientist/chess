package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import javax.xml.crypto.Data;
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
        for (GameData gameData : gameDataDatabase) {
            if (gameData.gameID() == gameID) {
                //Found a match!
                return gameData;
            }
        }
        return null;
    }

    public ArrayList<GameData> listGames(){
        return gameDataDatabase;
    }

    public void updateGame(int gameID, GameData replacingGameData) throws DataAccessException{
        for (GameData gameData : gameDataDatabase) {
            if (gameData.gameID() == gameID) {
                //Found a match!
                gameData = replacingGameData;
                return;
            }
        }

        throw new DataAccessException();
    }

}

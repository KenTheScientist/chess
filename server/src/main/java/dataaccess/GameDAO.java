package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public void createGame(GameData gameData) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public ArrayList<GameData> listGames() throws DataAccessException;
    public void updateGame(int gameID, GameData replacingGameData) throws DataAccessException;

}

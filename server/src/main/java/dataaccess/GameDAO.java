package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;

public interface GameDAO {
    public void clear();
    public void createGame(GameData gameData);
    public GameData getGame(int gameID);
    public ArrayList<GameData> listGames();
    public void updateGame(int gameID, String chessGame);

}

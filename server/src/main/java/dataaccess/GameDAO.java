package dataaccess;

import chess.ResponseException;
import datamodel.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public void clear() throws DataAccessException, ResponseException;
    public void createGame(GameData gameData) throws DataAccessException, ResponseException;
    public GameData getGame(int gameID) throws DataAccessException, ResponseException;
    public ArrayList<GameData> listGames() throws DataAccessException, ResponseException;
    public void updateGame(int gameID, GameData replacingGameData) throws DataAccessException, ResponseException;

}

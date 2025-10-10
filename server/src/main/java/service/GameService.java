package service;

import chess.ChessGame;
import dataaccess.*;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.ListGameData;
import datamodel.UserData;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameService {
    static MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    public static ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.memoryAuthDAO.getAuth(request.authToken());
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new DataAccessException();
        }
        else
        {
            //Get the game list
            ArrayList<GameData> gameList = memoryGameDAO.listGames();

            //Convert it to this modified list
            ArrayList<ListGameData> resultList = new ArrayList<>();

            for(GameData game : gameList){
                resultList.add(new ListGameData(game.gameID(),game.whiteUsername(),game.blackUsername(),game.gameName()));
            }

            ListGamesResult result = new ListGamesResult(resultList);

            return result;
        }
    }

    public static CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.memoryAuthDAO.getAuth(authToken);
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new DataAccessException();
        }
        else {
            //Authenticated!
            //Create the game
            int newID = new Random().nextInt(9999);
            GameData gameData = new GameData(newID,"","", request.gameName(), new ChessGame());
            memoryGameDAO.createGame(gameData);

            return new CreateGameResult(newID);
        }
    }


}

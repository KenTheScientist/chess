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

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameService {
    static MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    public static ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.memoryAuthDAO.getAuth(request.authToken());
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
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

    public static CreateGameResult createGame(CreateGameRequest request, String authToken) throws UnauthorizedException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.memoryAuthDAO.getAuth(authToken);
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
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

    public static void joinGame(JoinGameRequest request, String authToken) throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        AuthData searchingAuthData = UserService.memoryAuthDAO.getAuth(authToken);
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
        }
        else {
            //Authenticated!
            //See if the game exists
            GameData searchingGameData = memoryGameDAO.getGame(request.gameID());
            if(searchingGameData == null) {
                //Game not found
                throw new DataAccessException();
            }
            else
            {
                //Game exists. Now, is there an open color
                if(request.playerColor().equals("WHITE")){
                    //Check if white exists
                    if(!searchingGameData.whiteUsername().isEmpty()){
                        //Taken!
                        throw new AlreadyTakenException();
                    }
                    else {
                        //Let's add ourselves
                        GameData placingGameData = new GameData(searchingGameData.gameID(), searchingAuthData.username(), searchingGameData.blackUsername(), searchingGameData.gameName(), searchingGameData.game());
                        memoryGameDAO.updateGame(placingGameData.gameID(), placingGameData);
                    }
                }
                else if(request.playerColor().equals("BLACK")){
                    //Check if black exists
                    if(!searchingGameData.blackUsername().isEmpty()){
                        //Taken!
                        throw new AlreadyTakenException();
                    }
                    else {
                        //Let's add ourselves
                        GameData placingGameData = new GameData(searchingGameData.gameID(), searchingGameData.whiteUsername(), searchingAuthData.username(), searchingGameData.gameName(), searchingGameData.game());
                        memoryGameDAO.updateGame(placingGameData.gameID(), placingGameData);
                    }
                }
                else {
                    //This is a bad request
                    throw new DataAccessException();
                }

            }
        }
    }


}

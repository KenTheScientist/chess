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
    static GameDAO gameDAO;

    static {
        try {
            gameDAO = new SqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedException, DataAccessException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.authDAO.getAuth(request.authToken());
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
        }
        else
        {
            //Get the game list
            ArrayList<GameData> gameList = gameDAO.listGames();

            //Convert it to this modified list
            ArrayList<ListGameData> resultList = new ArrayList<>();

            for(GameData game : gameList){
                resultList.add(new ListGameData(game.gameID(),game.whiteUsername(),game.blackUsername(),game.gameName()));
            }

            ListGamesResult result = new ListGamesResult(resultList);

            return result;
        }
    }

    public static CreateGameResult createGame(CreateGameRequest request, String authToken) throws UnauthorizedException, DataAccessException {
        //First we have to verify the user
        AuthData searchingAuthData = UserService.authDAO.getAuth(authToken);
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
        }
        else {
            //Authenticated!
            //Create the game
            int newID = new Random().nextInt(9999);
            GameData gameData = new GameData(newID,null,null, request.gameName(), new ChessGame());
            gameDAO.createGame(gameData);

            return new CreateGameResult(newID);
        }
    }

    public static void joinGame(JoinGameRequest request, String authToken) throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        AuthData searchingAuthData = UserService.authDAO.getAuth(authToken);
        if(searchingAuthData == null) {
            //Unauthorized!
            throw new UnauthorizedException();
        }
        else {
            //Authenticated!
            //See if the game exists
            GameData searchingGameData = gameDAO.getGame(request.gameID());
            if(searchingGameData == null) {
                //Game not found
                throw new DataAccessException();
            }
            else
            {
                //Game exists. Now, is there an open color
                if(request.playerColor() != null && request.playerColor().equals("WHITE")){
                    //Check if white exists
                    if(searchingGameData.whiteUsername() != null){
                        //Taken!
                        throw new AlreadyTakenException();
                    }
                    else {
                        //Let's add ourselves
                        GameData placingGameData = new GameData(searchingGameData.gameID(),
                                searchingAuthData.username(), searchingGameData.blackUsername(),
                                searchingGameData.gameName(), searchingGameData.game());
                        gameDAO.updateGame(placingGameData.gameID(), placingGameData);
                    }
                }
                else if(request.playerColor() != null && request.playerColor().equals("BLACK")){
                    //Check if black exists
                    if(searchingGameData.blackUsername() != null){
                        //Taken!
                        throw new AlreadyTakenException();
                    }
                    else {
                        //Let's add ourselves
                        GameData placingGameData = new GameData(searchingGameData.gameID(),
                                searchingGameData.whiteUsername(), searchingAuthData.username(),
                                searchingGameData.gameName(), searchingGameData.game());
                        gameDAO.updateGame(placingGameData.gameID(), placingGameData);
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

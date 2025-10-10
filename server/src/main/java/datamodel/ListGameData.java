package datamodel;

import chess.ChessGame;
//Essentially a GameData but without the data
public record ListGameData(int gameID, String whiteUsername, String blackUsername, String gameName){}


package chess;

import java.util.ArrayList;

public class MoveGenerator {

    public static ArrayList<ChessPosition> getBishopMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){

        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();

        int[] colChanges = {1, 1, -1, -1};
        int[] rowChanges = {1, -1, 1, -1};
        for(int i = 0; i < 4; i++){
            int checkingCol = myCol;
            int checkingRow = myRow;
            while(true){
                checkingCol += colChanges[i];
                checkingRow += rowChanges[i];
                if(checkingCol < 1 || checkingCol > 8 || checkingRow < 1 || checkingRow > 8){
                    break; //Out of bounds
                }
                if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                    if(board.getPiece(new ChessPosition(checkingRow, checkingCol)).getTeamColor() == pieceColor){
                        break; //We ran into a friendly piece
                    }
                    else{
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        break;
                    }
                }
                else{
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                }
            }
        }
        return possiblePositions;

    }

    public static ArrayList<ChessPosition> getRookMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        int[] colChanges = {1, -1, 0, 0};
        int[] rowChanges = {0, 0, 1, -1};
        for(int i = 0; i < 4; i++){
            int currentCol = myCol;
            int currentRow = myRow;
            while(true){
                currentCol += colChanges[i];
                currentRow += rowChanges[i];
                if(currentCol < 1 || currentCol > 8 || currentRow < 1 || currentRow > 8){
                    break; //Out of bounds
                }
                if(board.hasPiece(new ChessPosition(currentRow, currentCol))){
                    if(board.getPiece(new ChessPosition(currentRow, currentCol)).getTeamColor() == pieceColor){
                        break; //We ran into a friendly piece
                    }
                    else{
                        possiblePositions.add(new ChessPosition(currentRow, currentCol));
                        break; //We ran into an enemy piece
                    }
                }
                else{
                    possiblePositions.add(new ChessPosition(currentRow, currentCol));
                }
            }
        }
        return possiblePositions;
    }

    public static ArrayList<ChessPosition> getKnightMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        int[] colChanges = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] rowChanges = {1, -1, 1, -1, 2, -2, 2, -2};
        for(int i = 0; i < 8; i++){
            int currentCol = myCol;
            int currentRow = myRow;
            currentCol += colChanges[i];
            currentRow += rowChanges[i];
            if(currentCol < 1 || currentCol > 8 || currentRow < 1 || currentRow > 8){
                continue; //Out of bounds
            }
            if(board.hasPiece(new ChessPosition(currentRow, currentCol))){
                if(board.getPiece(new ChessPosition(currentRow, currentCol)).getTeamColor() != pieceColor){
                    //Enemy piece
                    possiblePositions.add(new ChessPosition(currentRow, currentCol));
                }
            }
            else{
                possiblePositions.add(new ChessPosition(currentRow, currentCol));
            }
        }
        return possiblePositions;
    }

    public static ArrayList<ChessPosition> getKingMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        int[] colChanges = {1, 1, -1, -1, 1, 0, 0, -1};
        int[] rowChanges = {1, -1, 1, -1, 0, 1, -1, 0};
        for(int i = 0; i < 8; i++){
            int checkingCol = myCol;
            int checkingRow = myRow;
            checkingCol += colChanges[i];
            checkingRow += rowChanges[i];
            if(checkingCol < 1 || checkingCol > 8 || checkingRow < 1 || checkingRow > 8){
                continue; //Out of bounds
            }
            if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                if(board.getPiece(new ChessPosition(checkingRow, checkingCol)).getTeamColor() != pieceColor){
                    //Enemy piece!
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                }
            }
            else{
                possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
            }
        }
        return possiblePositions;
    }

    public static ArrayList<ChessPosition> getQueenMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        possiblePositions.addAll(getBishopMovePositions(board, myCol, myRow, pieceColor));
        possiblePositions.addAll(getRookMovePositions(board, myCol, myRow, pieceColor));
        return possiblePositions;
    }

    public static ArrayList<ChessPosition> getPawnMovePositions(ChessBoard board, int myCol, int myRow, ChessGame.TeamColor pieceColor){
        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        if(pieceColor == ChessGame.TeamColor.WHITE){
            //Heading UP
            //Attack LEFTUP
            if(myCol != 1 && board.hasPiece(new ChessPosition(myRow+1,myCol-1))){
                if(board.getPiece(new ChessPosition(myRow+1,myCol-1)).getTeamColor() != pieceColor){
                    possiblePositions.add(new ChessPosition(myRow+1,myCol-1));
                }
            }
            //Attack RIGHTUP
            if(myCol != 8 && board.hasPiece(new ChessPosition(myRow+1,myCol+1))){
                if(board.getPiece(new ChessPosition(myRow+1,myCol+1)).getTeamColor() != pieceColor){
                    possiblePositions.add(new ChessPosition(myRow+1,myCol+1));
                }
            }
            //Move up if no one's there
            if(myRow != 8 && !board.hasPiece(new ChessPosition(myRow+1,myCol))){
                possiblePositions.add(new ChessPosition(myRow+1,myCol));
            }
            //Move 2 if on start
            if(myRow == 2){
                if(!board.hasPiece(new ChessPosition(myRow+1,myCol)) && !board.hasPiece(new ChessPosition(myRow+2,myCol))){
                    possiblePositions.add(new ChessPosition(myRow+2,myCol));
                }
            }
        }
        else if(pieceColor == ChessGame.TeamColor.BLACK){
            //Heading DOWN
            //Attack LEFTDOWN
            if(myCol != 1 && board.hasPiece(new ChessPosition(myRow-1,myCol-1))){
                if(board.getPiece(new ChessPosition(myRow-1,myCol-1)).getTeamColor() != pieceColor){
                    possiblePositions.add(new ChessPosition(myRow-1,myCol-1));
                }
            }
            //Attack RIGHTDOWN
            if(myCol != 8 && board.hasPiece(new ChessPosition(myRow-1,myCol+1))){
                if(board.getPiece(new ChessPosition(myRow-1,myCol+1)).getTeamColor() != pieceColor){
                    possiblePositions.add(new ChessPosition(myRow-1,myCol+1));
                }
            }
            //Move down if no one's there
            if(myRow != 1 && !board.hasPiece(new ChessPosition(myRow-1,myCol))){
                possiblePositions.add(new ChessPosition(myRow-1,myCol));
            }
            //Move 2 if on start
            if(myRow == 7){
                if(!board.hasPiece(new ChessPosition(myRow-1,myCol)) && !board.hasPiece(new ChessPosition(myRow-2,myCol))){
                    possiblePositions.add(new ChessPosition(myRow-2,myCol));
                }
            }
        }
        return possiblePositions;
    }
}



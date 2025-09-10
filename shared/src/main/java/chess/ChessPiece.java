package chess;

import java.util.*;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {


    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }


    /**
     * Calculates whether a move is valid or not, given a move-to position and color on a board
     */

    public boolean moveValid(ChessBoard board, ChessPosition pos, ChessGame.TeamColor col)
    {
        if(pos.getColumn() < 1 || pos.getColumn() > 8 || pos.getRow() < 1 || pos.getRow() > 8) return false; // Out of bounds
        if(board.hasPiece(pos)){
            if(board.getPiece(pos).getTeamColor() == col) return false; //We're on the same team!!!
        }
        return true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessPosition> possiblePositions = new ArrayList<>(); //List of possible move-to positions
        Collection<ChessMove> possibleMoves = new ArrayList<>(); //Output list of ChessMoves

        int myCol = myPosition.col;
        int myRow = myPosition.row;

        switch (type){
            case PieceType.BISHOP:
                //Go up and right
                int checkingCol = myCol;
                int checkingRow = myRow;

                while(true)//UP AND RIGHT
                {
                    checkingRow++;
                    checkingCol++;
                    if(moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)){
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            //We ran into an enemy piece
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//DOWN AND LEFT
                {
                    checkingRow--;
                    checkingCol--;
                    if(moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)){
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            //We ran into an enemy piece
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//UP AND LEFT
                {
                    checkingRow++;
                    checkingCol--;
                    if(moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)){
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            //We ran into an enemy piece
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//DOWN AND RIGHT
                {
                    checkingRow--;
                    checkingCol++;
                    if(moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)){
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            //We ran into an enemy piece
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }


                break;
        }


        //Now fill the possibleMoves collection using the possible positions
        for(ChessPosition pos : possiblePositions){
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;

    }
}

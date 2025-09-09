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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessPosition> possiblePositions = new ArrayList<ChessPosition>(); //List of possible move-to positions
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>(); //Output list of ChessMoves

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
                    if(checkingCol > 8 || checkingRow > 8){
                        break;
                    }
                    else{
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            break;
                        }
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//UP AND LEFT
                {
                    checkingRow++;
                    checkingCol--;
                    if(checkingCol < 1 || checkingRow > 8){
                        break;
                    }
                    else{
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            break;
                        }
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//DOWN AND RIGHT
                {
                    checkingRow--;
                    checkingCol++;
                    if(checkingCol > 8 || checkingRow < 1){
                        break;
                    }
                    else{
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            break;
                        }
                    }
                }

                checkingCol = myCol;
                checkingRow = myRow;
                while(true)//DOWN AND LEFT
                {
                    checkingRow--;
                    checkingCol--;
                    if(checkingCol < 1 || checkingRow < 1){
                        break;
                    }
                    else{
                        possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                        if(board.hasPiece(new ChessPosition(checkingRow, checkingCol))){
                            break;
                        }
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

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

    @Override
    public String toString() {
        String options = "kqbnrp";
        int index = type.ordinal();
        char out = options.charAt(0);
        if(pieceColor == ChessGame.TeamColor.WHITE) out = Character.toUpperCase(out);

        return String.valueOf(out);

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


        if(type == PieceType.BISHOP) {
            //Go up and right
            int checkingCol = myCol;
            int checkingRow = myRow;

            while (true)//UP AND RIGHT
            {
                checkingRow++;
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN AND LEFT
            {
                checkingRow--;
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//UP AND LEFT
            {
                checkingRow++;
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN AND RIGHT
            {
                checkingRow--;
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        else if(type == PieceType.KING) {
            if (moveValid(board, new ChessPosition(myRow+1, myCol), pieceColor)) { //UP
                possiblePositions.add(new ChessPosition(myRow+1, myCol));
            }
            if (moveValid(board, new ChessPosition(myRow+1, myCol+1), pieceColor)) { //UPRIGHT
                possiblePositions.add(new ChessPosition(myRow+1, myCol+1));
            }
            if (moveValid(board, new ChessPosition(myRow, myCol+1), pieceColor)) { //RIGHT
                possiblePositions.add(new ChessPosition(myRow, myCol+1));
            }
            if (moveValid(board, new ChessPosition(myRow-1, myCol+1), pieceColor)) { //DOWNRIGHT
                possiblePositions.add(new ChessPosition(myRow-1, myCol+1));
            }
            if (moveValid(board, new ChessPosition(myRow-1, myCol), pieceColor)) { //DOWN
                possiblePositions.add(new ChessPosition(myRow-1, myCol));
            }
            if (moveValid(board, new ChessPosition(myRow-1, myCol-1), pieceColor)) { //DOWNLEFT
                possiblePositions.add(new ChessPosition(myRow-1, myCol-1));
            }
            if (moveValid(board, new ChessPosition(myRow, myCol-1), pieceColor)) { //LEFT
                possiblePositions.add(new ChessPosition(myRow, myCol-1));
            }
            if (moveValid(board, new ChessPosition(myRow+1, myCol-1), pieceColor)) { //UPLEFT
                possiblePositions.add(new ChessPosition(myRow+1, myCol-1));
            }


        }
        else if(type == PieceType.KNIGHT) {
            if (moveValid(board, new ChessPosition(myRow+2, myCol+1), pieceColor)) { //UPUPRIGHT
                possiblePositions.add(new ChessPosition(myRow+2, myCol+1));
            }
            if (moveValid(board, new ChessPosition(myRow+1, myCol+2), pieceColor)) { //RIGHTRIGHTUP
                possiblePositions.add(new ChessPosition(myRow+1, myCol+2));
            }
            if (moveValid(board, new ChessPosition(myRow-1, myCol+2), pieceColor)) { //RIGHTRIGHTDOWN
                possiblePositions.add(new ChessPosition(myRow-1, myCol+2));
            }
            if (moveValid(board, new ChessPosition(myRow-2, myCol+1), pieceColor)) { //DOWNDOWNRIGHT
                possiblePositions.add(new ChessPosition(myRow-2, myCol+1));
            }
            if (moveValid(board, new ChessPosition(myRow-2, myCol-1), pieceColor)) { //DOWNDOWNLEFT
                possiblePositions.add(new ChessPosition(myRow-2, myCol-1));
            }
            if (moveValid(board, new ChessPosition(myRow-1, myCol-2), pieceColor)) { //LEFTLEFTDOWN
                possiblePositions.add(new ChessPosition(myRow-1, myCol-2));
            }
            if (moveValid(board, new ChessPosition(myRow+1, myCol-2), pieceColor)) { //LEFTLEFTUP
                possiblePositions.add(new ChessPosition(myRow+1, myCol-2));
            }
            if (moveValid(board, new ChessPosition(myRow+2, myCol-1), pieceColor)) { //UPUPLEFT
                possiblePositions.add(new ChessPosition(myRow+2, myCol-1));
            }

        }
        else if(type == PieceType.ROOK) {

            int checkingCol = myCol;
            int checkingRow = myRow;

            while (true)//UP
            {
                checkingRow++;

                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//RIGHT
            {
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN
            {
                checkingRow--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//LEFT
            {
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        else if(type == PieceType.QUEEN) {

            int checkingCol = myCol;
            int checkingRow = myRow;

            while (true)//UP
            {
                checkingRow++;

                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//RIGHT
            {
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN
            {
                checkingRow--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//LEFT
            {
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;

            while (true)//UP AND RIGHT
            {
                checkingRow++;
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN AND LEFT
            {
                checkingRow--;
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//UP AND LEFT
            {
                checkingRow++;
                checkingCol--;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }

            checkingCol = myCol;
            checkingRow = myRow;
            while (true)//DOWN AND RIGHT
            {
                checkingRow--;
                checkingCol++;
                if (moveValid(board, new ChessPosition(checkingRow, checkingCol), pieceColor)) {
                    possiblePositions.add(new ChessPosition(checkingRow, checkingCol));
                    if (board.hasPiece(new ChessPosition(checkingRow, checkingCol))) {
                        //We ran into an enemy piece
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        else if(type == PieceType.PAWN){

            if(pieceColor == ChessGame.TeamColor.BLACK){
                //Heading DOWN
                //Attack LEFTDOWN
                if(moveValid(board, new ChessPosition(myRow-1,myCol-1),pieceColor)){
                    if(board.hasPiece(new ChessPosition(myRow-1,myCol-1))){
                        //Ready for attack
                        possiblePositions.add(new ChessPosition(myRow-1,myCol-1));
                    }
                }
                //Attack RIGHTDOWN
                if(moveValid(board, new ChessPosition(myRow-1,myCol+1),pieceColor)){
                    if(board.hasPiece(new ChessPosition(myRow-1,myCol+1))){
                        //Ready for attack
                        possiblePositions.add(new ChessPosition(myRow-1,myCol+1));
                    }
                }
                //Move down if no one's there
                if(moveValid(board, new ChessPosition(myRow-1,myCol),pieceColor)){
                    if(!board.hasPiece(new ChessPosition(myRow-1,myCol))) {
                        possiblePositions.add(new ChessPosition(myRow - 1, myCol));
                    }
                }
                //Move 2 if on start
                if(myRow == 7){
                    if(moveValid(board, new ChessPosition(myRow-2,myCol),pieceColor)){
                        if(!board.hasPiece(new ChessPosition(myRow-1,myCol)) && !board.hasPiece(new ChessPosition(myRow-2,myCol))) {
                            possiblePositions.add(new ChessPosition(myRow - 2, myCol));
                        }
                    }
                }

            }
            else if(pieceColor == ChessGame.TeamColor.WHITE){
                //Heading UP
                //Attack LEFTUP
                if(moveValid(board, new ChessPosition(myRow+1,myCol-1),pieceColor)){
                    if(board.hasPiece(new ChessPosition(myRow+1,myCol-1))){
                        //Ready for attack
                        possiblePositions.add(new ChessPosition(myRow+1,myCol-1));
                    }
                }
                //Attack RIGHTUP
                if(moveValid(board, new ChessPosition(myRow+1,myCol+1),pieceColor)){
                    if(board.hasPiece(new ChessPosition(myRow+1,myCol+1))){
                        //Ready for attack
                        possiblePositions.add(new ChessPosition(myRow+1,myCol+1));
                    }
                }
                //Move up if no one's there
                if(moveValid(board, new ChessPosition(myRow+1,myCol),pieceColor)){
                    if(!board.hasPiece(new ChessPosition(myRow+1,myCol))) {
                        possiblePositions.add(new ChessPosition(myRow + 1, myCol));
                    }
                }
                //Move 2 if on start
                if(myRow == 2){
                    if(moveValid(board, new ChessPosition(myRow+2,myCol),pieceColor)){
                        if(!board.hasPiece(new ChessPosition(myRow+1,myCol)) && !board.hasPiece(new ChessPosition(myRow+2,myCol))) {
                            possiblePositions.add(new ChessPosition(myRow + 2, myCol));
                        }
                    }
                }

            }
        }



        //Now fill the possibleMoves collection using the possible positions

        if(type != PieceType.PAWN) {
            for (ChessPosition pos : possiblePositions) {
                possibleMoves.add(new ChessMove(myPosition, pos, null));
            }
        }
        else
        {
            //Check if we're promoting
            if((pieceColor == ChessGame.TeamColor.WHITE && myRow == 7) || (pieceColor == ChessGame.TeamColor.BLACK && myRow == 2)) {
                for (ChessPosition pos : possiblePositions) {
                    possibleMoves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
                }
            }
            else{
                for (ChessPosition pos : possiblePositions) {
                    possibleMoves.add(new ChessMove(myPosition, pos, null));
                }
            }
        }

        return possibleMoves;

    }
}

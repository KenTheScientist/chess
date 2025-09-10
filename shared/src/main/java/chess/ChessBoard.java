package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        if(o == this){
//            return true;
//        }
//        ChessBoard that = (ChessBoard) o;
//        for(int i = 0; i < board.length; i++){
//            for(int j = 0; j < board[i].length; j++){
//                if(board[i][j] != that.board[i][j]){
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    @Override
    public boolean equals(Object o)
    {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        //System.out.println("COMPARING TO: \n" + o.toString());
        //System.out.println("COMPARING FROM: \n" + this.toString());

        ChessBoard that = (ChessBoard) o;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j] != that.board[i][j]){
                    return false;
                }
            }
        }

        return true;
        //return (Objects.equals(o.toString(), this.toString()));
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public ChessBoard() {

    }

    @Override
    public String toString() {

        String out = "";

        for(int i = 8; i > 0; i--)
        {
            for(int j = 1; j < 9; j++)
            {
                out += "|";
                ChessPiece renderingPiece = board[i-1][j-1];
                if(renderingPiece != null)
                {
                    out += renderingPiece.toString();
                }
                else
                {
                    out += " ";
                }

            }
            out += "|\n";
        }

        return out;
    }




    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public ChessPiece[][] getBoard(){
        return board;
    }

    public boolean hasPiece(ChessPosition position) {
        return (getPiece(position) != null);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
    }
}

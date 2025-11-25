package chess;
import java.util.*;
import java.util.ArrayList;

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
        char out = options.charAt(index);

        if(pieceColor == ChessGame.TeamColor.WHITE){
            out = Character.toUpperCase(out);
        }

        return String.valueOf(out);

    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType() {
        return this.type;
    }

    public boolean moveValid(ChessBoard board, ChessPosition pos, ChessGame.TeamColor col)
    {
        if(pos.getColumn() < 1 || pos.getColumn() > 8 || pos.getRow() < 1 || pos.getRow() > 8) {
            return false; // Out of bounds
        }
        if(board.hasPiece(pos)){
            return board.getPiece(pos).getTeamColor() != col; //We're on the same team!!!
        }
        return true;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> possiblePositions = new ArrayList<>(); //List of possible move-to positions
        Collection<ChessMove> possibleMoves = new ArrayList<>(); //Output list of ChessMoves
        int myCol = myPosition.column;
        int myRow = myPosition.row;

        if(type == PieceType.BISHOP) {
            possiblePositions = MoveGenerator.getBishopMovePositions(board, myCol, myRow, pieceColor);
        }
        else if(type == PieceType.ROOK) {
            possiblePositions = MoveGenerator.getRookMovePositions(board, myCol, myRow, pieceColor);
        }
        else if(type == PieceType.KNIGHT) {
            possiblePositions = MoveGenerator.getKnightMovePositions(board, myCol, myRow, pieceColor);
        }
        else if(type == PieceType.PAWN) {
            possiblePositions = MoveGenerator.getPawnMovePositions(board, myCol, myRow, pieceColor);
        }
        else if(type == PieceType.QUEEN) {
            possiblePositions = MoveGenerator.getQueenMovePositions(board, myCol, myRow, pieceColor);
        }
        else if(type == PieceType.KING) {
            possiblePositions = MoveGenerator.getKingMovePositions(board, myCol, myRow, pieceColor);
        }

        
        //Now fill the possibleMoves collection using the possible positions
        if(type != PieceType.PAWN) {
            for (ChessPosition pos : possiblePositions) {
                if(moveValid(board, pos, pieceColor)) {
                    possibleMoves.add(new ChessMove(myPosition, pos, null));
                }
            }
        }
        else
        {
            //Check if we're promoting
            if((pieceColor == ChessGame.TeamColor.WHITE && myRow == 7) || (pieceColor == ChessGame.TeamColor.BLACK && myRow == 2)) {
                for (ChessPosition pos : possiblePositions) {
                    if(moveValid(board, pos, pieceColor)) {
                        possibleMoves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                        possibleMoves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                        possibleMoves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
                    }
                }
            }
            else{
                for (ChessPosition pos : possiblePositions) {
                    if(moveValid(board, pos, pieceColor)) {
                        possibleMoves.add(new ChessMove(myPosition, pos, null));
                    }
                }
            }
        }
        return possibleMoves;
    }
}
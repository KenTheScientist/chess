package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessBoard gameBoard;
    public TeamColor teamTurn; //Whose turn is it?

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "gameBoard=" + gameBoard +
                ", teamTurn=" + teamTurn +
                '}';
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null) {
            return null;
        }
        if (!gameBoard.hasPiece(startPosition)) {
            return null;
        }

        ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        ArrayList<ChessMove> outMoves = new ArrayList<>();

        for (int i = 0; i < possibleMoves.size(); i++) {
            ChessMove move = possibleMoves.get(i);
            ChessBoard backupBoard = gameBoard.duplicate();
            //Try the move
            ChessPiece movingPiece = gameBoard.getPiece(move.getStartPosition());
            gameBoard.addPiece(move.getEndPosition(), movingPiece);
            gameBoard.addPiece(move.getStartPosition(), null);
            if (!isInCheck(movingPiece.getTeamColor())) {
                //This move is safe!
                outMoves.add(move);
            }

            gameBoard = backupBoard.duplicate();
        }

        return outMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece movingPiece = gameBoard.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> validMoveList = validMoves(move.getStartPosition());
        if (validMoveList == null) {
            throw new InvalidMoveException();
            //Tried to move a piece that wasn't there!
        }

        TeamColor myColor = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        if (myColor != teamTurn) {
            //We moved out of turn!
            throw new InvalidMoveException();
        }
        ChessBoard backupBoard = gameBoard.duplicate();
        if (validMoveList.contains(move)) {
            //Valid move
            if (move.getPromotionPiece() != null) {
                movingPiece = new ChessPiece(myColor, move.getPromotionPiece());
            }
            gameBoard.addPiece(move.getEndPosition(), movingPiece);
            gameBoard.addPiece(move.getStartPosition(), null);
            if (teamTurn == TeamColor.WHITE) {
                teamTurn = TeamColor.BLACK;
            } else {
                teamTurn = TeamColor.WHITE;
            }
        } else {
            //Invalid move
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (gameBoard.hasPiece(currentPosition) && !gameBoard.getPiece(currentPosition).getTeamColor().equals(teamColor)) {
                    //We are a piece that could be attacking the king
                    ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) gameBoard.getPiece(currentPosition).pieceMoves(gameBoard, currentPosition);
                    //If one of those moves is our king, then we're in check
                    for (int k = 0; k < possibleMoves.size(); k++) {
                        ChessPosition currentEndPos = possibleMoves.get(k).getEndPosition();
                        if (gameBoard.hasPiece(currentEndPos)) {
                            //They're attacking someone! Is it our king?
                            ChessPiece attackingPiece = gameBoard.getPiece(currentEndPos);
                            if (attackingPiece.getTeamColor() == teamColor && attackingPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                //We're in check
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            //Let's see if the king can escape
            //We go through all the moves and see if we can get out of checkmate
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition currentPosition = new ChessPosition(i, j);
                    if (gameBoard.hasPiece(currentPosition) && gameBoard.getPiece(currentPosition).getTeamColor().equals(teamColor)) {
                        //We are one of our own guys trying to save us!
                        ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                        ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) gameBoard.getPiece(currentPosition).pieceMoves(gameBoard, currentPosition);
                        for (int k = 0; k < possibleMoves.size(); k++) {
                            ChessMove tryingMove = possibleMoves.get(k);
                            ChessBoard backupBoard = gameBoard.duplicate();
                            try {
                                makeMove(tryingMove);
                                //Now we are out of check
                                gameBoard = backupBoard.duplicate();
                                return false;
                            } catch (InvalidMoveException e1) {
                                //This puts us in check again!!!
                                gameBoard = backupBoard.duplicate();
                            }

                        }
                    }


                }
            }


            return true;
        } else {
            //We're not even in check!
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            //If we're in check, we're not in stalemate
            return false;
        } else {
            //Let's see if there are any possible moves
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition currentPosition = new ChessPosition(i, j);
                    if (gameBoard.hasPiece(currentPosition) && gameBoard.getPiece(currentPosition).getTeamColor().equals(teamColor)) {
                        //Let's see if we can make a move
                        if (validMoves(currentPosition).size() > 0) {
                            //There's a valid move!
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}

package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BoardRenderer {

    public static String render(ChessBoard board, String ownerColor) {
        ArrayList<String> out = new ArrayList<>();
        ownerColor = "BLACK";//REMOVE THIS!
        //Header
        StringBuilder header = new StringBuilder();
        header.append(RCodes.SET_BG_COLOR_BLACK);
        header.append("   ");
        String[] labels = new String[] {"a","b","c","d","e","f","g","h"};
        if(ownerColor == "BLACK") labels = new String[] {"h","g","f","e","d","c","b","a"};;
        for (String label : labels) {
            header.append(" ");
            header.append(label);
            header.append(RCodes.WIDE_SPACE);
        }
        header.append("   ");
        header.append(RCodes.RESET_BG_COLOR);
        out.add(header.toString());

        //Body
        for(int i = 8; i > 0; i--){
            StringBuilder currentRow = new StringBuilder();
            currentRow.append(RCodes.SET_BG_COLOR_BLACK);
            currentRow.append(" ").append(i).append(" ");
            currentRow.append(RCodes.RESET_BG_COLOR);
            var columnSequence = new int[] {1, 2, 3, 4, 5, 6, 7, 8};
            if(ownerColor == "BLACK") columnSequence = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
            for(int j : columnSequence){
                if((i + j) % 2 == 0){
                    currentRow.append(RCodes.SET_BG_COLOR_DARK_GREY);
                }
                else{
                    currentRow.append(RCodes.SET_BG_COLOR_LIGHT_GREY);
                }
                if(board.hasPiece(new ChessPosition(i, j))){

                    var drawingPiece = board.getPiece(new ChessPosition(i,j));
                    var drawingPieceColor = drawingPiece.getTeamColor();
                    if(drawingPieceColor == ChessGame.TeamColor.WHITE){
                        currentRow.append(RCodes.SET_TEXT_COLOR_WHITE);
                    }
                    else{
                        currentRow.append(RCodes.SET_TEXT_COLOR_BLACK);
                    }
                    String drawingChar = getString(drawingPiece);
                    currentRow.append(drawingChar);
                    currentRow.append(RCodes.RESET_TEXT_COLOR);
                }
                else{
                    currentRow.append(RCodes.EMPTY);
                }
                currentRow.append(RCodes.RESET_BG_COLOR);
            }
            currentRow.append(RCodes.SET_BG_COLOR_BLACK);
            currentRow.append(" ").append(i).append(" ");
            currentRow.append(RCodes.RESET_BG_COLOR);

            out.add(currentRow.toString());
        }

        //Add another header
        out.add(header.toString());

        //If we're black, then we're going to reverse the array
        if(ownerColor.equals("BLACK")){
            Collections.reverse(out);
        }

        //Compile the grid array into the output string
        StringBuilder outString = new StringBuilder();
        for (String s : out) {
            outString.append(s);
            outString.append("\n");
        }

        return outString.toString();
    }

    private static String getString(ChessPiece drawingPiece) {
        String drawingChar = " ";
        drawingChar = switch (drawingPiece.getPieceType()) {
            case KING -> RCodes.BLACK_KING;
            case QUEEN -> RCodes.BLACK_QUEEN;
            case BISHOP -> RCodes.BLACK_BISHOP;
            case KNIGHT -> RCodes.BLACK_KNIGHT;
            case ROOK -> RCodes.BLACK_ROOK;
            case PAWN -> RCodes.BLACK_PAWN;
        };

        return drawingChar;
    }

}

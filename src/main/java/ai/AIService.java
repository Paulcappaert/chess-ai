package ai;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;

/**
 * contains static helper methods for the AI to use to evaluate a chess position
 */
public class AIService {

    public static double getValue(Board board) {
        double value = 0;

        if(board.isStaleMate() || board.isDraw()) {
            return 0;
        } else if(board.isMated() && board.getSideToMove() == Side.BLACK) {
            return 1000;
        } else if(board.isMated() && board.getSideToMove() == Side.WHITE) {
            return -1000;
        }

        for(Square sq : Square.values()) {
            Piece p = board.getPiece(sq);

            if(p == Piece.NONE)
                continue;

            int fileIndex = sq.getFile().getNotation().charAt(0) - 'A';
            int rankIndex = sq.getRank().getNotation().charAt(0) - '1';
            switch(p) {
                case BLACK_PAWN: value -= 1 + getPawnValue(7 - rankIndex);
                    break;
                case WHITE_PAWN: value += 1 + getPawnValue(rankIndex); break;
                case BLACK_KNIGHT: value -= 3 + getKnightValue(7 - rankIndex,fileIndex); break;
                case WHITE_KNIGHT: value += 3 + getKnightValue(rankIndex,fileIndex); break;
                case BLACK_BISHOP: value -= 3; break;
                case WHITE_BISHOP: value += 3; break;
                case BLACK_ROOK: value -= 5; break;
                case WHITE_ROOK: value += 5; break;
                case BLACK_QUEEN: value -= 9; break;
                case WHITE_QUEEN: value += 9; break;
            }
        }
        return value;
    }

    private static double getKnightValue(int rankIndex, int fileIndex) {


        double rankMultiplier = 0;
        double fileMultiplier = 0;

        switch(rankIndex) {
            case 1: rankMultiplier = 1; break;
            case 2: rankMultiplier = 1.03; break;
            case 3: rankMultiplier = 1.06;break;
            case 4: rankMultiplier = 1.1;break;
            case 5: rankMultiplier = 1.2;break;
            case 6: rankMultiplier = 1.3;break;
            case 7: rankMultiplier = 1.4;break;
            case 8: rankMultiplier = 1.11;break;
        }

        switch(fileIndex) {
            case 1:
            case 8: fileMultiplier = .7;break;
            case 2:
            case 7: fileMultiplier = 1;break;
            case 3:
            case 6: fileMultiplier = 1.1;break;
            case 4:
            case 5: fileMultiplier = 1.2;break;
        }

        return 3 * rankMultiplier * fileMultiplier;
    }

    private static double getPawnValue(int rankIndex) {
        switch(rankIndex) {
            case 2: return .25;
            case 3: return .5;
            case 4: return 1;
            case 5: return 2.5;
            case 6: return 4;
            case 7: return 4.5;
            default: return 0;
        }
    }
}

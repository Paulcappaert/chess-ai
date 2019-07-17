package ai;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChessAITest {

    private ChessAI ai;
    private Board board;

    @Before
    public void setup() {
        board = new Board();
        ai = new ChessAIMaxMinImpl(board.clone());
    }

    @Test
    public void oneMove_turnIsBlack() {
        doAIMove();
        assertEquals("correct side to move",Side.BLACK,board.getSideToMove());
    }

    @Test
    public void twoMove_TurnIsWhite() {
        doAIMove();
        doAIMove();

        assertEquals("correct side to move",Side.WHITE,board.getSideToMove());
    }

    @Test
    public void threeMove_TurnIsWhite() {
        doAIMove();
        doAIMove();
        doAIMove();

        assertEquals("correct side to move",Side.BLACK,board.getSideToMove());
    }

    @Test
    public void test4() {
        Move move = new Move(Square.E2,Square.E4);
        board.doMove(move);
        ai.updatePosition(move);

        doAIMove();

        move = new Move(Square.H2,Square.H4);
        board.doMove(move);
        ai.updatePosition(move);

        doAIMove();

        assertEquals("correct side to move",Side.WHITE,board.getSideToMove());
    }

    private void doAIMove() {
        ai.beginThinking(1000);
        Move move = ai.getOptimalMove();
        ai.updatePosition(move);
        board.doMove(move);
    }
}

package ai;

import com.github.bhlangonijr.chesslib.move.Move;

public interface ChessAI {

    /**
     * updates the position the AI is looking at
     * gives the AI to save some of its thinking work so it won't go to waste
     * @param move
     */
    public void updatePosition(Move move);

    public void beginThinking(long maxTime);

    public void stopThinking();

    public Move getOptimalMove();

    public boolean isStopped();
}

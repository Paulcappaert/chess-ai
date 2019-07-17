package ai;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChessAIMaxMinImpl implements ChessAI{

    final private Board board;
    private final Queue<Node> leafNodes;
    private Node root;

    private volatile boolean stop;

    /**
     * board object passed MUST be a clone because changes will be made to the object here
     * @param board
     */
    public ChessAIMaxMinImpl(Board board) {
        this.board = board;
        root = new Node(board.getSideToMove(),board,null,0,new LinkedList<>());
        stop = false;
        leafNodes = new LinkedList<>();
        leafNodes.add(root);
    }

    @Override
    public void updatePosition(Move move) {
        List<Node> children = root.getChildren();

        for(Node node: children) {
            if(node.getMove().equals(move)) {
                removeObsoleteLeafNodes(node.getChildNumber());
                root = node;
                board.doMove(move);
                return;
            }
        }

        board.doMove(move);
        root = new Node(board.getSideToMove(),board.clone(),null,0,new LinkedList<>());
        leafNodes.clear();
        leafNodes.add(root);
    }

    @Override
    public void beginThinking(long maxTime) {
        stop = false;
        long stopTime = System.currentTimeMillis() + maxTime/3;
        System.out.println(leafNodes.size());
        while (!stop && stopTime > System.currentTimeMillis() && leafNodes.size() > 0) {
            Node curr = leafNodes.remove();
            Board board = curr.getBoard();
            MoveList possibleMoves = null;

            try {
                possibleMoves = MoveGenerator.generateLegalMoves(board);
            } catch (MoveGeneratorException e) {
                e.printStackTrace();
            }

            for(Move move : possibleMoves) {
                board.doMove(move);
                Node newNode = new Node(board.getSideToMove(),board.clone(),move,
                                            curr.getNextChildNumber(),curr.getChildNumbersToRoot());
                curr.addChild(newNode);
                leafNodes.add(newNode);
                board.undoMove();
            }

            //the current node will no longer need the board because all of its legal moves
            //have been added to the tree
            curr.removeBoard();
        }
    }

    @Override
    public void stopThinking() {
        stop = true;
    }

    @Override
    public Move getOptimalMove() {
        return root.getBestMove();
    }

    @Override
    public boolean isStopped() {
        return stop;
    }

    /**
     * removes all leaf nodes that are not descendants of the nth child of the root node
     * @param n
     */
    private void removeObsoleteLeafNodes(int n) {
        int size = leafNodes.size();
        for(int i = 0;i < size;i++) {
            Node leaf = leafNodes.remove();
            if(leaf.removeRoot() == n) {
                leafNodes.add(leaf);
            }
        }
    }
}

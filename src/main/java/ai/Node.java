package ai;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.*;

public class Node {

    private final Side color;
    private boolean max;
    private List<Node> children;
    private Move move;
    private Board board;

    //1 means that this is the oldest child of the parent node
    //0 is the root node
    final private int childNumber;
    private int nextChildNumber;

    //a queue of the child numbers of all of the predecessors of this node, starting with the
    //child of the first node and endng with the current nodes child number
    private Queue<Integer> childNumbersToRoot;


    public Node(Side color, Board board, Move move, int childNumber, Queue<Integer> previousChildNumbersToRoot) {
        this.color = color;
        this.board = board;
        if(color == Side.WHITE)
            this.max = true;
        else
            this.max = false;
        children = new ArrayList<>();
        this.move = move;
        this.childNumber = childNumber;
        childNumbersToRoot = new LinkedList<>();

        //creates a copy of all of the child numbers for all the predecessors to this node
        for(int i = 0;i < previousChildNumbersToRoot.size();i++) {
            int n = previousChildNumbersToRoot.remove();
            childNumbersToRoot.add(n);
            previousChildNumbersToRoot.add(n);
        }

        if(childNumber != 0) {
            childNumbersToRoot.add(childNumber);
        }

        nextChildNumber = 1;
    }

    public void addChild(Node n) {
        children.add(n);
        nextChildNumber++;
    }

    public Move getBestMove() {

        if(children.size() == 0) {
            return null;
        }

        double best = children.get(0).getValue();
        Move bestMove = children.get(0).move;
        for(Node child : children) {
            double val = child.getValue();
            if(max && val > best) {
                best = val;
                bestMove = child.move;
            } else if(!max && val < best) {
                best = val;
                bestMove = child.move;
            }
        }
        return bestMove;
    }

    public double getValue() {
        if(children.isEmpty())
            return AIService.getValue(this.board);
        else {
            double val = children.get(0).getValue();
            for(Node child : children) {
                double cVal = child.getValue();
                if(max && cVal > val)
                    val = cVal;
                else if(!max && cVal < val)
                    val = cVal;
            }
            return val;
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    public Board getBoard() {
        return board;
    }

    /**
     * leaves the board object ready to be removed by the garbage collector
     * board objects take up a lot of memory, so it would be wasteful to keep a million copies
     */
    public void removeBoard() {
        this.board = null;
    }

    public Side getSide() {
        return color;
    }

    public Move getMove() {return move;}

    public int getChildNumber() {return childNumber;}

    public int getNextChildNumber(){return nextChildNumber;}

    public int removeRoot() {return childNumbersToRoot.remove();}

    public Queue<Integer> getChildNumbersToRoot(){return childNumbersToRoot;}
}
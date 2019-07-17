package gui;


import ai.ChessAI;
import ai.ChessAIMaxMinImpl;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller implements ActionListener {

    private Thread ai;
    private ChessAI chessAI;
    private volatile boolean isThinking;

    private Window window;
    private Board board;
    private BoardDisplay boardDisplay;

    private File selectedFile;
    private Rank selectedRank;
    private boolean selected;
    private Piece selectedImageKey;

    private ArrayList<Move> redoMoves;



    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board.getFen());
        Window window = new Window();
        new Controller(window,board);
    }

    public Controller(Window window,Board board) {
        this.window = window;
        this.board = board;
        this.boardDisplay = new BoardDisplay(getSquareArray());
        this.window.add(boardDisplay,BorderLayout.CENTER);

        boardDisplay.addMouseListener(new MouseControl());
        boardDisplay.addMouseMotionListener(new MouseMotionControl());
        this.window.setVisible(true);
        this.window.addController(this);

        redoMoves = new ArrayList<>();

        chessAI = new ChessAIMaxMinImpl(board.clone());
        ai = new Thread(new aiRunnable());
        isThinking = false;
    }

    private synchronized void moveComplete(Move move) {


        if(move != null) {
            chessAI.updatePosition(move);
        }

        updateBoardDisplay();

        if(board.getSideToMove() == window.getSelectedColor()) {
            isThinking = true;
            window.setAIThinking(true);
            EventQueue.invokeLater(new aiRunnable());
        }

        //window.setDetails(Double.toString(board.value()));

    }

    public void updateBoardDisplay() {
        boardDisplay.setSquares(getSquareArray());
        boardDisplay.repaint();
    }

    public class MouseControl extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            selected = true;
            int selectedX = (e.getX() - 10)/68;
            int selectedY = (554 - e.getY())/68;
            selectedFile = getFileFromIndex(selectedX);
            selectedRank = getRankFromIndex(selectedY);
            Piece selectedPiece = board.getPiece(Square.encode(selectedRank,selectedFile));
            if(selectedFile == File.NONE || selectedRank == Rank.NONE || selectedPiece == Piece.NONE)
                selected = false;
            else
                selectedImageKey = selectedPiece;

            boardDisplay.setSelected(selected);
            boardDisplay.setSelectedX(selectedX);
            boardDisplay.setSelectedY(selectedY);
            boardDisplay.setImage(selectedImageKey);
        }

        public void mouseReleased(MouseEvent e) {
            if(selected == false)
                return;

            selected = false;
            File file = getFileFromIndex((e.getX() - 10)/68);
            Rank rank = getRankFromIndex((554 - e.getY())/68);
            if(file != File.NONE && rank != Rank.NONE || canMove() && !isThinking) {
                try {
                    MoveList moves = MoveGenerator.generateLegalMoves(board);
                    Move move = new Move(Square.encode(selectedRank,selectedFile),Square.encode(rank,file),window.getPromotion(board.getSideToMove()));
                    if(moves.contains(move)) {
                        chessAI.stopThinking();
                        board.doMove(move,false);
                        window.noPromotion();
                        moveComplete(move);
                        //window.setHistory(board.getHistoryStrings(),board.getCurrMove());
                    } else {
                        updateBoardDisplay();
                    }
                } catch (MoveGeneratorException ex) {
                    ex.printStackTrace();
                }

            } else {
                updateBoardDisplay();
            }

            boardDisplay.setSelected(false);
        }
    }

    public class MouseMotionControl extends MouseMotionAdapter {
        private int drawX;
        private int drawY;

        public void mouseDragged(MouseEvent e) {
            drawX = e.getX() - 34;
            drawY = e.getY() - 34;
            if(drawX < -34 || drawX > 520 || drawY < -34 || drawY > 530)
                selected = false;
            boardDisplay.repaint();
            boardDisplay.setDrawX(drawX);
            boardDisplay.setDrawY(drawY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() instanceof JButton) {
            Move move;
            JButton button = (JButton) event.getSource();
            if(button.getActionCommand().equals("Restart")) {
                board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                chessAI = new ChessAIMaxMinImpl(board.clone());
                //window.setHistory(board.getHistoryStrings(),board.getCurrMove());
            } else if(button.getActionCommand().equals("Previous Move")) {
                move = board.undoMove();
                redoMoves.add(0,move);
                //window.setHistory(board.getHistoryStrings(),board.getCurrMove());
            } else if(button.getActionCommand().equals("Next Move")) {
                move = redoMoves.remove(0);
                board.doMove(move);
                //window.setHistory(board.getHistoryStrings(),board.getCurrMove());
            } else if(button.getActionCommand().equals("pause AI")) {
                chessAI.stopThinking();
            }
            move = null;
            moveComplete(move);
        }

    }

    Piece[][] getSquareArray() {
        Piece[][] squareArray = new Piece[8][8];
        for(int i = 0;i < 8;i++) {
            for(int j = 0;j < 8;j++) {
                squareArray[i][j] = board.getPiece(Square.squareAt(j * 8 + i));
            }
        }
        return squareArray;
    }

    public Rank getRankFromIndex(int i) {
        switch(i) {
            case 0: return Rank.RANK_1;
            case 1: return Rank.RANK_2;
            case 2: return Rank.RANK_3;
            case 3: return Rank.RANK_4;
            case 4: return Rank.RANK_5;
            case 5: return Rank.RANK_6;
            case 6: return Rank.RANK_7;
            case 7: return Rank.RANK_8;
            default: return Rank.NONE;
        }
    }

    public File getFileFromIndex(int i) {
        switch(i) {
            case 0: return File.FILE_A;
            case 1: return File.FILE_B;
            case 2: return File.FILE_C;
            case 3: return File.FILE_D;
            case 4: return File.FILE_E;
            case 5: return File.FILE_F;
            case 6: return File.FILE_G;
            case 7: return File.FILE_H;
            default: return File.NONE;
        }
    }

    public boolean canMove() {
        if(board.isDraw() || board.isMated() || board.isStaleMate()) {
            return false;
        } else {
            return true;
        }
    }

    private class aiRunnable implements Runnable {

        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                chessAI.beginThinking(window.getThinkingTime());
                if(!chessAI.isStopped()) {
                    Move optimalMove = chessAI.getOptimalMove();
                    System.out.println((System.currentTimeMillis() - start) / 1000.0);
                    if(optimalMove != null) {
                        board.doMove(optimalMove);
                        chessAI.updatePosition(optimalMove);
                        updateBoardDisplay();
                    }
                }
                isThinking = false;
                window.setAIThinking(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

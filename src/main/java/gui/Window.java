package gui;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;

public class Window extends JFrame {


    private JButton nextMove,prevMove,restart, pauseAI;
    private BoardDisplay display;
    private JTextArea history;
    private JTextArea details;
    private JButton computer;
    public JRadioButton whiteAuto;
    public JRadioButton blackAuto;
    public JRadioButton neitherAuto;
    public ButtonGroup promos;
    public ButtonGroup auto;
    private JTextArea thinkingTime;

    public Window() {
        setSize(750,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel promotions = new JPanel();
        promos = new ButtonGroup();
        JRadioButton knight = new JRadioButton("knight");
        knight.setActionCommand("knight");
        promos.add(knight);
        JRadioButton bishop = new JRadioButton("bishop");
        bishop.setActionCommand("bishop");
        promos.add(bishop);
        JRadioButton rook = new JRadioButton("rook");
        rook.setActionCommand("rook");
        promos.add(rook);
        JRadioButton queen = new JRadioButton("queen");
        queen.setActionCommand("queen");
        promos.add(queen);
        promotions.add(knight);
        promotions.add(bishop);
        promotions.add(rook);
        promotions.add(queen);
        this.add(promotions,BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(150,100));
        history = new JTextArea("Move History");
        scrollPane.setViewportView(history);
        add(scrollPane, BorderLayout.EAST);

        JPanel bPanel = new JPanel();
        nextMove = new JButton("Next Move");
        prevMove = new JButton("Previous Move");
        restart = new JButton("Restart");
        pauseAI = new JButton("pause AI");
        pauseAI.setEnabled(false);
        auto = new ButtonGroup();
        whiteAuto = new JRadioButton("Auto White ");
        whiteAuto.setActionCommand("1");
        blackAuto = new JRadioButton("Auto Black ");
        blackAuto.setActionCommand("2");
        neitherAuto = new JRadioButton("No ai");
        auto.add(whiteAuto);
        auto.add(blackAuto);
        auto.add(neitherAuto);
        neitherAuto.setSelected(true);
        thinkingTime = new JTextArea("1000");
        bPanel.add(nextMove);
        bPanel.add(prevMove);
        bPanel.add(restart);
        //bPanel.add(pauseAI);
        bPanel.add(whiteAuto);
        bPanel.add(blackAuto);
        bPanel.add(neitherAuto);
        bPanel.add(thinkingTime);

        add(bPanel, BorderLayout.NORTH);
    }

    public void addController(Controller controller) {
        restart.addActionListener(controller);
        prevMove.addActionListener(controller);
        nextMove.addActionListener(controller);
    }

    public void setHistory(ArrayList<String> historyStrings, int currMove) {
        int move = 0;
        int position = 0;
        Highlighter h = history.getHighlighter();
        h.removeAllHighlights();
        history.setText("");
        for(String s : historyStrings) {

            String line = (move/2 + 1) + ". " + s + "\n";
            position += line.length();
            history.append(line);
            move++;

            if(move == currMove - 1) {
                try {
                    h.addHighlight(position - 1, position, DefaultHighlighter.DefaultPainter);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDetails(String s) {
        details.setText(s);
    }

    public Piece getPromotion(Side color) {

        if(promos.getSelection() == null)
            return Piece.NONE;

        String selectedPiece = promos.getSelection().getActionCommand();

        switch(selectedPiece) {
            case "knight":
                if(color == Side.WHITE) return Piece.WHITE_KNIGHT;
                else return Piece.BLACK_KNIGHT;
            case "bishop":
                if(color == Side.WHITE) return Piece.WHITE_BISHOP;
                else return Piece.BLACK_BISHOP;
            case "rook":
                if(color == Side.WHITE) return Piece.WHITE_ROOK;
                else return Piece.BLACK_ROOK;
            case "queen":
                if(color == Side.WHITE) return Piece.WHITE_QUEEN;
                else return Piece.BLACK_QUEEN;
            default: return Piece.NONE;
        }
    }

    public void noPromotion() {
        promos.clearSelection();
    }

    public String getAuto() {
        return auto.getSelection().getActionCommand();
    }

    public int getThinkingTime() {
        return Integer.valueOf(thinkingTime.getText());
    }

    public void setAIThinking(boolean isThinking) {
        pauseAI.setEnabled(isThinking);
        prevMove.setEnabled(!isThinking);
        nextMove.setEnabled(!isThinking);
        restart.setEnabled(!isThinking);
    }

    public Side getSelectedColor() {
        if(getAuto() == "1") {
            return Side.WHITE;
        } else if(getAuto() == "2") {
            return Side.BLACK;
        } else {
            return null;
        }
    }
}

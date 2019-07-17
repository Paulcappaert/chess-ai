package gui;

import com.github.bhlangonijr.chesslib.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BoardDisplay extends JComponent {

    private HashMap<Piece, Image> images;
    private Piece[][] pieces;
    private boolean selected;
    private Image selectedImage;

    private int selectedX;
    private int selectedY;

    private int drawX,drawY;

    public BoardDisplay(Piece[][] pieces) {
        setPreferredSize(new Dimension(554,554));
        images = new HashMap<Piece,Image>();
        this.pieces = pieces;
        selectedImage = null;

        initializeImages();
        repaint();
    }

    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;

        for(int i = 0;i < 8;i++) {
            for(int j = 0;j < 8;j++) {
                g2d.drawImage(images.get(Piece.NONE), 10 + 68*i, 486 - 68*j, null);
                if(!selected || i != selectedX || j != selectedY)
                    g2d.drawImage(images.get(pieces[i][j]), 10 + 68*i, 486 - 68*j, null);
            }
        }

        if(selected) {
            g2d.drawImage(selectedImage, drawX, drawY, null);
        }
    }

    private void initializeImages() {
        try {
            images.put(Piece.NONE, ImageIO.read(new File("images/square.png")));
            images.put(Piece.WHITE_KING, ImageIO.read(new File("images/wKing.png")));
            images.put(Piece.WHITE_QUEEN, ImageIO.read(new File("images/wQueen.png")));
            images.put(Piece.WHITE_ROOK, ImageIO.read(new File("images/wRook.png")));
            images.put(Piece.WHITE_BISHOP, ImageIO.read(new File("images/wBishop.png")));
            images.put(Piece.WHITE_KNIGHT, ImageIO.read(new File("images/wKnight.png")));
            images.put(Piece.WHITE_PAWN, ImageIO.read(new File("images/wPawn.png")));
            images.put(Piece.BLACK_KING, ImageIO.read(new File("images/bKing.png")));
            images.put(Piece.BLACK_QUEEN, ImageIO.read(new File("images/bQueen.png")));
            images.put(Piece.BLACK_ROOK, ImageIO.read(new File("images/bRook.png")));
            images.put(Piece.BLACK_BISHOP, ImageIO.read(new File("images/bBishop.png")));
            images.put(Piece.BLACK_KNIGHT, ImageIO.read(new File("images/bKnight.png")));
            images.put(Piece.BLACK_PAWN, ImageIO.read(new File("images/bPawn.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSelected(boolean b) {
        selected = b;
    }

    public void setSelectedX(int x) {
        selectedX = x;
    }

    public void setSelectedY(int y) {
        selectedY = y;
    }

    public void setImage(Piece selectedImageKey) {
        selectedImage = images.get(selectedImageKey);

    }

    public void setDrawX(int drawX) {
        this.drawX = drawX;
    }

    public void setDrawY(int drawY) {
        this.drawY = drawY;
    }

    public void setSquares(Piece[][] squareArray) {
        pieces = squareArray;
    }
}

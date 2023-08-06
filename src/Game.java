import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class Game {
    enum State {
        PLACING, PLAYING, OVER
    }
    enum Turn {
        PLAYER, ENEMY
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    State state = State.PLACING;
    Turn turn = Turn.PLAYER;
    Board board;
    public static Ship selectedShip = null;
    public Game(Board board) {
        this.board = board;
    }
    public void click(int cX, int cY, boolean hit) {
        Peg.Click click = hit ? Peg.Click.HIT : Peg.Click.MISS;
        if (!overlap(cX, cY) && boundary(cX, cY)) {
            board.pegList(cX, cY, click);
            switchTurn();
        }

    }
    public boolean overlap(int cX, int cY) {
        for (Peg p : Board.pe) {
            return cX == p.pX && cY == p.pY;
        }
        return false;
    }
    public boolean boundary(int cX, int cY) {
        int lX = 0;
        int rX = 9;
        int uX = 0;
        int dX = 9;
        if (turn == Turn.PLAYER) {
            lX += 11;
            rX += 11;
        }
        return cX >= lX && cX <= rX && cY >= uX && cY <= dX;
    }
    public void switchTurn() {
        turn = (turn == Turn.PLAYER) ? Turn.ENEMY : Turn.PLAYER;
    }
    public boolean over() {
        for (Ship s : Board.sh) {
            return !(s.player == Ship.Player.ENEMY_HIDDEN);
        }
        return true;
    }
    public void mousePressed(MouseEvent e) {
        if (state == State.PLACING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
        } else if (state == State.PLAYING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
            click(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE, selectedShip != null);
        }
        board.repaint();
    }
    public void mouseReleased(MouseEvent e) {
        if (state == State.PLACING) {
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
                selectedShip.canPlace(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
                selectedShip = null;
            }
        }
        board.repaint();
    }
    public void mouseClicked(MouseEvent e) {
        if (state == State.PLACING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
                if (SwingUtilities.isRightMouseButton(e) && selectedShip.click == Ship.Click.PLACED) {
                    selectedShip.rotate();
                    selectedShip.canPlace(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
                }
                selectedShip = null;
            }
            if (board.button(e.getX(), e.getY()) && board.placed() && state == Game.State.PLACING) {
                state = Game.State.PLAYING;
            }
        }
        board.repaint();
    }
    public void mouseExited() {
        if (state == State.PLACING) {
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
                selectedShip.click = Ship.Click.DESELECTED;
                selectedShip.deselect();
                selectedShip = null;
            }
        }
        board.repaint();
    }
    public void mouseEntered() {}
    public void mouseDragged(MouseEvent e) {
        if (state == State.PLACING) {
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
                selectedShip.move(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
            }
        }
        board.repaint();
    }
    public void mouseMoved() {}
}

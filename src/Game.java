import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class Game {
    enum State {
        PLACING, PLAYING, MENU
    }
    enum Turn {
        PLAYER, ENEMY
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    State state = State.PLACING;
    Turn turn;
    Board board;
    public static Ship selectedShip = null;
    public Game(Board board) {
        this.board = board;
    }
    public void mousePressed(MouseEvent e) {
        selectedShip = Board.getShip(e.getX(), e.getY());
    }
    public void mouseReleased(MouseEvent e) {
        if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
            selectedShip.canPlace(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
            selectedShip = null;
            board.repaint();
        }
    }
    public void mouseClicked(MouseEvent e) {
        selectedShip = Board.getShip(e.getX(), e.getY());
        if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
            if (SwingUtilities.isRightMouseButton(e) && selectedShip.click == Ship.Click.PLACED) {
                selectedShip.rotate();
                selectedShip.canPlace(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
            }
            selectedShip = null;
            board.repaint();
        }
    }
    public void mouseExited() {
        if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
            selectedShip.click = Ship.Click.DESELECTED;
            selectedShip.deselect();
            selectedShip = null;
            board.repaint();
        }
    }
    public void mouseEntered() {}
    public void mouseDragged(MouseEvent e) {
        if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER) {
            selectedShip.move(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
            board.repaint();
        }
    }
    public void mouseMoved() {}
}

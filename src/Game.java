import javax.swing.*;
import java.awt.event.MouseEvent;
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
    public void myClick(int cX, int cY, boolean hit) {
        Peg peg = overlap(cX, cY);
        Peg.Click click = hit ? Peg.Click.HIT : Peg.Click.MISS;
        if (peg == null && boundary(cX, cY)) {
            board.pegList(cX, cY, click);
            if (hit) {
                selectedShip.sinking++;
                sunk();
            }
            if (over()) state = State.OVER;
            selectedShip = null;
            switchTurn();
            enemyClick();
        }
    }
    public void enemyClick() {
        while (true) {
            int cX = (int) (Math.random() * 10) + 1;
            int cY = (int) (Math.random() * 10) + 1;

            selectedShip = Board.getShip(cX * SQR_SIZE, cY * SQR_SIZE);

            Peg peg = overlap(cX, cY);
            Peg.Click click = selectedShip != null ? Peg.Click.HIT : Peg.Click.MISS;

            if (peg == null && boundary(cX, cY)) {
                board.pegList(cX, cY, click);
                if (selectedShip != null) {
                    selectedShip.sinking++;
                    sunk();
                }
                if (over()) state = State.OVER;
                selectedShip = null;
                switchTurn();
                break; // Break the loop since a valid hit was made.
            }
        }
    }

    public Peg overlap(int cX, int cY) {
        for (Peg p : Board.pe) {
            if (cX == p.pX && cY == p.pY) return p;
        }
        return null;
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
    public void sunk() {
        for (Ship s : Board.sh) {
            if (s.sinking >= s.length) {
                s.sunk = Ship.Sunk.SUNK;
            }
        }
    }
    public boolean over() {
        Ship.Player player = switch (turn) {
            case ENEMY -> Ship.Player.PLAYER_SHOWN;
            case PLAYER -> Ship.Player.ENEMY_HIDDEN;
        };

        for (Ship s : Board.sh) {
            if (s.player == player && s.sunk == Ship.Sunk.NOT_SUNK) {
                return false;
            }
        }
        return true;
    }
    public void mousePressed(MouseEvent e) {
        if (state == State.PLACING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
        } else if (state == State.PLAYING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
            myClick(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE, selectedShip != null);
        }
        board.repaint();
    }
    public void mouseReleased(MouseEvent e) {
        if (state == State.PLACING) {
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER_SHOWN) {
                selectedShip.canPlace(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
                selectedShip = null;
            }
        }
        board.repaint();
    }
    public void mouseClicked(MouseEvent e) {
        if (state == State.PLACING) {
            selectedShip = Board.getShip(e.getX(), e.getY());
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER_SHOWN) {
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
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER_SHOWN) {
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
            if (selectedShip != null && selectedShip.player == Ship.Player.PLAYER_SHOWN) {
                selectedShip.move(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
            }
        }
        board.repaint();
    }
    public void mouseMoved() {}
}

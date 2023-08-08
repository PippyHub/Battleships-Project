import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
public class Game {
    enum State {
        PLACING, PLAYING, OVER
    }
    enum Turn {
        PLAYER, ENEMY
    }
    enum Difficulty {
        EASY, HARD
    }
    static class Coordinate {
        int x;
        int y;
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    private ArrayList<Integer> previousClicks = new ArrayList<>();
    State state = State.PLACING;
    Turn turn = Turn.PLAYER;
    Board board;
    Difficulty difficulty;
    public static Ship selectedShip = null;
    int cycle;
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
            int cX = 0;
            int cY = 0;
            Ship sunk = getSunk();
            if (difficulty == Difficulty.EASY) {
                Coordinate coordinate = easyDifficulty();
                cX = coordinate.x;
                cY = coordinate.y;
            } else if (difficulty == Difficulty.HARD) {
                Coordinate coordinate = hardDifficulty(cX, cY, sunk);
                cX = coordinate.x;
                cY = coordinate.y;
            }
            selectedShip = Board.getShip(cX * SQR_SIZE, cY * SQR_SIZE);
            Peg peg = overlap(cX, cY);
            Peg.Click click = selectedShip != null ? Peg.Click.HIT : Peg.Click.MISS;

            if (peg == null && boundary(cX, cY)) {
                previousClicks.add(cX);
                previousClicks.add(cY);
                board.pegList(cX, cY, click);
                if (selectedShip != null) {
                    selectedShip.sinking++;
                    sunk();
                }
                if (over()) state = State.OVER;
                selectedShip = null;
                if (sunk != null && sunk.sunk == Ship.Sunk.SUNK) cycle = 0;
                switchTurn();
                break;
            }
        }
    }
    public Ship getSunk() {
        for (Ship s : Board.sh) {
            if (s.sunk == Ship.Sunk.NOT_SUNK) {
                return s;
            }
        }
        return null;
    }
    public Coordinate easyDifficulty() {
        int cX = (int) (Math.random() * 10);
        int cY = (int) (Math.random() * 10);

        return new Coordinate(cX, cY);
    }
    public Coordinate hardDifficulty(int cX, int cY, Ship sunk) {
        if (sunk != null) {
            cX = sunk.sX;
            cY = sunk.sY;
            switch (sunk.rotation) {
                case VERTICAL -> cY += cycle;
                case HORIZONTAL -> cX += cycle;
            }
            cycle++;
        }
        return new Coordinate(cX, cY);
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
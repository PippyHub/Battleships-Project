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
    enum Hunting {
        RANDOM, HUNTING_RANDOM, HUNTING_DIRECTION
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
    State state = State.PLACING;
    Turn turn = Turn.PLAYER;
    Board board;
    Difficulty difficulty;
    public static Ship selectedShip = null;
    int cycle;
    Hunting hunting = Hunting.RANDOM;
    private final ArrayList<Integer> previousHits = new ArrayList<>();
    boolean lastHit;
    boolean firstHuntingMove = true;
    boolean wrongDirection;
    int randomCycle, huntingCycle;
    int huntX, huntY;
    int miss;
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
            Coordinate coordinates = null;
            if (miss >= 2) {
                hunting = Hunting.HUNTING_RANDOM;
            }
            switch (difficulty) {
                case EASY -> coordinates = easyDifficulty();
                case HARD -> coordinates = hardDifficulty(cX, cY, sunk);
            }
            cX = coordinates.x;
            cY = coordinates.y;
            selectedShip = Board.getShip(cX * SQR_SIZE, cY * SQR_SIZE);
            Peg peg = overlap(cX, cY);
            Peg.Click click = selectedShip != null ? Peg.Click.HIT : Peg.Click.MISS;
            if (peg == null && boundary(cX, cY)) {
                board.pegList(cX, cY, click);
                previousHits.add(cX);
                previousHits.add(cY);
                if (selectedShip != null) {
                    miss = 0;
                    selectedShip.sinking++;
                    sunk();
                    lastHit = true;
                    if (hunting == Hunting.RANDOM) {
                        hunting = Hunting.HUNTING_RANDOM;
                        firstHuntingMove = true;
                    } else if (hunting == Hunting.HUNTING_RANDOM && !firstHuntingMove){
                        hunting = Hunting.HUNTING_DIRECTION;
                    }
                    if (hunting == Hunting.HUNTING_DIRECTION) {
                        wrongDirection = false;
                        if (selectedShip.sunk == Ship.Sunk.SUNK) {
                            hunting = Hunting.RANDOM;
                        }
                    }
                } else {
                    lastHit = false;
                    if (hunting == Hunting.HUNTING_DIRECTION) {
                        miss++;
                        wrongDirection = true;
                    }
                }
                if (over()) state = State.OVER;
                selectedShip = null;
                if (sunk != null && sunk.sunk == Ship.Sunk.SUNK) cycle = 0;
                switchTurn();
                break;
            } else {
                if (hunting == Hunting.HUNTING_DIRECTION) {
                    miss++;
                    wrongDirection = true;
                }
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
        int cX = 0;
        int cY = 0;
        if (randomCycle > 4) {
            hunting = Hunting.RANDOM;
        }
        if (hunting == Hunting.HUNTING_RANDOM) {
            if (firstHuntingMove) {
                huntX = previousHits.get(previousHits.size() - 2);
                huntY = previousHits.get(previousHits.size() - 1);
                firstHuntingMove = false;
            }
            cX = huntX;
            cY = huntY;
            int[][] randomDirection = {
                {0, -1},
                {1, 0},
                {0, 1},
                {-1, 0},
            };
            cX += randomDirection[huntingCycle][0];
            cY += randomDirection[huntingCycle][1];
            if (huntingCycle == 3) huntingCycle = 0;
            else huntingCycle++;
            randomCycle++;
        }
        if (hunting == Hunting.HUNTING_DIRECTION) {
            int prevX = previousHits.get(previousHits.size() - 2);
            int prevY = previousHits.get(previousHits.size() - 1);

            cX = prevX;
            cY = prevY;

            int signX;
            int signY;

            signX = (int) Math.signum(prevX - huntX);
            signY = (int) Math.signum(prevY - huntY);

            cX += signX;
            cY += signY;

            if (wrongDirection) {
                previousHits.add(huntX);
                previousHits.add(huntY);
                cX = huntX;
                cY = huntY;
                cX -= signX;
                cY -= signY;
            }

            huntingCycle = (int) ((Math.random() * 3) + 1);
            randomCycle = 0;
        }
        if (hunting == Hunting.RANDOM){
            cX = (int) (Math.random() * 10);
            cY = (int) (Math.random() * 10);

            huntingCycle = (int) ((Math.random() * 3) + 1);
            randomCycle = 0;
        }
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
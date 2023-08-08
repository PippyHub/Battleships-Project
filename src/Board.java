import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    static final int SQR_SIZE = 64;
    static final int SQR_AMOUNT = 10;
    static final int BOARD_SIZE = SQR_SIZE * SQR_AMOUNT;
    static final int SHIP_AREA_SIZE = SQR_SIZE * 11;
    static LinkedList<Peg> pe = new LinkedList<>();
    static LinkedList<Ship> sh = new LinkedList<>();
    Images img = new Images();
    private final Image[] images;
    private final Game game;
    int buttonWidth, buttonHeight, buttonX, buttonY, buttonArc;
    public Board() {
        images = img.loadImages();
        game = new Game(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        player();
        enemy();
    }
    public void player() {
        shipList(11, 1, Ship.Name.CARRIER, Ship.Player.PLAYER_SHOWN);
        shipList(13, 1, Ship.Name.BATTLESHIP, Ship.Player.PLAYER_SHOWN);
        shipList(15, 1, Ship.Name.CRUISER, Ship.Player.PLAYER_SHOWN);
        shipList(17, 1, Ship.Name.SUBMARINE, Ship.Player.PLAYER_SHOWN);
        shipList(19, 1, Ship.Name.DESTROYER, Ship.Player.PLAYER_SHOWN);
    }
    public void enemy() {
        shipList(1, 1, Ship.Name.CARRIER, Ship.Player.ENEMY_HIDDEN);
        shipList(3, 1, Ship.Name.BATTLESHIP, Ship.Player.ENEMY_HIDDEN);
        shipList(5, 1, Ship.Name.CRUISER, Ship.Player.ENEMY_HIDDEN);
        shipList(7, 1, Ship.Name.SUBMARINE, Ship.Player.ENEMY_HIDDEN);
        shipList(9, 1, Ship.Name.DESTROYER, Ship.Player.ENEMY_HIDDEN);

        int enemyPlaced = 0;
        while (enemyPlaced != 5) {
            enemyPlaced = 0;
            for (Ship s : sh) {
                if (s.player == Ship.Player.ENEMY_HIDDEN) {
                    int sX = (int) (Math.random() * 10) + 1;
                    int sY = (int) (Math.random() * 10) + 1;
                    sX += 10;
                    if (((int) (Math.random() * 2) + 1) == 1) s.rotate();
                    if(s.enemyPlace(sX, sY)) enemyPlaced++;
                }
            }
        }
    }
    public void shipList(int sX, int sY, Ship.Name name, Ship.Player player) {
        new Ship(sX, sY, name, player, sh);
    }
    public void pegList(int sX, int sY, Peg.Click click) {
        new Peg(sX, sY, click, pe);
    }
    public void paint(Graphics g) {
        g.setColor(new Color(86, 86, 86));
        g.fillRect(0, 0, BOARD_SIZE + SHIP_AREA_SIZE, BOARD_SIZE);
        if (game.state != Game.State.OVER) {
            drawGrid(g, SQR_AMOUNT, BOARD_SIZE, 0, 0);
            if (game.state == Game.State.PLAYING) drawGrid(g, SQR_AMOUNT, BOARD_SIZE, SHIP_AREA_SIZE, 0);
        }
        paintShips(g);
        paintPegs(g);
        if (placed() && game.state == Game.State.PLACING) button(g);
    }
    public boolean placed() {
        int shipAmount = 0;
        for (Ship s : sh){
            if (s.player == Ship.Player.PLAYER_SHOWN) {
                shipAmount++;
            }
        }
        int placed = 0;
        for (Ship s : sh) {
            if (s.click == Ship.Click.PLACED) {
                placed++;
            }
        }
        return placed >= shipAmount;
    }
    public void button(Graphics g) {
        buttonWidth = 300;
        buttonHeight = 100;
        buttonX = BOARD_SIZE + (SHIP_AREA_SIZE / 2) - (buttonWidth / 2);
        buttonY = (int) (BOARD_SIZE - (BOARD_SIZE * 0.25) - (buttonHeight / 2.0));
        buttonArc = 20;
        drawButton(g, buttonWidth, buttonHeight, buttonX, buttonY, buttonArc);
    }
    public void drawButton(Graphics g, int width, int height, int x, int y, int arc) {
        g.setColor(Color.magenta);
        g.fillRoundRect(x, y, width/ 2, height, arc, arc);
        g.setColor(Color.red);
        g.fillRoundRect(x + width / 2, y, width / 2, height, arc, arc);
    }
    public void drawGrid(Graphics g, int gridSize, int size, int startX, int startY) {
        g.setColor(Color.BLACK);
        int cellSize = size / gridSize;

        startX = (startX / cellSize) * cellSize;
        startY = (startY / cellSize) * cellSize;

        for (int x = startX; x <= size + startX; x += cellSize) {
            g.drawLine(x, startY, x, size);
        }
        for (int y = startY; y <= size + startY; y += cellSize) {
            g.drawLine(startX, y, startX + size, y);
        }
    }
    public void paintShips(Graphics g) {
        for (Ship s : sh) {
            if (s.player == Ship.Player.PLAYER_SHOWN || s.player == Ship.Player.ENEMY_HIDDEN && s.sunk == Ship.Sunk.SUNK) {
                Color color = switch (s.name) {
                    case CARRIER -> Color.orange;
                    case BATTLESHIP -> Color.yellow;
                    case CRUISER -> Color.green;
                    case SUBMARINE -> Color.cyan;
                    case DESTROYER -> Color.pink;
                };
                g.setColor(color);

                int breadth = 1;
                int length = s.length;
                int arc = 20;

                breadth *= SQR_SIZE;
                length *= SQR_SIZE;

                switch (s.rotation) {
                    case VERTICAL -> g.fillRoundRect(s.x, s.y, breadth, length, arc, arc);
                    case HORIZONTAL -> g.fillRoundRect(s.x, s.y, length, breadth, arc, arc);
                }
            }
        }
    }
    public void paintPegs(Graphics g) {
        for (Peg p : pe) {
            int index = switch (p.click) {
                case MISS -> 0;
                case HIT -> 1;
            };
            g.drawImage(images[index], p.x, p.y, this);
        }
    }
    public static Ship getShip(int x, int y) {
        int sX = x / SQR_SIZE;
        int sY = y / SQR_SIZE;
        for (Ship s : sh) {
            switch (s.rotation) {
                case VERTICAL -> { if(sY >= s.sY && sY <= s.sY + (s.length - 1) && sX == s.sX) return s; }
                case HORIZONTAL -> { if(sX >= s.sX && sX <= s.sX + (s.length - 1) && sY == s.sY) return s; }
            }
        }
        return null;
    }
    public boolean button(int x, int y) {
        if (x >= buttonX && x <= (buttonX + buttonWidth) && y >= buttonY && y <= (buttonY + buttonHeight)){
            if (x >= buttonX + buttonWidth / 2) game.difficulty = Game.Difficulty.HARD;
            else game.difficulty = Game.Difficulty.EASY;
            return true;
        }
        return false;
    }
    public void actionPerformed(ActionEvent e) {}
    public void mousePressed(MouseEvent e) {
        game.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        game.mouseReleased(e);
    }

    public void mouseClicked(MouseEvent e) {
        game.mouseClicked(e);
    }

    public void mouseExited(MouseEvent e) {
        game.mouseExited();
    }

    public void mouseEntered(MouseEvent e) {
        game.mouseEntered();
    }

    public void mouseDragged(MouseEvent e) {
        game.mouseDragged(e);
    }

    public void mouseMoved(MouseEvent e) {
        game.mouseMoved();
    }
}
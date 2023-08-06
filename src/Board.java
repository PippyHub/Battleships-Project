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
    int placed = 0;
    public Board() {
        images = img.loadImages();
        game = new Game(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        player();
        enemy();
    }
    public void player() {
        shipList(11, 1, Ship.Name.CARRIER, Ship.Player.PLAYER);
        shipList(13, 1, Ship.Name.BATTLESHIP, Ship.Player.PLAYER);
        shipList(15, 1, Ship.Name.CRUISER, Ship.Player.PLAYER);
        shipList(17, 1, Ship.Name.SUBMARINE, Ship.Player.PLAYER);
        shipList(19, 1, Ship.Name.DESTROYER, Ship.Player.PLAYER);
    }
    public void enemy() {
        shipList(1, 1, Ship.Name.CARRIER, Ship.Player.ENEMY);
        shipList(3, 1, Ship.Name.BATTLESHIP, Ship.Player.ENEMY);
        shipList(5, 1, Ship.Name.CRUISER, Ship.Player.ENEMY);
        shipList(7, 1, Ship.Name.SUBMARINE, Ship.Player.ENEMY);
        shipList(9, 1, Ship.Name.DESTROYER, Ship.Player.ENEMY);

        while (placed != 5) {
            placed = 0;
            for (Ship s : sh) {
                if (s.player == Ship.Player.ENEMY) {
                    int sX = (int) (Math.random() * 10) + 1;
                    int sY = (int) (Math.random() * 10) + 1;
                    sX += 10;
                    if(s.enemyPlace(sX, sY)) placed++;
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
        drawButton(g);
        drawGrid(g, SQR_AMOUNT, BOARD_SIZE);
        paintShips(g);
        paintPegs(g);
    }
    public void drawButton(Graphics g){
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        g.fillRect(x, y, width, height);
    }
    public void drawGrid(Graphics g, int gridSize, int size) {
        g.setColor(Color.BLACK);
        int cellSize = size / gridSize;
        for (int x = 0; x <= size; x += cellSize) {
            g.drawLine(x, 0, x, size);
        }
        for (int y = 0; y <= size; y += cellSize) {
            g.drawLine(0, y, size, y);
        }
    }
    public void paintShips(Graphics g) {
        for (Ship s : sh) {
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
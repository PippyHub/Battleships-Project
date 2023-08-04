import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    static final int SQR_SIZE = 64;
    static final int SQR_AMOUNT = 10;
    static final int BOARD_SIZE = SQR_SIZE * SQR_AMOUNT;
    static final int SHIP_AREA_SIZE = BOARD_SIZE;
    static LinkedList<Peg> pe = new LinkedList<>();
    static LinkedList<Ship> sh = new LinkedList<>();
    Images img = new Images();
    private final Image[] images;
    public static Ship selectedShip = null;
    public Board() {
        shipList(0, 0, Ship.Name.BATTLESHIP);
        shipList(0, 1, Ship.Name.CARRIER);

        images = img.loadImages();
        addMouseListener(this);
    }
    public void pegList(int sX, int sY) {
        new Peg(sX, sY, pe);
    }
    public void shipList(int sX, int sY, Ship.Name name) {
        new Ship(sX, sY, name, sh);
    }
    public void paint(Graphics g) {
        g.setColor(new Color(86, 86, 86));
        g.fillRect(0, 0, BOARD_SIZE + SHIP_AREA_SIZE, BOARD_SIZE);
        drawGrid(g, SQR_AMOUNT, BOARD_SIZE);
        paintShips(g);
        paintPegs(g);
    }
    public void drawGrid(Graphics g, int gridSize, int size) {
        int cellSize = size / gridSize;
        for (int x = 0; x <= size; x += cellSize) {
            g.setColor(Color.BLACK);
            g.drawLine(x, 0, x, size);
        }
        for (int y = 0; y <= size; y += cellSize) {
            g.setColor(Color.BLACK);
            g.drawLine(0, y, size, y);
        }
    }
    public void paintShips(Graphics g) {
        for (Ship s : sh) {
            Color color = switch (s.name) {
                case CARRIER -> Color.blue;
                case BATTLESHIP -> Color.green;
                case CRUISER, SUBMARINE -> Color.pink;
                case DESTROYER -> Color.orange;
            };
            g.setColor(color);

            int breadth = 1;
            int length = s.length;
            int arc = 20;

            switch (s.click) {
                case DESELECTED -> {
                    breadth *= SQR_SIZE;
                    length *= SQR_SIZE;
                }
                case SELECTED, PLACED -> {
                    breadth *= SQR_SIZE + 1;
                    length *= SQR_SIZE;
                }
            };

            switch (s.rotation) {
                case VERTICAL -> g.fillRoundRect(s.x, s.y, breadth, length, arc, arc);
                case HORIZONTAL -> g.fillRoundRect(s.x, s.y, length, breadth, arc, arc);
            }
        }
    }
    public void paintPegs(Graphics g) {
        for (Peg s : pe) {
            int index = switch (s.click) {
                case MISS -> 0;
                case HIT -> 1;
            };
            g.drawImage(images[index], s.x, s.y, this);
        }
    }
    public static Ship getShip(int x, int y) {
        int sX = x / SQR_SIZE;
        int sY = y / SQR_SIZE;
        for (Ship s : sh) {
            switch (s.rotation) {
                case VERTICAL -> { if(sY >= s.sY && sY <= s.sY + s.length && sX == s.sX) return s; }
                case HORIZONTAL -> { if(sX >= s.sX && sX <= s.sX + s.length && sY == s.sY) return s; }
            }
        }
        return null;
    }
    public void actionPerformed(ActionEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {
        selectedShip = getShip(e.getX(), e.getY());
        if (selectedShip != null) {
            if (SwingUtilities.isRightMouseButton(e)) {
                selectedShip.rotation = switch (selectedShip.rotation) {
                    case VERTICAL -> Ship.Rotation.HORIZONTAL;
                    case HORIZONTAL -> Ship.Rotation.VERTICAL;
                };
            }
        }
        repaint();
    }
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
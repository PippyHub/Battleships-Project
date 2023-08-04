import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    static final int SQR_SIZE = 64;
    static final int SQR_AMOUNT = 10;
    static final int BOARD_SIZE = SQR_SIZE * SQR_AMOUNT;
    static final int SHIP_AREA_SIZE = BOARD_SIZE;
    LinkedList<Square> sq = new LinkedList<>();
    LinkedList<Ship> sh = new LinkedList<>();
    Images img = new Images();
    private final Image[] images;

    public Board() {
        shipList(0, 0, Ship.Name.BATTLESHIP);
        shipList(0, 1, Ship.Name.CARRIER);

        images = img.loadImages();
        addMouseListener(this);

        for (int y = 0; y < SQR_AMOUNT; y++) {
            for (int x = 0; x < SQR_AMOUNT; x++) {
                squareList(x, y);
            }
        }
    }
    public void squareList(int sX, int sY) {
        new Square(sX, sY, sq);
    }
    public void shipList(int sX, int sY, Ship.Name name) {
        new Ship(sX, sY, name, sh);
    }
    public void paint(Graphics g) {
        g.setColor(new Color(86, 86, 86));
        g.fillRect(BOARD_SIZE, 0, SHIP_AREA_SIZE, BOARD_SIZE);
        for (Square s : sq) {
            int index = switch (s.state) {
                case HIT -> 0;
                case MISS -> 1;
                case EMPTY -> 2;
            };
            g.drawImage(images[index], s.x, s.y, this);
        }
        for (Ship s : sh) {
            Color color = switch (s.name) {
                case CARRIER -> Color.blue;
                case BATTLESHIP -> Color.red;
                case CRUISER, SUBMARINE -> Color.pink;
                case DESTROYER -> Color.orange;
            }; //Use later for images
            g.setColor(color);

            int breadth = 1;
            int length = switch (s.name) {
                case CARRIER -> 5;
                case BATTLESHIP -> 4;
                case CRUISER, SUBMARINE -> 3;
                case DESTROYER -> 2;
            };
            switch (s.click) {
                case DESELECTED -> {
                    breadth *= SQR_SIZE;
                    length *= SQR_SIZE;
                }
                case SELECTED, PLACED -> {
                    breadth *= SQR_SIZE + 10;
                    length *= SQR_SIZE + 10;
                }
            };
            switch (s.rotation) {
                case VERTICAL -> g.fillRect(s.x, s.y, breadth, length);
                case HORIZONTAL -> g.fillRect(s.x, s.y, length, breadth);
            }
        }
    }
    public void actionPerformed(ActionEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
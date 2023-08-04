import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
public class Board extends JPanel implements ActionListener, MouseListener {
    static final int SQR_SIZE = 64;
    static final int SQR_AMOUNT = 8;
    static final int BOARD_SIZE = SQR_SIZE * SQR_AMOUNT;
    static final int SHIP_AREA_SIZE = 256;
    LinkedList<Square> sq = new LinkedList<>();
    LinkedList<Ship> sh = new LinkedList<>();
    Images img = new Images();
    private final Image[] images;

    public Board() {
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
        for (Square s : sq) {
            int index = switch (s.state) {
                case HIT -> 0;
                case MISS -> 1;
                case EMPTY -> 2;
            };
            g.drawImage(images[index], s.x, s.y, this);
        }
        g.setColor(new Color(86, 86, 86));
        g.fillRect(0, BOARD_SIZE, BOARD_SIZE, SHIP_AREA_SIZE);
    }
    public void actionPerformed(ActionEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
}
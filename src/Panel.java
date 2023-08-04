import javax.swing.*;
import java.awt.*;
public class Panel extends JFrame {
    static final int BOARD_SIZE = Board.SQR_SIZE * Board.SQR_AMOUNT;
    static final int SHIP_AREA_SIZE = Board.SHIP_AREA_SIZE;
    public static Board panel = new Board();

    public Panel() {
        setTitle("Battleship");
        this.getContentPane().setPreferredSize(new Dimension(BOARD_SIZE + SHIP_AREA_SIZE, BOARD_SIZE));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.add(panel);

        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
}
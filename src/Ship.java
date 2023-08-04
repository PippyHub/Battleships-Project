import java.util.LinkedList;
public class Ship {
    public enum Name {
        CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, sX, sY;
    LinkedList<Ship> sh;
    Name name;
    public Ship(int sX, int sY, Name name, LinkedList<Ship> sh) {
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
        this.name = name;
        this.sh = sh;
        sh.add(this);
    }
}

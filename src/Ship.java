import java.util.LinkedList;
public class Ship {
    public enum Name {
        CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER
    }
    public enum Click {
        DESELECTED, SELECTED, PLACED
    }
    public enum Rotation {
        VERTICAL, HORIZONTAL
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, sX, sY;
    LinkedList<Ship> sh;
    Name name;
    Click click = Click.DESELECTED;
    Rotation rotation = Rotation.HORIZONTAL;
    int length;
    public Ship(int sX, int sY, Name name, LinkedList<Ship> sh) {
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
        this.name = name;
        this.sh = sh;
        sh.add(this);

        length = switch (name) {
            case CARRIER -> 5;
            case BATTLESHIP -> 4;
            case CRUISER, SUBMARINE -> 3;
            case DESTROYER -> 2;
        };
    }
}

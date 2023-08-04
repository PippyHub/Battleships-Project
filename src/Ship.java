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
    public enum Player {
        PLAYER, ENEMY
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, sX, sY, startX, startY;
    LinkedList<Ship> sh;
    Name name;
    Click click = Click.DESELECTED;
    Rotation rotation = Rotation.VERTICAL;
    int length;
    public Ship(int sX, int sY, Name name, LinkedList<Ship> sh) {
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
        this.startX = sX;
        this.startY = sY;
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
    public void rotate(){
        rotation = switch (rotation) {
            case VERTICAL -> Ship.Rotation.HORIZONTAL;
            case HORIZONTAL -> Ship.Rotation.VERTICAL;
        };
    }
    public void move(int sX, int sY){
        click = Click.SELECTED;
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
    }
    public void canPlace(int sX, int sY){
       if (bounds(sX, sY) || overlap(sX, sY)) {
           click = Click.DESELECTED;
       } else {
           click = Click.PLACED;
       }
       reset();
    }
    public void reset() {
        if (click == Click.DESELECTED) {
            rotation = Rotation.VERTICAL;
            this.x = startX * SQR_SIZE;
            this.y = startY * SQR_SIZE;
            this.sX = startX;
            this.sY = startY;
        }
    }
    public boolean bounds(int sX, int sY) {
        return switch (rotation) {
            case VERTICAL -> sX > 9 || sY + (length - 1) > 9;
            case HORIZONTAL -> sX + (length - 1) > 9 || sY > 9;
        };
    }
    public boolean overlap(int sX, int sY) {
        for (Ship s : sh) {
            if (s != this) {
                int squareX = 0;
                int squareY = 0;
                switch (rotation) {
                    case VERTICAL -> squareY += (length - 1);
                    case HORIZONTAL -> squareX += (length - 1);
                }
                while (squareX != 0 || squareY != 0) {
                    if (Board.getShip(sX + squareX * SQR_SIZE, sY + squareY * SQR_SIZE) != null) {
                        System.out.println(true);
                        return true;
                    }
                    switch (rotation) {
                        case VERTICAL -> squareY --;
                        case HORIZONTAL -> squareX --;
                    }
                }
            }
        }
        return false;
    }
}

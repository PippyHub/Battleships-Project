import java.util.LinkedList;
public class Square {
    public enum State {
        EMPTY, HIT, MISS
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, sX, sY;
    LinkedList<Square> ps;
    State state = State.EMPTY;
    public Square(int sX, int sY, LinkedList<Square> ps) {
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
        this.ps = ps;
        ps.add(this);
    }
}

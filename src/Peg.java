import java.util.LinkedList;
public class Peg {
    public enum Click {
        MISS, HIT
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, sX, sY;
    LinkedList<Peg> pe;
    Click click;
    public Peg(int sX, int sY, Click click, LinkedList<Peg> pe) {
        this.x = sX * SQR_SIZE;
        this.y = sY * SQR_SIZE;
        this.sX = sX;
        this.sY = sY;
        this.click = click;
        this.pe = pe;
        pe.add(this);
    }
}

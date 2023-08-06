import java.util.LinkedList;
public class Peg {
    public enum Click {
        MISS, HIT
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    int x, y, pX, pY;
    LinkedList<Peg> pe;
    Click click;
    public Peg(int pX, int pY, Click click, LinkedList<Peg> pe) {
        this.x = pX * SQR_SIZE;
        this.y = pY * SQR_SIZE;
        this.pX = pX;
        this.pY = pY;
        this.click = click;
        this.pe = pe;
        pe.add(this);
    }
}

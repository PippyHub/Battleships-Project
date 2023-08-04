import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Images {
    private final Image[] images;
    public Images() {
        images = new Image[3];
    }
    public Image[] loadImages() {
        try {
            BufferedImage all = ImageIO.read(new File("src/Images/BattleShip Sprites.png"));
            int index = 0;
            for (int x = 0; x < 96; x += 32) {
                images[index] = all.getSubimage(x, 0, 32, 32)
                        .getScaledInstance(Board.SQR_SIZE, Board.SQR_SIZE, BufferedImage.SCALE_SMOOTH);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
}
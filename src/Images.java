import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Images {
    final int IMAGE_WIDTH = 128;
   final int SUB_IMAGE_SIZE = 64;
    private final Image[] images;
    public Images() {
        images = new Image[3];
    }
    public Image[] loadImages() {
        try {
            BufferedImage all = ImageIO.read(new File("src/Images/pegs.png"));
            int index = 0;
            for (int x = 0; x < IMAGE_WIDTH; x += SUB_IMAGE_SIZE) {
                images[index] = all.getSubimage(x, 0, SUB_IMAGE_SIZE, SUB_IMAGE_SIZE)
                        .getScaledInstance(Board.SQR_SIZE, Board.SQR_SIZE, BufferedImage.SCALE_SMOOTH);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
}
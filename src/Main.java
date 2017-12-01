import constants.ImageComparatorConstants;
import utils.ImageComparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Alexander Manko, alexander.manko95@gmail.com
 * @since 01.12.17
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String firstImagePath = "";
        String secondImagePath = "";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--first":
                    if (i + 1 < args.length) {
                        firstImagePath = args[i + 1];
                    }
                    break;
                case "--second":
                    if (i + 1 < args.length) {
                        secondImagePath = args[i + 1];
                    }
                    break;
            }
        }

        File firstImageFile = new File(firstImagePath);
        File secondImageFile = new File(secondImagePath);

        BufferedImage firstImage = ImageIO.read(firstImageFile);
        BufferedImage secondImage = ImageIO.read(secondImageFile);

        int radius = ImageComparatorConstants.RADIUS;
        double sensibility = ImageComparatorConstants.SENSIBILITY;
        ImageComparator imageComparator = new ImageComparator(firstImage, secondImage, radius, sensibility);

        BufferedImage diffImage = imageComparator.getDiffImage();
        ImageIO.write(diffImage, "png", new File("./diff.png"));
        System.out.println("OK! Just look for diff.png in the current directory.");
    }
}

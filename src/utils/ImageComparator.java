package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm for finding differences between two images.
 *
 * @author Alexander Manko, alexander.manko95@gmail.com
 * @since 01.12.17
 */
public class ImageComparator {

    private int[][] diffMatrix;

    private BufferedImage firstImage;
    private BufferedImage secondImage;
    private BufferedImage diffBufferedImage;
    private int radius;
    private double sensibility;
    private int width;
    private int height;

    private int minRectX;
    private int minRectY;
    private int maxRectX;
    private int maxRectY;

    public ImageComparator(BufferedImage firstImage, BufferedImage secondImage, int radius, double sensibility) {
        this.firstImage = firstImage;
        this.secondImage = secondImage;
        this.radius = radius;
        this.sensibility = sensibility;
        this.width = this.firstImage.getWidth();
        this.height = this.firstImage.getHeight();
        fillDiffMatrix();
    }

    /**
     * Recursive searching maximum width and height of point's area.
     * If current point is 1(distinction) then, first of all, we need to
     * change current value to something like -1. It's need for exclude ability get here again.
     * After that we need to go down and start another one level of recursion.
     * When recursion finished we need to go to the right, and so on.
     * Also we compare current coordinates with old one on each recursion level.
     * After all we get maximal and minimal coordinates of rectangle corners.
     *
     * @param x - current x position in diffMatrix.
     * @param y - current x position in diffMatrix.
     */
    private void recursive(int x, int y) {
        if (diffMatrix[y][x] == 1) {
            diffMatrix[y][x] = -1;

            if (minRectX > x) { minRectX = x; }
            if (minRectY > y) { minRectY = y; }
            if (maxRectX < x) { maxRectX = x; }
            if (maxRectY < y) { maxRectY = y; }

            if ((diffMatrix.length > (y + 1)) && (diffMatrix[y + 1][x] == 1)) {
                recursive(x, y + 1);
            }
            if ((diffMatrix[0].length > (x + 1)) && (diffMatrix[y][x + 1] == 1)) {
                recursive(x + 1, y);
            }
            if (((x - 1) >= 0) && (diffMatrix[y][x - 1] == 1)) {
                recursive(x - 1, y);
            }
            if (((y - 1) >= 0) && (diffMatrix[y - 1][x] == 1)) {
                recursive(x, y - 1);
            }
        }
    }

    /**
     * Current method is searching differences between two images.
     * First of all it calculates distance between two pixels
     * in corresponding positions for each image. In case of pixels is different
     * we write down to diffMatrix 1 otherwise 0. Also there is we using 'sensibility' for
     * decide which difference actually for us. Also I set a few point around every difference.
     * It helps me get rid of spaces between points.
     */
    private void fillDiffMatrix() {
        diffMatrix = new int[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int firstImageRGB = firstImage.getRGB(x, y);
                int secondImageRGB = secondImage.getRGB(x, y);

                Color firstImageColor = new Color(firstImageRGB);
                Color secondImageColor = new Color(secondImageRGB);

                int firstRed = firstImageColor.getRed();
                int firstGreen = firstImageColor.getGreen();
                int firstBlue = firstImageColor.getBlue();

                int secondRed = secondImageColor.getRed();
                int secondGreen = secondImageColor.getGreen();
                int secondBlue = secondImageColor.getBlue();

                double distance = Math.sqrt(Math.pow(firstRed - secondRed, 2) +
                        Math.pow(firstGreen - secondGreen, 2) +
                        Math.pow(firstBlue - secondBlue, 2));
                double maxDistance = Math.sqrt(Math.pow(255, 2) * 3);
                double percent = distance / maxDistance;

                if (percent > sensibility) {
                    int from = -1 * radius;
                    int to = radius;

                    for (int yDif = from; yDif < to; yDif++) {
                        for (int xDif = from; xDif < to; xDif++) {
                            if (((y + yDif) >= 0) && ((y + yDif) < height) &&
                                    ((x + xDif) >= 0) && ((x + xDif) < width)) {
                                diffMatrix[y + yDif][x + xDif] = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method just runs the 'recursive' method in case of we have faced with difference.
     * After the recursion has finished we getting rectangle around area of differences.
     *
     * @return List of rectangles. Each of them wrap area of differences.
     */
    private List<Rectangle> getDiffRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (diffMatrix[y][x] == 1) {
                    minRectX = x;
                    maxRectY = y;
                    maxRectX = x;
                    minRectY = y;
                    recursive(x, y);
                    int rectangleWidth = maxRectY - minRectY;
                    int rectangleHeight = maxRectX - minRectX;
                    rectangles.add(new Rectangle(minRectX, minRectY, rectangleWidth, rectangleHeight));
                }
            }
        }

        return rectangles;
    }


    /**
     * This method get second image and clone to new one. Then it adds rectangles to the image.
     *
     * @return image with added rectangles in places they were different.
     */
    public BufferedImage getDiffImage() {
        if (diffBufferedImage == null) {
            List<Rectangle> rectangles = getDiffRectangles();
            int imageWidth = secondImage.getWidth();
            int imageHeight = secondImage.getHeight();
            int imageType = secondImage.getType();

            diffBufferedImage = new BufferedImage(imageWidth, imageHeight, imageType);
            diffBufferedImage.setData(secondImage.getData());

            for (Rectangle rectangle : rectangles) {
                Graphics graphics = diffBufferedImage.createGraphics();
                int rectX = Double.valueOf(rectangle.getX()).intValue();
                int rectY = Double.valueOf(rectangle.getY()).intValue();
                int rectWidth = Double.valueOf(rectangle.getHeight()).intValue();
                int rectHeight = Double.valueOf(rectangle.getWidth()).intValue();

                graphics.setColor(Color.RED);
                graphics.drawRoundRect(rectX, rectY, rectWidth, rectHeight, 0, 15);
                graphics.dispose();
            }
        }

        return diffBufferedImage;
    }

    public int[][] getDiffMatrix() {
        return diffMatrix;
    }
}
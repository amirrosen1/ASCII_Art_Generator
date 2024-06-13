package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
public class Image {

    private final Color[][] pixelArray;
    private final int width;
    private final int height;

    /**
     * Constructor to create an object of Image class from a file.
     *
     * @param filename String of the file name
     * @throws IOException if the file is not found or cannot be read
     */
    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();


        pixelArray = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArray[i][j] = new Color(im.getRGB(j, i));
            }
        }
    }

    /**
     * Constructor to create an object of Image class from a Color[][].
     * @param pixelArray Color[][] of the pixels
     * @param width      int of the width
     * @param height     int of the height
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }


    /**
     * Returns the width of the image.
     * @return int of the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image.
     * @return int of the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the pixel at the specified coordinates.
     * @param x int of the x coordinate
     * @param y int of the y coordinate
     * @return Color of the pixel
     */
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }


    /**
     * This method saves the image to a file.
     * @param fileName String of the file name
     */
    public void saveImage(String fileName) {
        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < pixelArray.length; x++) {
            for (int y = 0; y < pixelArray[x].length; y++) {
                bufferedImage.setRGB(y, x, pixelArray[x][y].getRGB());
            }
        }
        File outputfile = new File(fileName + ".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method pads the image to the next power of 2 dimensions.
     * @return Image object of the padded image
     */
    public Image padImageToPowerOf2Dimensions() {
        int originalWidth = width;
        int originalHeight = height;

        int newWidth = calculateNextPowerOf2(originalWidth);
        int newHeight = calculateNextPowerOf2(originalHeight);

        int offsetX = (newWidth - originalWidth) / 2;
        int offsetY = (newHeight - originalHeight) / 2;

        Color[][] paddedImage = new Color[newHeight][newWidth];


        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                paddedImage[y][x] = Color.WHITE; // Use white for padding
            }
        }
        // Copy original image to the center of the new image
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                paddedImage[y + offsetY][x + offsetX] = pixelArray[y][x];
            }
        }
        return new Image(paddedImage, newWidth, newHeight);
    }

    /**
     * This method checks if the given dimension is a power of 2.
     * @param dimension int of the dimension
     * @return boolean true if the dimension is a power of 2, false otherwise
     */
    private static boolean isPowerOf2(int dimension) {
        double logBase2 = Math.log(dimension) / Math.log(2);
        return Math.ceil(logBase2) == Math.floor(logBase2);
    }

    /**
     * This method calculates the next power of 2 for the given dimension.
     * @param dimension int of the dimension
     * @return int of the next power of 2
     */
    private static int calculateNextPowerOf2(int dimension) {
        if (isPowerOf2(dimension)) {
            return dimension; // No padding needed
        }
        double logBase2 = Math.log(dimension) / Math.log(2);
        int power = (int) Math.ceil(logBase2);
        return (int) Math.pow(2, power);
    }

    /**
     * This method divides the image into sub-images.
     * @param subImagesPerRow int of the number of sub-images per row
     * @return ArrayList of Image objects containing the sub-images
     */
    public ArrayList<Image> divideImageIntoSubImages(int subImagesPerRow) {
        int subImageSize = width / subImagesPerRow;
        ArrayList<Image> subImages = new ArrayList<>();
        for (int row = 0; row < width; row += subImageSize) {
            for (int col = 0; col < height; col += subImageSize) {

                Color[][] subImagePixels = new Color[subImageSize][subImageSize];
                for (int subRow = 0; subRow < subImageSize; subRow++) {
                    System.arraycopy(pixelArray[row + subRow], col, subImagePixels[subRow], 0,
                            subImageSize);
                }
                subImages.add(new Image(subImagePixels, subImageSize, subImageSize));
            }
        }
        return subImages;
    }

    /**
     * This method calculates the average brightness of the image.
     * @return double of the average brightness
     */
    public double calculateAverageBrightnessOfImage() {
        double totalBrightness = 0;
        int pixelCount = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color colorPixel = pixelArray[i][j];
                // Convert to grayscale using the luminosity method
                double greyPixel = colorPixel.getRed() * 0.2126 + colorPixel.getGreen() * 0.7152 +
                        colorPixel.getBlue() * 0.0722;
                totalBrightness += greyPixel;
                pixelCount++;
            }
        }
        return (totalBrightness / pixelCount) / 255.0;
    }
}

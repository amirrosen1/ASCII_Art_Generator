package ascii_art;


import image.Image;
import image_char_matching.SubImgCharMatcher;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * AsciiArtAlgorithm class is responsible for creating the ASCII art of an image.
 */
public class AsciiArtAlgorithm {
    private Image image;
    private Image paddedImage;
    private int resolution;
    private boolean aChangeInCharsWasMade = false;
    private boolean aChangeOfImageWasMade = true;
    private boolean aChangeOfResolutionWasMade = false;
    private char[][] currentAsciiArt;
    private final SubImgCharMatcher subImgCharMatcher;
    private ArrayList<Double> subImageBrightnessesList = new ArrayList<>();

    /**
     * Constructor to create an object of AsciiArtAlgorithm class.
     * @param image Image object
     * @param resolution int
     * @param charset char[]
     */
    public AsciiArtAlgorithm(Image image, int resolution, char[] charset) {
        this.image = image;
        this.resolution = resolution;
        paddedImage = image.padImageToPowerOf2Dimensions();
        subImgCharMatcher = new SubImgCharMatcher(charset);
        run();
    }


    /**
     * This method is called to create the ASCII art from the image.
     * It checks if any change was made to the image, resolution or charset and
     * updates the ASCII art accordingly.
     * @return the ASCII art
     */
    public char[][] run() {
        if (aChangeOfImageWasMade) {
            paddedImage = image.padImageToPowerOf2Dimensions();
            currentAsciiArt = createNewImage(paddedImage);
            aChangeOfImageWasMade = false;
        }
        if (aChangeInCharsWasMade) {
            currentAsciiArt = matchNewBrightnesses();
            aChangeInCharsWasMade = false;
        }
        if (aChangeOfResolutionWasMade) {
            currentAsciiArt = createNewImage(paddedImage);
            aChangeOfResolutionWasMade = false;
        }

        return currentAsciiArt;
    }


    /**
     * This method creates an ASCII art from a new image. it is called when a new image is set
     * or when the resolution is changed.
     * @param paddedImage Image object that is padded to power of 2 dimensions
     * @return char[][] new ASCII art
     */

    private char[][] createNewImage(Image paddedImage) {
        ArrayList<Image> subImages = paddedImage.divideImageIntoSubImages(resolution);
        int rows = subImages.size() / resolution;
        int cols = resolution;
        char[][] asciiArt = new char[rows][cols];
        ArrayList<Double> newSubImageBrightnessesList = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Image subImage = subImages.get(i * cols + j);
                double averageBrightness = subImage.calculateAverageBrightnessOfImage();
                newSubImageBrightnessesList.add(averageBrightness);
                char bestCharMatch = subImgCharMatcher.getCharByImageBrightness(averageBrightness);
                asciiArt[i][j] = bestCharMatch;
            }
        }
        this.subImageBrightnessesList = newSubImageBrightnessesList;
        return asciiArt;
    }

    /**
     * This method matches the new brightnesses of the sub images to the charset. it is called
     * when a change is made to the charset.
     * @return char[][] new ASCII art
     */
    private char[][] matchNewBrightnesses() {
        int rows = subImageBrightnessesList.size() / resolution;
        int cols = resolution;
        char[][] asciiArt = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double averageBrightness = subImageBrightnessesList.get(i * cols + j);
                char bestCharMatch = subImgCharMatcher.getCharByImageBrightness(averageBrightness);
                asciiArt[i][j] = bestCharMatch;
            }
        }
        return asciiArt;
    }


    /**
     * This method adds a character to the charset.
     * @param c the char to add
     */
    public void addChar(char c) {
        subImgCharMatcher.addChar(c);
    }

    /**
     * This method removes a character from the charset.
     * @param c the char to remove
     */
    public void removeChar(char c) {
        subImgCharMatcher.removeChar(c);
    }

    /**
     * This method sets a new image to the object.
     * @param newImage Image object to set as the new image
     */
    public void setImage(Image newImage) {
        this.image = newImage;
    }

    /**
     * This method sets a new resolution to the object.
     * @param newResolution int to set as the new resolution
     */
    public void setResolution(int newResolution) {
        this.resolution = newResolution;
    }

    /**
     * This method returns the current resolution.
     * @return int current resolution
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * This method returns the width of the created image.
     * @return int width of the image
     */
    public int getImageWidth() {
        return paddedImage.getWidth();
    }

    /**
     * This method returns the height of the created image.
     * @return int height of the image
     */
    public int getImageHeight() {
        return paddedImage.getHeight();
    }

    /**
     * This method prints the current chars in the charset.
     */
    public void printCurrentChars() {
        TreeSet<Character> currentChars = subImgCharMatcher.getCurrentCharsInMap();
        for (char c : currentChars) {
            System.out.print(c + " ");
        }
        System.out.println();
    }


    /**
     * This method checks if the charset contains a character.
     * @param c the char to check
     * @return boolean true if the charset contains the char, false otherwise
     */
    public boolean containsChar(char c) {
        TreeSet<Character> currentChars = subImgCharMatcher.getCurrentCharsInMap();
        return currentChars.contains(c);
    }

    /**
     * This method checks if the current charset is empty.
     * @return boolean true if the current chars in the charset is empty, false otherwise
     */
    public boolean isCurrentCharsMapEmpty() {
        return subImgCharMatcher.getCurrentCharsInMap().isEmpty();
    }


    /**
     * This method sets the boolean aChangeInCharsWasMade parameter,
     * which is used to check if a change in charset was made.
     */
    public void setAChangeInCharsWasMade(boolean aChangeInCharsWasMade) {
        this.aChangeInCharsWasMade = aChangeInCharsWasMade;
    }

    /**
     * This method sets the boolean aChangeOfImageWasMade parameter,
     * which is used to check if a change of image was made.
     */
    public void setAChangeOfImageWasMade(boolean aChangeOfImageWasMade) {
        this.aChangeOfImageWasMade = aChangeOfImageWasMade;
    }

    /**
     * This method sets the boolean aChangeOfResolutionWasMade parameter,
     * which is used to check if a change of resolution was made.
     */
    public void setAChangeOfResolutionWasMade(boolean aChangeOfResolutionWasMade) {
        this.aChangeOfResolutionWasMade = aChangeOfResolutionWasMade;
    }
}

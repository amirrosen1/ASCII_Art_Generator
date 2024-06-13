package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.io.IOException;

/**
 * The Shell class is the main class of the program. It is responsible for the user interface and
 * for the communication between the user and the rest of the program.
 */
public class Shell {

    private AsciiArtAlgorithm asciiArtAlgorithm;
    private AsciiOutput asciiOutput;
    private static final String TERMINATION_STRING = "exit";
    private static final String DEFAULT_INPUT_IMAGE_PATH = "cat.jpeg";
    private static final String DEFAULT_OUTPUT_IMAGE_PATH = "out.html";
    private static final char SPACE_CHAR = ' ';
    private static final String DEFAULT_FONT = "Courier New";
    private static final String USER_INPUT = ">>> ";
    private static final String DISPLAY_CHARS = "chars";
    private static final String ADD_CHARS = "add";
    private static final String REMOVE_CHARS = "remove";
    private static final String RESOLUTION = "res";
    private static final String IMAGE = "image";
    private static final String OUTPUT = "output";
    private static final String ASCII_ART = "asciiArt";
    private static final String CONSOLE = "console";
    private static final String HTML = "html";
    private static final String ALL_CHARS = "all";
    private static final String SPACE = "space";
    private static final String DASH = "-";
    private static final String UP_COMMAND = "up";
    private static final String DOWN_COMMAND = "down";
    private static final String CHANGE_RESOLUTION_MESSAGE = "Resolution set to %d.";
    private static final String ADD_ERROR_MESSAGE = "Did not add due to incorrect format.";
    private static final String REMOVE_ERROR_MESSAGE = "Did not remove due to incorrect format.";
    private static final String IMAGE_FILE_ERROR_MESSAGE = "Did not execute due to problem with image file.";
    private static final String INVALID_COMMAND_MESSAGE = "Did not execute due to incorrect command.";
    private static final String EMPTY_CHARSET_MESSAGE = "Did not execute. Charset is empty.";
    private static final String INCORRECT_FORMAT_MESSAGE = "Did not change output method due to incorrect" +
            " format.";
    private static final String IMAGE_RESOLUTION_ERROR_MESSAGE = "Did not change resolution due to " +
            "exceeding boundaries.";
    private static final String INCORRECT_RESOLUTION_FORMAT_MESSAGE = "Did not change resolution due to" +
            " incorrect format.";
    private static final String EXCEEDING_BOUNDARIES_MESSAGE = "Did not change resolution due to exceeding" +
            " boundaries.";
    private final int DEFAULT_RESOLUSION = 128;
    private final char[] defaultCharSet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    /**
     * The run method is the main method of the program. It is responsible for the user interface and
     * for the communication between the user and the rest of the program.
     */
    public void run() {
        try {
            Image defaultImage = new Image(DEFAULT_INPUT_IMAGE_PATH);
            asciiArtAlgorithm = new AsciiArtAlgorithm(defaultImage, DEFAULT_RESOLUSION, defaultCharSet);
            asciiOutput = new ConsoleAsciiOutput();
        } catch (IOException e) {
            System.out.println(IMAGE_FILE_ERROR_MESSAGE);
            return;
        }
        System.out.print(USER_INPUT);
        String input = KeyboardInput.readLine();
        while (!input.equals(TERMINATION_STRING)) {
            try {
                if (input.equals(DISPLAY_CHARS)) {
                    asciiArtAlgorithm.printCurrentChars();

                } else if (input.startsWith(ADD_CHARS) && input.charAt(3) == SPACE_CHAR) {
                    String charToAdd = input.substring(4);
                    addChar(charToAdd);

                } else if (input.startsWith(REMOVE_CHARS)) {
                    removeChar(input.substring(REMOVE_CHARS.length()).trim());

                } else if (input.startsWith(RESOLUTION)) {

                    String newResolution = input.substring(RESOLUTION.length()).trim();
                    changeResolution(newResolution);
                } else if (input.startsWith(IMAGE)) {

                    setNewImage(input.substring(IMAGE.length()).trim());
                } else if (input.startsWith(OUTPUT)) {

                    changeOutputLocation(input.substring(OUTPUT.length()).trim());
                } else if (input.startsWith(ASCII_ART)) {

                    runAlgorithm();
                } else {
                    throw new IllegalArgumentException(INVALID_COMMAND_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                asciiArtAlgorithm.setAChangeOfImageWasMade(false);
                System.out.println(e.getMessage());
            }
            System.out.print(USER_INPUT);
            input = KeyboardInput.readLine();
        }
    }


    /**
     * The runAlgorithm method is responsible for running the algorithm and printing the result.
     *
     * @throws IllegalArgumentException if the charset is empty.
     */
    private void runAlgorithm() throws IllegalArgumentException {
        if (asciiArtAlgorithm.isCurrentCharsMapEmpty()) {
            throw new IllegalArgumentException(EMPTY_CHARSET_MESSAGE);
        } else {
            char[][] asciiArt = asciiArtAlgorithm.run();
            asciiOutput.out(asciiArt);
        }
    }

    /**
     * The changeOutputLocation method is responsible for changing the output location of the program.
     *
     * @param outputLocation the new output location - console/html file.
     * @throws IllegalArgumentException if the output location is not console or html.
     */
    private void changeOutputLocation(String outputLocation) throws IllegalArgumentException {
        if (outputLocation.equals(CONSOLE)) {
            asciiOutput = new ConsoleAsciiOutput();
        } else if (outputLocation.equals(HTML)) {
            asciiOutput = new HtmlAsciiOutput(DEFAULT_OUTPUT_IMAGE_PATH, DEFAULT_FONT);
        } else {
            throw new IllegalArgumentException(INCORRECT_FORMAT_MESSAGE);
        }
    }

    /**
     * The setNewImage method is responsible for setting a new image to the program.
     *
     * @param newImagePath the new image path.
     * @throws IOException if the image file is not found.
     */
    private void setNewImage(String newImagePath) throws IOException {
        try {
            Image newImage = new Image(newImagePath);
            asciiArtAlgorithm.setImage(newImage);
            asciiArtAlgorithm.setAChangeOfImageWasMade(true);
        } catch (IOException e) {
            throw new IOException(IMAGE_FILE_ERROR_MESSAGE);
        }
    }


    /**
     * The addChar method is responsible for adding a new character to the charset.
     *
     * @param userInput the new character to add.
     * @throws IllegalArgumentException if the input is not in the correct format.
     */
    private void addChar(String userInput) throws IllegalArgumentException {
        asciiArtAlgorithm.setAChangeInCharsWasMade(true);
        if (userInput.equals(ALL_CHARS)) {
            for (int i = 32; i < 127; ++i) {
                asciiArtAlgorithm.addChar((char) i);
            }
        } else if (userInput.equals(SPACE)) {
            asciiArtAlgorithm.addChar(SPACE_CHAR);
        } else if (userInput.contains(DASH) && !userInput.equals(DASH) && userInput.length() == 3) {
            char start = userInput.charAt(0);
            char end = userInput.charAt(2);
            // Determine the direction of the iteration based on the characters' order
            if (start <= end) {
                for (char c = start; c <= end; c++) {
                    asciiArtAlgorithm.addChar(c);
                }
            } else {
                for (char c = start; c >= end; c--) {
                    asciiArtAlgorithm.addChar(c);
                }
            }
        } else if (userInput.length() == 1) {
            char c = userInput.charAt(0);
            if (!asciiArtAlgorithm.containsChar(c)) {
                asciiArtAlgorithm.addChar(c);
            } else {
                asciiArtAlgorithm.setAChangeInCharsWasMade(false);
            }
        } else {
            asciiArtAlgorithm.setAChangeInCharsWasMade(false);
            throw new IllegalArgumentException(ADD_ERROR_MESSAGE);
        }
    }


    /**
     * The removeChar method is responsible for removing a character from the charset.
     *
     * @param userInput the character to remove.
     * @throws IllegalArgumentException if the input is not in the correct format.
     */
    private void removeChar(String userInput) throws IllegalArgumentException {
        asciiArtAlgorithm.setAChangeInCharsWasMade(true);
        if (userInput.equals(ALL_CHARS)) {
            for (int i = 32; i < 127; ++i) {
                asciiArtAlgorithm.removeChar((char) i);
            }
        } else if (userInput.equals(SPACE)) {
            asciiArtAlgorithm.removeChar(SPACE_CHAR);
        } else if (userInput.contains(DASH) && !userInput.equals(DASH) && userInput.length() == 3) {
            char start = userInput.charAt(0);
            char end = userInput.charAt(2);
            if (start <= end) {
                for (char c = start; c <= end; c++) {
                    asciiArtAlgorithm.removeChar(c);
                }
            } else {
                for (char c = start; c >= end; c--) {
                    asciiArtAlgorithm.removeChar(c);
                }
            }
        } else if (userInput.length() == 1) {
            char c = userInput.charAt(0);
            if (asciiArtAlgorithm.containsChar(c)) {
                asciiArtAlgorithm.removeChar(c);
            } else {
                asciiArtAlgorithm.setAChangeInCharsWasMade(false);
            }
        } else {
            asciiArtAlgorithm.setAChangeInCharsWasMade(false);
            throw new IllegalArgumentException(REMOVE_ERROR_MESSAGE);
        }
    }

    /**
     * The changeResolution method is responsible for changing the resolution of the program.
     *
     * @param newResolutionStr the new resolution.
     * @throws IllegalArgumentException if the input resolution can't be applied is not in the
     *                                  correct format.
     */
    private void changeResolution(String newResolutionStr) throws IllegalArgumentException {
        int imgWidth = asciiArtAlgorithm.getImageWidth();
        int imgHeight = asciiArtAlgorithm.getImageHeight();
        int minCharsInRow = Math.max(1, imgWidth / imgHeight);
        int maxCharsInRow = imgWidth;
        int currentResolution = asciiArtAlgorithm.getResolution();
        if (newResolutionStr.equals(UP_COMMAND)) {
            if (currentResolution * 2 <= maxCharsInRow) {
                asciiArtAlgorithm.setResolution(currentResolution * 2);
                System.out.println(String.format(CHANGE_RESOLUTION_MESSAGE, currentResolution * 2));
                asciiArtAlgorithm.setAChangeOfResolutionWasMade(true);
            } else {
                throw new IllegalArgumentException(IMAGE_RESOLUTION_ERROR_MESSAGE);
            }
        } else if (newResolutionStr.equals(DOWN_COMMAND)) {
            if (currentResolution / 2 >= minCharsInRow) {
                asciiArtAlgorithm.setResolution(currentResolution / 2);
                System.out.println(String.format(CHANGE_RESOLUTION_MESSAGE, currentResolution / 2));
                asciiArtAlgorithm.setAChangeOfResolutionWasMade(true);
            } else {
                throw new IllegalArgumentException(EXCEEDING_BOUNDARIES_MESSAGE);
            }
        } else {
            throw new IllegalArgumentException(INCORRECT_RESOLUTION_FORMAT_MESSAGE);
        }
    }


    /**
     * The main method of the program.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run();
    }
}

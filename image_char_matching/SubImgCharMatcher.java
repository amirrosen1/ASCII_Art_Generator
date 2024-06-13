package image_char_matching;

import java.util.*;

/**
 * SubImgCharMatcher class is responsible for matching characters to the brightness of a sub-image.
 */
public class SubImgCharMatcher {
    private final TreeMap<Double, TreeSet<Character>> normalizedBrightnessToCharsMap = new TreeMap<>();
    private final HashMap<Character, Double> rawBrightnessMap = new HashMap<>();
    private double minBrightness = Double.MAX_VALUE;
    private double maxBrightness = Double.MIN_VALUE;
    private boolean minMaxChanged = false;


    /**
     * Constructor to create an object of SubImgCharMatcher class.
     * @param charset char[] of the characters to match
     */
    public SubImgCharMatcher(char[] charset) {
        initBrightnessValues(charset);
    }

    /**
     * This method initializes the brightness values of the characters.
     * @param charset char[] of the characters to calculate brightness for
     */
    private void initBrightnessValues(char[] charset) {
        for (char c : charset) {
            double brightness = calculateBrightness(c);

            addCharacterToBrightnessMap(c, brightness);
        }
        normalizeAllBrightnessValues();
    }

    /**
     * This method adds a character to the regular brightness map.
     * @param c char of the character
     * @param brightness double of the brightness value
     */
    private void addCharacterToBrightnessMap(char c, double brightness) {
        rawBrightnessMap.put(c, brightness);
        updateMinAndMaxBrightness(brightness, true);
    }

    /**
     * This method updates the min and max brightness values.
     * @param brightness double of the brightness value
     * @param adding boolean, true if adding, false if removing
     */
    private void updateMinAndMaxBrightness(double brightness, boolean adding) {
        if (adding) {
            if (brightness < minBrightness) {
                minBrightness = brightness;
                minMaxChanged = true;
            }
            if (brightness > maxBrightness) {
                maxBrightness = brightness;
                minMaxChanged = true;
            }
        } else {
            if (brightness == minBrightness || brightness == maxBrightness) {
                minMaxChanged = true;
                if (normalizedBrightnessToCharsMap.containsKey(brightness) &&
                        normalizedBrightnessToCharsMap.get(brightness).isEmpty()) {
                    // Need to find new min or max
                    if (brightness == minBrightness) {
                        minBrightness = normalizedBrightnessToCharsMap.higherKey(minBrightness);
                    }
                    if (brightness == maxBrightness) {
                        maxBrightness = normalizedBrightnessToCharsMap.lowerKey(maxBrightness);
                    }
                }
            }
        }
    }

    /**
     * This method normalizes all the brightness values.
     */
    private void normalizeAllBrightnessValues() {
        if (!minMaxChanged) return; // Skip if min/max haven't changed
        normalizedBrightnessToCharsMap.clear();
        for (char c : rawBrightnessMap.keySet()) {
            double normalizedBrightness = normalizeBrightness(rawBrightnessMap.get(c));
            normalizedBrightnessToCharsMap.computeIfAbsent(normalizedBrightness, k -> new TreeSet<>()).add(c);
        }
        minMaxChanged = false; // Reset flag after recalculating
    }



    /**
     * This method returns the character that best matches the given brightness.
     * @param brightness double of the brightness value
     * @return char of the character that best matches the brightness
     */
    public char getCharByImageBrightness(double brightness) {
        Double lowerKey = normalizedBrightnessToCharsMap.floorKey(brightness);
        Double higherKey = normalizedBrightnessToCharsMap.ceilingKey(brightness);
        if (lowerKey == null) {
            return normalizedBrightnessToCharsMap.get(higherKey).first();
        }

        if (higherKey == null) {
            return normalizedBrightnessToCharsMap.get(lowerKey).first();
        }
        double lowerDiff = Math.abs(brightness - lowerKey);
        double higherDiff = Math.abs(brightness - higherKey);
        if (lowerDiff < higherDiff) {
            return normalizedBrightnessToCharsMap.get(lowerKey).first();
        } else {
            return normalizedBrightnessToCharsMap.get(higherKey).first();
        }

    }


    /**
     * This method adds a character to the char maps - regular and normalized.
     * @param c the char to add
     */
    public void addChar(char c) {
        double brightness = calculateBrightness(c);
        addCharacterToBrightnessMap(c, brightness);
        if (minMaxChanged) {
            normalizeAllBrightnessValues(); // Recalculate only if needed
        }
        else {
            double normalizedBrightness = normalizeBrightness(brightness);
            normalizedBrightnessToCharsMap.computeIfAbsent(normalizedBrightness, k -> new TreeSet<>()).add(c);
        }
    }

    /**
     * This method removes a character from the char maps - regular and normalized.
     * @param c the char to remove
     */
    public void removeChar(char c) {
        if (!rawBrightnessMap.containsKey(c)) return; // Character not present

        double brightness = rawBrightnessMap.remove(c);
        double normalizedBrightness = normalizeBrightness(brightness);
        TreeSet<Character> charSet = normalizedBrightnessToCharsMap.get(normalizedBrightness);
        charSet.remove(c);
        if (charSet.isEmpty()) {
            normalizedBrightnessToCharsMap.remove(normalizedBrightness);
        }
        updateMinAndMaxBrightness(brightness, false);
        if (minMaxChanged) {
            normalizeAllBrightnessValues(); // Recalculate only if needed
        }
    }

    /**
     * This method calculates the brightness of a character.
     * @param c char of the character
     * @return double of the brightness value
     */
    private double calculateBrightness(char c) {
        boolean[][] charImage = CharConverter.convertToBoolArray(c);
        double whitePixels = 0;
        for (boolean[] row : charImage) {
            for (boolean pixel : row) {
                if (pixel) {
                    whitePixels++;
                }
            }
        }
        return whitePixels / (charImage.length * charImage[0].length);
    }

    /**
     * This method normalizes the brightness value.
     * @param brightness double of the brightness value
     * @return double of the normalized brightness value
     */
    private double normalizeBrightness(double brightness) {
        if (maxBrightness == minBrightness) return 1.0;
        return (brightness - minBrightness) / (maxBrightness - minBrightness);
    }

    /**
     * This method returns the current characters in the map.
     * @return TreeSet of Character, containing the current characters in the map
     */
    public TreeSet<Character> getCurrentCharsInMap(){
        return new TreeSet<>(rawBrightnessMap.keySet());
    }
}

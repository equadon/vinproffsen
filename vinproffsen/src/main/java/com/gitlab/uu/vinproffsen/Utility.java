package com.gitlab.uu.vinproffsen;

/**
 * Utility methods.
 *
 * @author Niklas Persson
 * @version 2016-03-20
 */
public class Utility {
    /**
     * Return a pluralized string.
     * @param count number
     * @param singular singular of word
     * @param plural plural of word
     * @return pluralized string with format: "# singular/plural"
     */
    public static String pluralize(int count, String singular, String plural) {
        return count + " " + ((count == 1) ? singular : plural);
    }
}

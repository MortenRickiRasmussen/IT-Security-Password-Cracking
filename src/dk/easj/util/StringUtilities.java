package dk.rhs.util;

/**
 * Various string utilities, not included in the class {@code String}
 * @author andersb
 */
public class StringUtilities {

    /**
     * Capicalizes the first letter of a string
     * @param str the string to be capitalized
     * @return a capitalized version of {@code str}
     */
    public static String capitalize(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("str is null");
        }
        if (str.isEmpty()) {
            return str;
        }
        final String firstLetterUppercase = str.substring(0, 1).toUpperCase();
        final String theRest = str.substring(1);
        return firstLetterUppercase + theRest;
    }
}

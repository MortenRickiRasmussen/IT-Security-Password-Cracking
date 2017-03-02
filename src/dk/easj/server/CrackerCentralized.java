package dk.rhs.distributedpasswordcracking;

import dk.rhs.util.StringUtilities;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Password cracking dictionary attack (a brute force algorithm)
 * Centralized, i.e. not distributed
 * @author andersb
 */
public class CrackerCentralized {

    private static MessageDigest messageDigest;
    private static final Logger LOGGER = Logger.getLogger("passwordCracker");

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Starts the password cracking program.
     * Writes the time used for cracking.
     * @param args the command line arguments, not used
     */
    public static void main(final String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();

        final List<UserInfo> userInfos = PasswordFileHandler.readPasswordFile("passwords.txt");
        final List<UserInfoClearText> result = new ArrayList<UserInfoClearText>();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("webster-dictionary.txt");
            final BufferedReader dictionary = new BufferedReader(fileReader);
            while (true) {
                final String dictionaryEntry = dictionary.readLine();
                if (dictionaryEntry == null) {
                    break;
                }
                final List<UserInfoClearText> partialResult = checkWordWithVariations(dictionaryEntry, userInfos);
                result.addAll(partialResult);
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
        final long endTime = System.currentTimeMillis();
        final long usedTime = endTime - startTime;
        System.out.println(result);
        System.out.println("Used time: " + usedTime / 1000 + " seconds = " + usedTime / 60000.0 + " minutes");
    }

    /**
     * Checks a single word from a dictionary, against a list of encrypted passwords.
     * Tries different variations on the dictionary entry, like all uppercase, adding digits to the end of the entry, etc.
     *
     * @param dictionaryEntry a single word from a dictionary, i.e. a possible password
     * @param userInfos a list of user information records: username + encrypted password
     */
    static List<UserInfoClearText> checkWordWithVariations(final String dictionaryEntry, final List<UserInfo> userInfos) {
        final List<UserInfoClearText> result = new ArrayList<UserInfoClearText>();

        final String possiblePassword = dictionaryEntry;
        final List<UserInfoClearText> partialResult = checkSingleWord(userInfos, possiblePassword);
        result.addAll(partialResult);

        final String possiblePasswordUpperCase = dictionaryEntry.toUpperCase();
        final List<UserInfoClearText> partialResultUpperCase = checkSingleWord(userInfos, possiblePasswordUpperCase);
        result.addAll(partialResultUpperCase);

        final String possiblePasswordCapitalized = StringUtilities.capitalize(dictionaryEntry);
        final List<UserInfoClearText> partialResultCapitalized  = checkSingleWord(userInfos, possiblePasswordCapitalized);
        result.addAll(partialResultCapitalized);

        final String possiblePasswordReverse = new StringBuilder(dictionaryEntry).reverse().toString();
         final List<UserInfoClearText> partialResultReverse = checkSingleWord(userInfos, possiblePasswordReverse);
         result.addAll(partialResultReverse);

        for (int i = 0; i < 100; i++) {
            final String possiblePasswordEndDigit = dictionaryEntry + i;
            final List<UserInfoClearText> partialResultEndDigit= checkSingleWord(userInfos, possiblePasswordEndDigit);
            result.addAll(partialResultEndDigit);
        }

        for (int i = 0; i < 100; i++) {
            final String possiblePasswordStartDigit = i + dictionaryEntry;
            final List<UserInfoClearText> partialResultStartDigit = checkSingleWord(userInfos, possiblePasswordStartDigit);
            result.addAll(partialResultStartDigit);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                final String possiblePasswordStartEndDigit = i + dictionaryEntry + j;
                final List<UserInfoClearText> partialResultStartEndDigit = checkSingleWord(userInfos, possiblePasswordStartEndDigit);
                result.addAll(partialResultStartEndDigit);
            }
        }

        return result;
    }

    /**
     * Check a single  word (may include a single variation)from the dictionary against a list of encrypted passwords
     * @param userInfos a list of user information records: username + encrypted password
     * @param possiblePassword a single dictionary entry (may include a single variation)
     * @return the user information record, if the dictionary entry matches the users password, or {@code  null} if not.
     */
    static List<UserInfoClearText> checkSingleWord(final List<UserInfo> userInfos, final String possiblePassword) {
        final byte[] digest = messageDigest.digest(possiblePassword.getBytes());
        final List<UserInfoClearText> results = new ArrayList<UserInfoClearText>();
        for (UserInfo userInfo : userInfos) {
            if (Arrays.equals(userInfo.getEntryptedPassword(), digest)) {
                results.add(new UserInfoClearText(userInfo.getUsername(), possiblePassword));
            }
        }
        return results;
    }
}

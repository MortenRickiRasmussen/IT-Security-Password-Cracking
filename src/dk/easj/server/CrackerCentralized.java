package dk.easj.server;

import com.sun.xml.internal.ws.util.StringUtils;

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
 *
 * @author andersb
 */
public class CrackerCentralized {

    private MessageDigest messageDigest;
    private Logger LOGGER = Logger.getLogger("passwordCracker");

    {
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
     *
     * @param args the command line arguments, not used
     */
    public void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        List<UserInfo> userInfos = PasswordFileHandler.readPasswordFile("passwords.txt");
        List<UserInfoClearText> result = new ArrayList<UserInfoClearText>();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("webster-dictionary.txt");
            BufferedReader dictionary = new BufferedReader(fileReader);
            while (true) {
                String dictionaryEntry = dictionary.readLine();
                if (dictionaryEntry == null) {
                    break;
                }
                List<UserInfoClearText> partialResult = checkWordWithVariations(dictionaryEntry, userInfos);
                result.addAll(partialResult);
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
        long endTime = System.currentTimeMillis();
        long usedTime = endTime - startTime;
        System.out.println(result);
        System.out.println("Used time: " + usedTime / 1000 + " seconds = " + usedTime / 60000.0 + " minutes");
    }

    /**
     * Checks a single word from a dictionary, against a list of encrypted passwords.
     * Tries different variations on the dictionary entry, like all uppercase, adding digits to the end of the entry, etc.
     *
     * @param dictionaryEntry a single word from a dictionary, i.e. a possible password
     * @param userInfos       a list of user information records: username + encrypted password
     */
    List<UserInfoClearText> checkWordWithVariations(String dictionaryEntry, List<UserInfo> userInfos) {
        List<UserInfoClearText> result = new ArrayList<UserInfoClearText>();

        String possiblePassword = dictionaryEntry;
        List<UserInfoClearText> partialResult = checkSingleWord(userInfos, possiblePassword);
        result.addAll(partialResult);

        String possiblePasswordUpperCase = dictionaryEntry.toUpperCase();
        List<UserInfoClearText> partialResultUpperCase = checkSingleWord(userInfos, possiblePasswordUpperCase);
        result.addAll(partialResultUpperCase);

        String possiblePasswordCapitalized = StringUtils.capitalize(dictionaryEntry);
        List<UserInfoClearText> partialResultCapitalized = checkSingleWord(userInfos, possiblePasswordCapitalized);
        result.addAll(partialResultCapitalized);

        String possiblePasswordReverse = new StringBuilder(dictionaryEntry).reverse().toString();
        List<UserInfoClearText> partialResultReverse = checkSingleWord(userInfos, possiblePasswordReverse);
        result.addAll(partialResultReverse);

        for (int i = 0; i < 100; i++) {
            String possiblePasswordEndDigit = dictionaryEntry + i;
            List<UserInfoClearText> partialResultEndDigit = checkSingleWord(userInfos, possiblePasswordEndDigit);
            result.addAll(partialResultEndDigit);
        }

        for (int i = 0; i < 100; i++) {
            String possiblePasswordStartDigit = i + dictionaryEntry;
            List<UserInfoClearText> partialResultStartDigit = checkSingleWord(userInfos, possiblePasswordStartDigit);
            result.addAll(partialResultStartDigit);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                String possiblePasswordStartEndDigit = i + dictionaryEntry + j;
                List<UserInfoClearText> partialResultStartEndDigit = checkSingleWord(userInfos, possiblePasswordStartEndDigit);
                result.addAll(partialResultStartEndDigit);
            }
        }

        return result;
    }

    /**
     * Check a single  word (may include a single variation)from the dictionary against a list of encrypted passwords
     *
     * @param userInfos        a list of user information records: username + encrypted password
     * @param possiblePassword a single dictionary entry (may include a single variation)
     * @return the user information record, if the dictionary entry matches the users password, or {@code  null} if not.
     */
    List<UserInfoClearText> checkSingleWord(List<UserInfo> userInfos, String possiblePassword) {
        byte[] digest = messageDigest.digest(possiblePassword.getBytes());
        List<UserInfoClearText> results = new ArrayList<UserInfoClearText>();
        for (UserInfo userInfo : userInfos) {
            if (Arrays.equals(userInfo.getEntryptedPassword(), digest)) {
                results.add(new UserInfoClearText(userInfo.getUsername(), possiblePassword));
            }
        }
        return results;
    }
}

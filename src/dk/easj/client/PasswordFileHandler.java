package dk.easj.client;

import dk.easj.server.UserInfo;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Various utilites useful to create and read a password file
 *
 * @author andersb
 */
public class PasswordFileHandler {

    public static final String MESSAGE_DIGEST_ALGORITHM = "SHA";

    /**
     * Writes a liste of usernames and encrypted and encoded (BASE64) passwords to a file
     *
     * @param filename  the name of the file
     * @param usernames the list of usernames
     * @param passwords the list of passwords (not encrypted, yet)
     * @throws NoSuchAlgorithmException if the encryption algorithm does not exist
     * @throws IOException              if there was an IOException, relating to the file
     */
    public static void writePasswordFile(final String filename, final String[] usernames, final String[] passwords) throws NoSuchAlgorithmException, IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        if (usernames.length != passwords.length) {
            throw new IllegalArgumentException("usernames and passwords must be same lengths");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            final BASE64Encoder base64Encoder = new BASE64Encoder();
            for (int i = 0; i < usernames.length; i++) {
                final byte[] encryptedPassword = messageDigest.digest(passwords[i].getBytes());
                final String line = usernames[i] + ":" + base64Encoder.encode(encryptedPassword) + "\n";
                fos.write(line.getBytes());
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Returns a List of UserInfo records from the specified password file
     *
     * @param filename the name of the password file
     * @return a List of UserInfo records from the specified password file
     * @throws IOException if something bad happens while reading the file
     */
    public static ArrayList<UserInfo> readPasswordFile(String filename) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            ArrayList<UserInfo> result = new ArrayList<>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] parts = line.split(":");
                UserInfo userInfo = new UserInfo(parts[0], parts[1]);
                result.add(userInfo);
            }
            return result;
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }
}

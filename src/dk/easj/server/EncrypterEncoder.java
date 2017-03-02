package dk.rhs.distributedpasswordcracking;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;

/**
 * Encrypts and encodes (BASE64) a password
 * @author andersb
 */
public class EncrypterEncoder {

    private final MessageDigest messageDigest;
    private final BASE64Encoder base64encoder = new BASE64Encoder();

    /**
     * Encrypts (using the specified algorithm) and encodes (using BASE64)
     * @param encryptionAlgorithm the algorithm to use for encryption
     * @throws NoSuchAlgorithmException if the specified encryption algorithm is not available
     */
    public EncrypterEncoder(final String encryptionAlgorithm) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(encryptionAlgorithm);
    }

    /**
     * Encrypts (using the specified algorithm) and encodes (using BASE64)
     * @param str the string to be encrypted
     * @return an encrypted and encoded version of {@code str}
     */
    public String encryptAndEncode(final String str) {
        final byte[] encryptedString = messageDigest.digest(str.getBytes());
        return base64encoder.encode(encryptedString);
    }
}

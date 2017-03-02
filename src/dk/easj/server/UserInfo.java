package dk.rhs.distributedpasswordcracking;

import java.io.IOException;
import java.io.Serializable;
import sun.misc.BASE64Decoder; // Will generate warnings

/**
 * Username + encrypted and encoded (BASE64) password
 * @author andersb
 */
public class UserInfo implements Serializable {

    private static final BASE64Decoder BASE64DECODER = new BASE64Decoder();
    private final String username;
    private final String entryptedPasswordBASE64;
    private final byte[] entryptedPassword;

    /**
     * Constructs a new object of this class
     * @param username the username of the UserInfo
     * @param entryptedPasswordBASE64 encrypted and encoded (Base&¤) password
     */
    public UserInfo(final String username, final String entryptedPasswordBASE64) throws IOException {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }
        if (entryptedPasswordBASE64 == null) {
            throw new IllegalArgumentException("entryptedPasswordBASE64 is null");
        }
        this.username = username;
        this.entryptedPasswordBASE64 = entryptedPasswordBASE64;
        this.entryptedPassword = BASE64DECODER.decodeBuffer(entryptedPasswordBASE64);
    }

    /**
     * Get the value of entryptedPasswordBASE64
     *
     * @return the value of entryptedPasswordBASE64
     */
    public String getEntryptedPasswordBASE64() {
        return entryptedPasswordBASE64;
    }

    /**
     * The encrypted (byt not encoded) password
     * @return
     */
    public byte[] getEntryptedPassword() {
        return entryptedPassword;
    }

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns a string representation of this object
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return username + ":" + entryptedPasswordBASE64;
    }

    /**
     * Returns {@code true} if the this object is equals to {@code obj}
     * Two UserInfo objects are equal if they have the same username
     * @param obj the object to compare to
     * @return {@code true} if the this object is equals to {@code obj}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserInfo other = (UserInfo) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the hashcode of this object
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }
}

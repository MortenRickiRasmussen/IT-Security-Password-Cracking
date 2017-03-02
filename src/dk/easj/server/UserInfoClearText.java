package dk.rhs.distributedpasswordcracking;

import java.io.Serializable;

/**
 * Username + password. The password is in clear text, i.e. not encrypted
 * @author peterl
 */
public class UserInfoClearText implements Serializable {

    private final String username;
    private final String password;

    /**
     * Creates a new object of this class
     * @param username the username
     * @param password the password, clear text, i.e. not encrypted
     */
    public UserInfoClearText(final String username, final String password) {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }
        if (password == null) {
            throw new IllegalArgumentException("password is null");
        }
        this.username = username;
        this.password = password;
    }

    /**
     * Return the password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Return the username
     * @return the username
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
        return username + ": " + password;
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
        final UserInfoClearText other = (UserInfoClearText) obj;
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
        int hash = 7;
        hash = 13 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }
}

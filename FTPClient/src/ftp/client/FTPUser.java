package ftp.client;

import java.net.Inet4Address;

/**
 *
 * @author silasmsales
 */
public class FTPUser {
    private String username;
    private String password;
    private Inet4Address IPAddress;

    public FTPUser(String username, String password, Inet4Address IPAddress) {
        this.username = username;
        this.password = password;
        this.IPAddress = IPAddress;
    }

    public FTPUser() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Inet4Address getIPAddress() {
        return IPAddress;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param IPAddress the IPAddress to set
     */
    public void setIPAddress(Inet4Address IPAddress) {
        this.IPAddress = IPAddress;
    }

}

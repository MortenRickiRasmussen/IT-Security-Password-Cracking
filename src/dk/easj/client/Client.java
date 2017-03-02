package dk.easj.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jakob on 02-03-17.
 */
public class Client {

    private String hostname;
    private int port;
    private String clientName;
    private boolean loggedIn;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        connect(hostname, port);
    }

    /**
     * Sends a connection request to the server
     *
     * @param hostname
     * @param port
     */
    public void connect(String hostname, int port) {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = clientName;
            this.hostname = hostname;
            this.port = port;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

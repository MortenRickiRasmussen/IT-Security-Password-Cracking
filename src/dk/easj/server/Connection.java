package dk.easj.server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by morty on 07-09-16.
 */
public class Connection extends Thread {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private Server server;

    public Connection(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;

        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;

        try {
            while ((message = input.readLine()) != null) {

            }
        } catch (IOException e) {
            /*When socket closes an exception is thrown
            Therefore do nothing when that happens*/
        }
    }

}

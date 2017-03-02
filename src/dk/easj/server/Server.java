package dk.easj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by morty on 02-Mar-17.
 */
public class Server implements Runnable {

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    private ServerSocket serverSocket;
    private ArrayList<Socket> sockets;
    private boolean running;

    public Server() {
        sockets = new ArrayList<>();
    }

    public void run() {
        running = true;

        try {
            serverSocket = new ServerSocket(9999);

            System.out.println("Server running");

            while (running) {
                Socket socket = serverSocket.accept();
                Connection slave = new Connection(this, socket);
                slave.start();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}

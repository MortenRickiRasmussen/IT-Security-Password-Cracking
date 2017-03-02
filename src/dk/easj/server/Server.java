package dk.easj.server;

import java.io.IOException;
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
    private ArrayList<Slave> slaves;
    private boolean running;

    public Server() {
        slaves = new ArrayList<>();
    }

    public void run() {
        running = true;

        try {
            serverSocket = new ServerSocket(9999);

            System.out.println("Server running");

            while (running) {
                Socket socket = serverSocket.accept();
                Slave slave = new Slave(this, socket);
                slave.start();
                slaves.add(slave);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void serverStop() {
        running = false;

        try {
            for (Slave slave : slaves) {
                slave.disconnect();
            }
            slaves.clear();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

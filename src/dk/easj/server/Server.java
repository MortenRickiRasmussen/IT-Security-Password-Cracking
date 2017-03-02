package dk.easj.server;

import java.io.BufferedReader;
import java.io.FileReader;
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
    private ArrayList<UserInfo> passwordFile;
    private ArrayList<String> dictionary;

    public Server() {
        try {
            slaves = new ArrayList<>();
            dictionary = new ArrayList<>();
            passwordFile = new ArrayList<>();
            readPasswordFile();
            readDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;

        try {
            serverSocket = new ServerSocket(9999);

            System.out.println("Server running");

            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("Connectied");
                Slave slave = new Slave(this, socket);
                slave.start();
                slaves.add(slave);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public void readDictionary() throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("webster-dictionary.txt");
            BufferedReader dictionary = new BufferedReader(fileReader);
            while (dictionary.readLine() != null) {
                this.dictionary.add(dictionary.readLine());
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }


    public void readPasswordFile() throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("passwords.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] parts = line.split(":");
                UserInfo userInfo = new UserInfo(parts[0], parts[1]);
                this.passwordFile.add(userInfo);
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }
}

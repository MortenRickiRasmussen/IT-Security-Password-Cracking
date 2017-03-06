package dk.easj.client;

import dk.easj.server.UserInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jakob on 02-03-17.
 */
public class Client {

    private String hostname;
    private int port;
    private Thread[] threads;
    private ArrayList<UserInfoClearText> result;




    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.result = new ArrayList<>();
        this.threads = new Thread[Runtime.getRuntime().availableProcessors()];
        connect();
    }

    /**
     * Sends a connection request to the server
     */
    private void connect() {
        try {
            Socket socket = new Socket(InetAddress.getByName(hostname), port);
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());


            ArrayList<String> chunk;

            try {
                while ((chunk = (ArrayList<String>) inFromServer.readObject()) != null) {
                    ArrayList<UserInfo> userInfos = (ArrayList<UserInfo>) inFromServer.readObject();
                    int steps = chunk.size()/threads.length;
                    for (int i = 0; i < threads.length; i++) {
                        ArrayList<String> bite = new ArrayList<>(chunk.subList(steps*i+1, steps*(i+1)));
                        Thread thread = new Thread(new Cracker(userInfos, bite, this));
                        threads[i] = thread;
                    }

                    for (Thread thread : threads) {
                        thread.start();
                    }
                    for (Thread thread : threads) {
                        thread.join();
                    }

                    System.out.println(result);
                    outToServer.writeObject(result);
                    outToServer.flush();
                    result = new ArrayList<>();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addResult(ArrayList<UserInfoClearText> result){
        this.result.addAll(result);
    }

}

package dk.easj.server;

import dk.easj.model.UserInfo;
import dk.easj.model.UserInfoClearText;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private List<UserInfo> passwordFile;
    private List<String> dictionary;
    private int linesSend = 0;
    private ArrayList<UserInfoClearText> result;
    private int count = 0;

    public Server() {
        try {
            slaves = new ArrayList<>();
            dictionary = new ArrayList<>();
            passwordFile = new ArrayList<>();
            result = new ArrayList<>();
            readPasswordFile();
            readDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getChunk() {
        int linesToSend = 10000;
        this.linesSend += 10000;
        if (linesSend > dictionary.size()) {
            System.out.println(linesSend + " og " + count);
            return  new ArrayList<>();
        }else{
            return new ArrayList<>(dictionary.subList(this.linesSend - linesToSend, this.linesSend));
        }
    }



    public void run() {
        running = true;

        try {
            serverSocket = new ServerSocket(9999);

            System.out.println("Server running");

            while (running) {
                System.out.println("Connecting...");
                Socket socket = serverSocket.accept();
                System.out.println("Connected");
                Slave slave = new Slave(this, socket);
                startSlave(slave);
                slave.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void startSlave(Slave slave) {

        List<String> chunk = getChunk();
        if(chunk.size() > 0) {
            slave.getMessage(getChunk());
            slave.getMessage(this.passwordFile);
            slaves.add(slave);
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
            String line;
            while ((line = dictionary.readLine()) != null) {
                this.dictionary.add(line);
                count++;
            }
            System.out.println(this.dictionary.size());
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    public synchronized void addResult(ArrayList<UserInfoClearText> result){
        this.result.addAll(result);
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

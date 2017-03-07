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
import java.util.Scanner;

/**
 * Created by morty on 02-Mar-17.
 */
public class Server implements Runnable {

    public static void main(String[] args) {
        String passwordPath;
        String dictionaryPath;

        if (args.length > 0){
            passwordPath = args[0];
            dictionaryPath = args[1];
        }else{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the path to the password file");
            passwordPath = scanner.nextLine();
            System.out.println("Please enter the path to the dictionary file");
            dictionaryPath = scanner.nextLine();
        }

        Server server = new Server(passwordPath, dictionaryPath);
        server.run();
    }

    private ServerSocket serverSocket;
    private ArrayList<Slave> slaves;
    private boolean running;
    private List<UserInfo> passwordFile;
    private List<String> dictionary;
    private int linesSend = 0;
    private ArrayList<UserInfoClearText> result;
    private long startTime;
    private boolean done;
    private String passwordPath;
    private String dictionaryPath;

    public Server(String passwordPath, String dictionaryPath) {
        try {
            this.passwordPath = passwordPath;
            this.dictionaryPath = dictionaryPath;
            done = false;
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
        this.linesSend += linesToSend;
        if (linesSend >= dictionary.size()) {
            done = true;
            System.out.println("Sent line "+(this.linesSend - linesToSend)+" to "+(this.dictionary.size()));
            return new ArrayList<>(dictionary.subList(this.linesSend - linesToSend, this.dictionary.size()));
        } else {
            System.out.println("Sent line "+(this.linesSend - linesToSend)+" to "+(this.linesSend));
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
                if (startTime == 0){
                    startTime = System.currentTimeMillis();
                }
                System.out.println("Connected");
                Slave slave = new Slave(this, socket);
                startSlave(slave);
                slaves.add(slave);
                slave.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void startSlave(Slave slave) {
        if (done) {
            try {
                slave.disconnect();
                slaves.remove(slave);
                if (slaves.size() == 0){
                    serverStop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            ArrayList<String> chunk = (ArrayList<String>) getChunk();
            slave.getMessage(chunk);
            slave.getMessage(this.passwordFile);
        }
    }

    public void serverStop() {
        running = false;
        System.out.println("Found following results: "+result);
        long endTime = System.currentTimeMillis();
        long usedTime = endTime - startTime;
        System.out.println("Used time: " + usedTime / 1000 + " seconds = " + usedTime / 60000.0 + " minutes");
    }

    public void readDictionary() throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dictionaryPath);
            BufferedReader dictionary = new BufferedReader(fileReader);
            String line;
            while ((line = dictionary.readLine()) != null) {
                this.dictionary.add(line);
            }
            System.out.println(this.dictionary.size());
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    public synchronized void addResult(ArrayList<UserInfoClearText> result) {
        this.result.addAll(result);
    }


    public void readPasswordFile() throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(passwordPath);
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

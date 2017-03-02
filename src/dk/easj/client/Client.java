package dk.easj.client;

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
    private String clientName;
    private boolean loggedIn;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    private MessageDigest messageDigest;
    private Logger LOGGER = Logger.getLogger("passwordCracker");

    {
        try {
            messageDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        connect();
    }

    /**
     * Sends a connection request to the server
     */
    private void connect() {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());


            ArrayList<UserInfo> userInfos;

            try {
                while ((userInfos = (ArrayList<UserInfo>) inFromServer.readObject()) != null) {
                    ArrayList<String> chunk = (ArrayList<String>) inFromServer.readObject();

                    System.out.println(userInfos);
                    System.out.println(chunk);
                    ArrayList<UserInfoClearText> result = startCracking(userInfos, chunk);
                    System.out.println(result);
                    outToServer.writeObject(result);
                    outToServer.flush();
                }
            } catch (Exception ex) {
                //Not really a problem
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<UserInfoClearText> startCracking(ArrayList<UserInfo> userInfos, ArrayList<String> chunk) {
        ArrayList<UserInfoClearText> result = new ArrayList<>();
        for (String dict : chunk) {
            List<UserInfoClearText> partialResult = checkWordWithVariations(dict, userInfos);
            result.addAll(partialResult);
        }
        return result;
    }

    private ArrayList<UserInfoClearText> checkWordWithVariations(String dictionaryEntry, List<UserInfo> userInfos) {
        ArrayList<UserInfoClearText> result = new ArrayList<UserInfoClearText>();

        String possiblePassword = dictionaryEntry;
        List<UserInfoClearText> partialResult = checkSingleWord(userInfos, possiblePassword);
        result.addAll(partialResult);

        String possiblePasswordUpperCase = dictionaryEntry.toUpperCase();
        List<UserInfoClearText> partialResultUpperCase = checkSingleWord(userInfos, possiblePasswordUpperCase);
        result.addAll(partialResultUpperCase);

        String possiblePasswordCapitalized = StringUtilities.capitalize(dictionaryEntry);
        List<UserInfoClearText> partialResultCapitalized = checkSingleWord(userInfos, possiblePasswordCapitalized);
        result.addAll(partialResultCapitalized);

        String possiblePasswordReverse = new StringBuilder(dictionaryEntry).reverse().toString();
        List<UserInfoClearText> partialResultReverse = checkSingleWord(userInfos, possiblePasswordReverse);
        result.addAll(partialResultReverse);

        for (int i = 0; i < 100; i++) {
            String possiblePasswordEndDigit = dictionaryEntry + i;
            List<UserInfoClearText> partialResultEndDigit = checkSingleWord(userInfos, possiblePasswordEndDigit);
            result.addAll(partialResultEndDigit);
        }

        for (int i = 0; i < 100; i++) {
            String possiblePasswordStartDigit = i + dictionaryEntry;
            List<UserInfoClearText> partialResultStartDigit = checkSingleWord(userInfos, possiblePasswordStartDigit);
            result.addAll(partialResultStartDigit);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                String possiblePasswordStartEndDigit = i + dictionaryEntry + j;
                List<UserInfoClearText> partialResultStartEndDigit = checkSingleWord(userInfos, possiblePasswordStartEndDigit);
                result.addAll(partialResultStartEndDigit);
            }
        }

        return result;
    }

    private List<UserInfoClearText> checkSingleWord(List<UserInfo> userInfos, String possiblePassword) {
        byte[] digest = messageDigest.digest(possiblePassword.getBytes());
        List<UserInfoClearText> results = new ArrayList<UserInfoClearText>();
        for (UserInfo userInfo : userInfos) {
            if (Arrays.equals(userInfo.getEntryptedPassword(), digest)) {
                results.add(new UserInfoClearText(userInfo.getUsername(), possiblePassword));
            }
        }
        return results;
    }
}

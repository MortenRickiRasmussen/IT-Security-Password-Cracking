package dk.easj.client;

/**
 * Created by jakob on 02-03-17.
 */
public class Main {
    public static void main(String[] args) {
        int port = 9999;
        String hostname = args[0];

        Client client = new Client(hostname, port);
    }
}

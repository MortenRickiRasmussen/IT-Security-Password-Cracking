package dk.easj.client;

import java.util.Scanner;

/**
 * Created by jakob on 02-03-17.
 */
public class Main {
    public static void main(String[] args) {
        int port = 9999;
        String hostname;
        if (args.length > 0) {
            hostname = args[0];
        }else{
            System.out.println("Hostname not found in args, please input a hostname:");
            hostname = (new Scanner(System.in)).nextLine();
        }

        Client client = new Client(hostname, port);
    }
}

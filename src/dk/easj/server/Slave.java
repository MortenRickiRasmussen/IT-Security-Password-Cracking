package dk.easj.server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by morty on 07-09-16.
 */
public class Slave extends Thread {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Server server;

    public Slave(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        Object message;

        try {
            while ((message = input.readObject()) != null) {
                server.addResult((ArrayList<UserInfoClearText>) message);
                server.startSlave(this);
            }
        } catch (Exception e) {
            /*When socket closes an exception is thrown
            Therefore do nothing when that happens*/
        }
    }

    public void getMessage(List message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        if (output != null) {
            output.close();
        }
        if (input != null) {
            input.close();
        }
        if (socket.isConnected()) {
            socket.close();
        }
    }

}

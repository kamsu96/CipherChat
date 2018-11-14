package server;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Łukasz Sus
 */
public class ServerThread extends Thread {
    private String host;
    private ServerSocket serverSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean work;

    private ServerLogic serverLogic;

    public ServerThread(ServerLogic serverLogic) {
        this.serverLogic = serverLogic;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void run() {
        work = true;
        Socket socket;

        // initialization of network connection
        try {
            InetAddress hostInetAddress = InetAddress.getLocalHost();
            String address = hostInetAddress.getHostAddress();
            host = InetAddress.getLocalHost().getHostName();
            System.out.println(address);
            System.out.println(host);
            serverSocket = new ServerSocket(serverLogic.getPort());
        } catch (IOException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Socket for server cannot be created");
            System.exit(0);
        }
        System.out.println("Server has started to listen on host: " + host);

        while (work) {
            try {
                socket = serverSocket.accept();
                if (socket != null) {
                    System.out.println(String.format("Client socket created: %s.", socket.toString()));
                    ClientThread ct = new ClientThread(socket, serverLogic);
                    ct.start();
                    serverLogic.getClientThreads().add(ct);
                }
            } catch (IOException e) {
                System.out.println("Server socket error");
            }
        }

        try {
            output.close();
        } catch (IOException e) {
        }
        try {
            input.close();
        } catch (IOException e) {

        }
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }
}

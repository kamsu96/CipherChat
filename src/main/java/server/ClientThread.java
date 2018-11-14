package server;

import app.CipherChatApp;
import cipher.Encoder;
import jsonParser.JsonMessage;
import jsonParser.MessageType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Łukasz Sus
 */
public class ClientThread extends Thread {
    public static final boolean ENCRYPT = true;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean work;
    private Encoder encoder;

    private ServerLogic serverLogic;

    public ClientThread(Socket socket, ServerLogic serverLogic) {
        this.socket = socket;
        this.serverLogic = serverLogic;
        this.encoder = serverLogic.getEncoder();
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
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
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            while (work) {
                String message = (String) input.readObject();
                if(ENCRYPT) {
                    message = encoder.decode(message);
                } else {
                    System.out.println("IN: " + message);
                }
                JsonMessage jsonMessage = new JsonMessage(message);
                jsonMessage = jsonMessage.doAction(serverLogic, socket);
                //if(jsonMessage.getMsgType()!= MessageType.PONG) {
                    message = jsonMessage.toString();
                    if(ENCRYPT) {
                        message = encoder.encode(message);
                    }else{
                        System.out.println("OUT: " + jsonMessage.toString());
                    }
                    output.writeObject(message);
               // }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        serverLogic.finishConnection(socket);

        try {
            output.close();
        } catch (IOException e) {
        }
        try {
            input.close();
        } catch (IOException e) {
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}


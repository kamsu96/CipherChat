package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Łukasz Sus
 */
public class ClientSocket {

    private int port;
    private String host;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientSocket(int port) {
        this.port = port;
        try {
            host = InetAddress.getLocalHost().getHostName();
            socket = new Socket(host, port);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeObject(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage(){
        try {
            return input.readObject().toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new String();
    }

/*    private byte[] castSoapMessageToByteArray(SOAPMessage message) throws IOException, SOAPException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        //oos.writeObject(message);
        message.writeTo(oos);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return bytes;
    }*/

    public void close(){
        if(socket!=null) {
            try {
                socket.close();
            }catch(IOException e) {

            }
        }
        if(output!=null) {
            try{
                output.close();
            }catch(IOException e) {

            }
        }
        if(input!=null) {
            try {
                input.close();
            }catch(IOException e) {

            }
        }
    }

    @Override
    public void finalize() {
        if(socket!=null) {
            try {
                socket.close();
            }catch(IOException e) {

            }
        }
        if(output!=null) {
            try{
                output.close();
            }catch(IOException e) {

            }
        }
        if(input!=null) {
            try {
                input.close();
            }catch(IOException e) {

            }
        }
    }
}

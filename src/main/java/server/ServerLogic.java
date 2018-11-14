package server;

import cipher.Encoder;
import db.UserContext;
import jsonParser.JsonMessage;
import jsonParser.JsonMessageFactory;
import jsonParser.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Łukasz Sus
 */
public class ServerLogic {
    public static final boolean ENCRYPT = true;

    private UserContext userContext;
    private ServerThread serverThread;
    private ConnectionRefresher connRefresher;
    private List<ClientThread> clientThreads;
    private Map<String, Socket> clientSockets;
    private Map<String, List<JsonMessage>> messagesForClient;
    private int portNumber;
    private Encoder encoder;

    public ServerLogic(UserContext userContext, int portNumber){
        this.userContext = userContext;
        clientThreads = new ArrayList<>();
        serverThread = new ServerThread(this);
        connRefresher = new ConnectionRefresher(this);
        clientSockets = new HashMap<>();
        messagesForClient = new HashMap<>();

        this.portNumber = portNumber;
        encoder = new Encoder();
    }

    public void start(){
        serverThread.start();
        connRefresher.start();
    }

    public Encoder getEncoder() {
        return encoder;
    }
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public List<ClientThread> getClientThreads() {
        return clientThreads;
    }

    public Map<String, Socket> getClientSockets() {
        return clientSockets;
    }

    public int getPort() {
        return portNumber;
    }

    public JsonMessage regResponse(String login, String pass1, String pass2){
        if(isLoginUnique(login)){
            if(pass1.equals(pass2)) {
                addNewUser(login, pass1);
                return new JsonMessage(MessageType.REG, new String("true"), new String("Registration succeeded."));
            } else{
                return new JsonMessage(MessageType.REG, new String("false"), new String("Passwords don't match."));
            }
        } else{
            return new JsonMessage(MessageType.REG, new String("false"), new String("Login is already in the database."));
        }
    }

    private boolean isLoginUnique(String login){
        return userContext.isLoginUnique(login);
    }

    private void addNewUser(String login, String pass1){
        userContext.addUser(login, pass1);
    }

    public JsonMessage loginResponse(Socket socket, String login, String pass){
        if(isLoginAndPasswordCorrect(login, pass)){
            clientSockets.put(login, socket);
            System.out.println(String.format("Login: %s, Socket: %s", login, socket.toString()));
            return new JsonMessage(MessageType.LOGIN, new String("true"), new String("Login succeeded."));
        }else{
            return new JsonMessage(MessageType.LOGIN, new String("false"), new String("Login or password is incorrect."));
        }
    }

    private boolean isLoginAndPasswordCorrect(String login, String pass){
        return userContext.isPasswordCorrect(login, pass);
    }

    public JsonMessage textResponse(Socket socket, JsonMessage message){
        if(isSocketAuthorized(socket, message.getP1())){
            if(isUserLoggedIn(message.getP2())){
                //stackMessage(message);
                sendMessage(message);
                return new JsonMessage(MessageType.TEXT, new String("true"), new String("Succeeded."));
            }
            else{
                return new JsonMessage(MessageType.TEXT, new String("false"), new String("Recipient is not logged."));
            }
        } else{
            return socketUnathorized();
        }
    }

    private boolean isSocketAuthorized(Socket socket, String login){
        if(clientSockets.containsKey(login) && clientSockets.get(login).equals(socket)){
            return true;
        }
        return false;
    }
    private JsonMessage socketUnathorized(){
        return new JsonMessage(MessageType.TEXT, new String("false"), new String("You are not correctly logged in."));
    }

    private boolean isUserLoggedIn(String login){
        return clientSockets.containsKey(login);
    }

    public boolean sendMessage(JsonMessage message){
        String recipient = message.getP2();
        Socket socket = clientSockets.get(recipient);
        ObjectOutputStream outputStream = findOutput(socket);
        if(outputStream != null) {
            try {
                if(ENCRYPT) {
                    String strMessage = encoder.encode(message.toString());
                    outputStream.writeObject(strMessage);
                }
                else {
                    outputStream.writeObject(message.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            return false;
        }
    }

    private ObjectOutputStream findOutput(Socket socket){
        for(ClientThread ct : clientThreads){
            if(ct.getSocket().equals(socket)){
                return ct.getOutput();
            }
        }
        return null;
    }

    private void stackMessage(JsonMessage message){
        String recipient = message.getP2();
        if(messagesForClient.containsKey(recipient)){
            messagesForClient.get(recipient).add(message);
        }else{
            List<JsonMessage> messages = new ArrayList<>();
            messages.add(message);
            messagesForClient.put(recipient, messages);
        }
    }

/*    public JsonMessage loadResponse(Socket socket, String login){
        if(isSocketAuthorized(socket, login)) {
            if (messagesForClient.containsKey(login)) {
                return JsonMessageFactory.createLoadMsg(MessageType.LOAD, this.messagesForClient.get(login));
            } else {
                return new JsonMessage(MessageType.LOAD, new String("[]"));
            }
        }else{
            return socketUnathorized();
        }
    }*/

    public JsonMessage listResponse(Socket socket, String login, boolean active){
        if(isSocketAuthorized(socket, login)) {
            if (active) {
                List<String> keys = new ArrayList<>(clientSockets.keySet());
                return JsonMessageFactory.createListMsg(MessageType.LIST, keys);
            } else {
                return JsonMessageFactory.createListMsg(MessageType.LIST, userContext.getAllUserLogins());
            }
        }else{
            return socketUnathorized();
        }
    }

    public JsonMessage findResponse(Socket socket, String login, String phrase, boolean active){
        if(isSocketAuthorized(socket, login)) {
            if (active) {
                List<String> keys = new ArrayList<>(clientSockets.keySet());
                List<String> filtered = new ArrayList<>();
                phrase = new String(".*").concat(phrase).concat(new String(".*"));
                for(String s : keys){
                    if (Pattern.matches(phrase, s)){
                        filtered.add(s);
                    }
                }
                return JsonMessageFactory.createListMsg(MessageType.FIND, filtered);
            } else {
                return JsonMessageFactory.createListMsg(MessageType.FIND, userContext.getAllUserLogins(phrase));
            }
        }else{
            return socketUnathorized();
        }
    }

    public JsonMessage logoutResponse(Socket socket, String login){
        if(isSocketAuthorized(socket, login)) {
            if (clientSockets.keySet().contains(login)) {
                clientSockets.remove(login);
                return new JsonMessage(MessageType.LOGOUT, new String("true"));
            }else{
                return new JsonMessage(MessageType.LOGOUT, new String("false"), new String("User isn't logged."));
            }
        }else{
            return socketUnathorized();
        }
    }

    public JsonMessage pongResponse(Socket socket, String login){
        if(isSocketAuthorized(socket, login)){
            connRefresher.refreshActiveValue(login);
            return new JsonMessage(MessageType.PONG, new String("true"));
        } else{
            return socketUnathorized();
        }
    }

    public void finishConnection(Socket socket) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, Socket> entry : clientSockets.entrySet()) {
            if (socket.equals(entry.getValue())){
                keys.add(entry.getKey());
            }
        }
        for(String key : keys){
            if(clientSockets.containsKey(key)){
                clientSockets.remove(key);
            }
        }
    }

    public void remove(String login){
        Socket socket = clientSockets.get(login);
        List<ClientThread> toRemove = new ArrayList<>();
        for(ClientThread ct : clientThreads){
            if(ct.getSocket().equals(socket)) {
                toRemove.add(ct);
            }
        }
        for(ClientThread ct : toRemove){
            clientThreads.remove(ct);
        }
        clientSockets.remove(login);
    }

    public void close(){
        for(ClientThread ct : clientThreads){
            ct.setWork(false);
        }
        connRefresher.setWork(false);
        serverThread.setWork(false);
    }

    protected void finalize(){
        serverThread.setWork(false);
        for(ClientThread ct : clientThreads){
            ct.setWork(false);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package app;

import cipher.Encoder;
import jsonParser.JsonMessage;
import server.ClientSocket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientChatApp extends Thread{
    private static int port;
    private static boolean isLogged = false;
    private static JsonMessage json;
    private static ClientSocket clientSocket;
    private static String message = "";
    private static String nick = "";
    private static boolean isOnline;
    private static List<String> users = new ArrayList<>();
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private Socket socket;
    private static Encoder encoder = new Encoder();
    private static boolean alreadySentMessage = false;




    public static void main(String[] args) throws IOException, NullPointerException, ClassNotFoundException {
        ClientChatApp c = new ClientChatApp();
        do{
            clearScreen();
        }while(!choosePort());

        clientSocket = new ClientSocket(port);
        String option;
        c.start();
        while (true){
            displayMainMenu();
            option = readLine();
            if (option.equals("0"))
                break;
            if (!isLogged){
                switch (option) {
                    case "1": {
                        login();
                        break;
                    }
                    case "2": {
                        register();
                        break;
                    }
                    default:
                        break;
                }
            }else {
                switch (option) {
                    case "1": {
                        testConnection();
                        break;
                    }
                    case "2": {
                        getUsersList(false);
                        break;
                    }
                    case "3": {
                        getUsersList(true);
                        break;
                    }
                    case "4": {
                        findUser(false);
                        break;
                    }
                    case "5": {
                        findUser(true);
                        break;
                    }
                    case "6": {
                        send();
                        break;
                    }

                    default:
                        break;
                }
            }
        }
        clientSocket.close();
    }

    private static void clearScreen() {
        for (int i=0;i<20;i++)
            System.out.println(" ");
    }

    private static boolean choosePort() throws IOException {
        System.out.print("Please enter port number: ");
        port = Integer.parseInt(readLine());
        return port >= 1000 && port <= 99999;
    }

    private static void displayMainMenu(){
        clearScreen();
        System.out.println("\n========Cipher Chat==========");
        if(!isLogged) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
        }else{
            System.out.println("1. Test connection with server");
            System.out.println("2. Show all users");
            System.out.println("3. Show online users");
            System.out.println("4. Find user");
            System.out.println("5. Find user (online)");
            System.out.println("6. Write a message");
            System.out.println("0. Exit");
        }
    }

    private static String readLine() throws IOException {
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    private static void login() throws IOException, ClassNotFoundException {
        String login, password;
        clearScreen();
        System.out.print("Login: ");
        login = readLine();
        nick = login;
        System.out.print("Password: ");
        password = readLine();
        json = new JsonMessage("LOGIN",login,password);
        out.writeObject(encoder.encode(json.toString().trim()));


    }

    private static void register() throws IOException {
        String login, password, password2;
        clearScreen();
        System.out.print("Login: ");
        login = readLine();
        System.out.print("Password: ");
        password = readLine();
        System.out.print("Repeat password: ");
        password2 = readLine();
        json = new JsonMessage("REG",login,password,password2);
        out.writeObject(encoder.encode(json.toString().trim()));
    }

    private static void testConnection() throws IOException {
        json = new JsonMessage("PONG",nick);
        out.writeObject(encoder.encode(json.toString().trim()));
    }

    private static void getUsersList(boolean online) throws IOException {
        json = new JsonMessage("LIST",nick,String.valueOf(online));
        out.writeObject(encoder.encode(json.toString().trim()));
        isOnline = online;
    }

    private static void findUser(boolean online) throws IOException {
        System.out.print("Write a nickname / part a nickname: ");
        String user = readLine();
        json = new JsonMessage("FIND",nick,user,String.valueOf(online));
        out.writeObject(encoder.encode(json.toString().trim()));
        isOnline = online;
    }

    private static void send() throws IOException {
        System.out.print("Write a nickname / part a nickname: ");
        String user = readLine();
        System.out.println("Write '0' in message to quit message mode.");
        while (true) {
            String msg = readLine();
            if (msg.equals("0"))
                break;
            json = new JsonMessage("TEXT", nick, user, msg);
            out.writeObject(encoder.encode(json.toString().trim()));
            alreadySentMessage = true;
        }
    }

    public void run(){
        try {
//            InetAddress host = InetAddress.getByAddress("192.186.1.105");
            socket = new Socket("192.168.1.105",port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Initialization failed");
            return;
        }

        while (true) {
            try {
                json = new JsonMessage(encoder.decode((String) in.readObject()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            getMsgFromServer(json);
        }
    }

    private static void getMsgFromServer(JsonMessage jsonMessage){
        switch (jsonMessage.getP0()){
            case "LOGIN":{
                if (Boolean.parseBoolean(jsonMessage.getP1()))
                    isLogged = true;
                System.out.println(jsonMessage.getP2());
                continueMsg();
                break;
            }
            case "REG":{
                System.out.println(jsonMessage.getP2());
                continueMsg();
                break;
            }
            case "FIND":{
                if (jsonMessage.getP1().equals("[]")) {
                    message = "No found any users";
                    if (isOnline)
                        message = "No found any online users";
                }else {
                    if (isOnline)
                        message = "Found online users:";
                    else
                        message = "Found users:";
                }
                System.out.println(message);
                String usersString = jsonMessage.getP1();
                usersString = usersString.replace('[',Character.MIN_VALUE);
                usersString = usersString.replace(']',Character.MIN_VALUE);
                usersString = usersString.replace('\"',Character.MIN_VALUE);
                users = Arrays.asList(usersString.split(","));
                isOnline = false;
                if (users.size() > 0){
                    for (String user : users)
                        System.out.println(user);
                }
                continueMsg();
                break;
            }
            case "LIST":{
                if (jsonMessage.getP1().equals("[]")) {
                    message = "There are no any users";
                    if (isOnline)
                        message += " online";
                }else {
                    if (isOnline)
                        message = "Online users:";
                    else
                        message = "Users:";
                }
                System.out.println(message);
                String usersString = jsonMessage.getP1();
                usersString = usersString.replace('[',Character.MIN_VALUE);
                usersString = usersString.replace(']',Character.MIN_VALUE);
                usersString = usersString.replace('\"',Character.MIN_VALUE);
                users = Arrays.asList(usersString.split(","));
                isOnline = false;
                if (users.size() > 0){
                    for (String user : users)
                        System.out.println(user);
                }
                continueMsg();
                break;
            }
            case "TEXT":{
                if (jsonMessage.getP1().equals("false"))
                    message = jsonMessage.getP2();
                else if (!jsonMessage.getP1().equals("true")){
                    message = "From '" + jsonMessage.getP1() + "': " + jsonMessage.getP3();
                }
                if (!alreadySentMessage)
                    System.out.println(message);
                alreadySentMessage=false;
                break;
            }
            case "PING":{
                if (Boolean.parseBoolean(jsonMessage.getP1()))
                    message = "Test passed!";
                else
                    message = jsonMessage.getP2();
                System.out.println(message);
                continueMsg();
                break;
            }
        }

    }

    private static void continueMsg(){
        System.out.println("Press ENTER to continue...");
    }

}


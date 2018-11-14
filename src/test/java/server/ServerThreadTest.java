package server;

import db.UserContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServerThreadTest {

    private ServerThread serverThread;
    private ServerLogic serverLogic;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        serverLogic = new ServerLogic(new UserContext("chat.db"), 15000);
        serverThread = new ServerThread(serverLogic);
    }


    @Test
    void testRun() {
        serverThread.start();
        char c = 0;
        /*do {
            try {
                System.out.println("Wprowadz 'q'");
                c = (char) System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(c != 'q');*/
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverThread.setWork(false);
        assertTrue(true);
    }
}
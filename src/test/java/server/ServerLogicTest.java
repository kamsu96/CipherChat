package server;

import db.UserContext;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServerLogicTest {

    private static ServerLogic serverLogic;
    private static ClientSocket cs1;
    private static ClientSocket cs2;

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        UserContext userContext = new UserContext("chat.db");
        userContext.removeAllUsers();
        serverLogic = new ServerLogic(userContext, 15000);
        serverLogic.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cs1 = new ClientSocket(15000);
        cs2 = new ClientSocket(15000);
    }

    @ParameterizedTest
    @MethodSource("parametersForTestCommunication")
    void testCommunication(String socketName, String inputMsg, String outputMsg) {
        ClientSocket cs = socketName.equals("cs1") ? cs1 : cs2;
        cs.sendMessage(inputMsg);
        String str = cs.receiveMessage();
        System.out.println(str);

        outputMsg = outputMsg.replace(" ", "").replace("\n", "");
        str = str.replace(" ", "").replace("\n", "");

        assertEquals(outputMsg, str);
    }

    @ParameterizedTest
    @MethodSource("parametersForTestTextCommunication")
    void testTextCommunication(String inputMsg, String outputMsg) {
        cs1.sendMessage(inputMsg);
        String str1 = cs1.receiveMessage();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //cs2.sendMessage(inputMsg2);
        String str2 = cs2.receiveMessage();

        inputMsg = inputMsg.replace(" ", "").replace("\n", "");
        outputMsg = outputMsg.replace(" ", "").replace("\n", "");
        str1 = str1.replace(" ", "").replace("\n", "");
        str2 = str2.replace(" ", "").replace("\n", "");

        assertEquals(outputMsg, str1);
        assertEquals(inputMsg, str2);
    }

    private static Stream<String[]> parametersForTestCommunication() {
        return Stream.of(new String[]{"cs1",
                new String("{  \n" +
                "   \"P0\":\"REG\",\n" +
                "   \"P1\":\"user1\",\n" +
                "   \"P2\":\"pass\",\n" +
                "   \"P3\":\"pass1\"\n" +
                "}"),
                new String("{  \n" +
                        "   \"P0\":\"REG\",\n" +
                        "   \"P1\":\"false\",\n" +
                        "   \"P2\":\"Passwords don't match.\"\n" +
                        "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                        "   \"P0\":\"REG\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"pass\",\n" +
                        "   \"P3\":\"pass\"\n" +
                        "}"),
                        new String("{  \n" +
                                "   \"P0\":\"REG\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Registration succeeded.\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                        "   \"P0\":\"REG\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"pass\",\n" +
                        "   \"P3\":\"pass\"\n" +
                        "}"),
                        new String("{  \n" +
                                "   \"P0\":\"REG\",\n" +
                                "   \"P1\":\"false\",\n" +
                                "   \"P2\":\"Login is already in the database.\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                        "   \"P0\":\"REG\",\n" +
                        "   \"P1\":\"user2\",\n" +
                        "   \"P2\":\"pass\",\n" +
                        "   \"P3\":\"pass\"\n" +
                        "}"),
                        new String("{  \n" +
                                "   \"P0\":\"REG\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Registration succeeded.\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                                "   \"P0\":\"LOGIN\",\n" +
                                "   \"P1\":\"user1\",\n" +
                                "   \"P2\":\"pass\",\n" +
                                "   \"P3\":\"\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"LOGIN\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Login succeeded.\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                        "   \"P0\":\"LOGIN\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"aaa\",\n" +
                        "   \"P3\":\"\"\n" +
                        "}"),
                        new String("{  \n" +
                                "   \"P0\":\"LOGIN\",\n" +
                                "   \"P1\":\"false\",\n" +
                                "   \"P2\":\"Login or password is incorrect.\"\n" +
                                "}")},

        new String[]{"cs2",
                new String("{  \n" +
                "   \"P0\":\"LOGIN\",\n" +
                "   \"P1\":\"user2\",\n" +
                "   \"P2\":\"pass\",\n" +
                "   \"P3\":\"\"\n" +
                "}"),
                new String("{  \n" +
                        "   \"P0\":\"LOGIN\",\n" +
                        "   \"P1\":\"true\",\n" +
                        "   \"P2\":\"Login succeeded.\"\n" +
                        "}")},

        new String[]{"cs1",
                new String("{  \n" +
                        "   \"P0\":\"LIST\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"true\",\n" +
                        "   \"P3\":\"\"\n" +
                        "}"),
                new String("{  \n" +
                        "   \"P0\":\"LIST\",\n" +
                        "   \"P1\":\"[\\\"user1\\\", \\\"user2\\\"]\"\n" +
                        "}")},

        new String[]{"cs2",
                new String("{  \n" +
                        "   \"P0\":\"FIND\",\n" +
                        "   \"P1\":\"user2\",\n" +
                        "   \"P2\":\"us\",\n" +
                        "   \"P3\":\"true\"\n" +
                        "}"),
                new String("{  \n" +
                        "   \"P0\":\"FIND\",\n" +
                        "   \"P1\":\"[\\\"user1\\\", \\\"user2\\\"]\"\n" +
                        "}")},

                new String[]{"cs2",
                        new String("{  \n" +
                                "   \"P0\":\"FIND\",\n" +
                                "   \"P1\":\"user2\",\n" +
                                "   \"P2\":\"us\",\n" +
                                "   \"P3\":\"false\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"FIND\",\n" +
                                "   \"P1\":\"[\\\"user1\\\", \\\"user2\\\"]\"\n" +
                                "}")},

              new String[]{"cs2",
                        new String("{  \n" +
                                "   \"P0\":\"FIND\",\n" +
                                "   \"P1\":\"user2\",\n" +
                                "   \"P2\":\"user1\",\n" +
                                "   \"P3\":\"true\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"FIND\",\n" +
                                "   \"P1\":\"[\\\"user1\\\"]\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                                "   \"P0\":\"LOGOUT\",\n" +
                                "   \"P1\":\"user1\",\n" +
                                "   \"P2\":\"\",\n" +
                                "   \"P3\":\"\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"LOGOUT\",\n" +
                                "   \"P1\":\"true\"\n" +
                                "}")},

                new String[]{"cs1",
                        new String("{  \n" +
                                "   \"P0\":\"LOGIN\",\n" +
                                "   \"P1\":\"user1\",\n" +
                                "   \"P2\":\"pass\",\n" +
                                "   \"P3\":\"\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"LOGIN\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Login succeeded.\"\n" +
                                "}")}
        );
    }

    private static Stream<String[]> parametersForTestTextCommunication() {
        return Stream.of(
                new String[]{new String("{  \n" +
                "   \"P0\":\"TEXT\",\n" +
                "   \"P1\":\"user1\",\n" +
                "   \"P2\":\"user2\",\n" +
                "   \"P3\":\"content1\"\n" +
                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"TEXT\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Succeeded.\"\n" +
                                "}")},
                new String[]{
                        new String("{  \n" +
                                "   \"P0\":\"TEXT\",\n" +
                                "   \"P1\":\"user1\",\n" +
                                "   \"P2\":\"user2\",\n" +
                                "   \"P3\":\"content2\"\n" +
                                "}"),
                        new String("{  \n" +
                                "   \"P0\":\"TEXT\",\n" +
                                "   \"P1\":\"true\",\n" +
                                "   \"P2\":\"Succeeded.\"\n" +
                                "}")}
                        );
    }
}
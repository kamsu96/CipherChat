package jsonParser;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonMessageTest {

    @ParameterizedTest
    @MethodSource("parametersForTestToString")
    public void testToString(String message) {
        JsonMessage jsonMessage = new JsonMessage(message);
        String retMessage = jsonMessage.toString();
        message = message.replace("\n", "");
        message = message.replace(" ", "");
        retMessage = retMessage.replace("\n", "");
        retMessage = retMessage.replace(" ", "");
        System.out.println(message);
        System.out.println(retMessage);
        assertEquals(retMessage, message);
    }

    private static Stream<String> parametersForTestToString() {
        return Stream.of(new String("{  \n" +
                "   \"P0\":\"REG\",\n" +
                "   \"P1\":\"user1\",\n" +
                "   \"P2\":\"pass\",\n" +
                "   \"P3\":\"pass\"\n" +
                "}"),
                "{  \n" +
                        "   \"P0\":\"LOGIN\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"pass\",\n" +
                        "   \"P3\":\"\"\n" +
                        "}",
                "{  \n" +
                        "   \"P0\":\"LIST\",\n" +
                        "   \"P1\":\"user1\",\n" +
                        "   \"P2\":\"\",\n" +
                        "   \"P3\":\"\"\n" +
                        "}");
    }

//    Object[] parametersForTestToString() {
//        return new Object[]{
//                new Object[]{new String("{  \n" +
//                        "   \"P0\":\"REG\",\n" +
//                        "   \"P1\":\"user1\",\n" +
//                        "   \"P2\":\"pass\",\n" +
//                        "   \"P3\":\"pass\"\n" +
//                        "}")},
//                new Object[]{new String("{  \n" +
//                        "   \"P0\":\"LOGIN\",\n" +
//                        "   \"P1\":\"user1\",\n" +
//                        "   \"P2\":\"pass\",\n" +
//                        "   \"P3\":\"\"\n" +
//                        "}")},
//                new Object[]{new String("{  \n" +
//                        "   \"P0\":\"LIST\",\n" +
//                        "   \"P1\":\"user1\",\n" +
//                        "   \"P2\":\"\",\n" +
//                        "   \"P3\":\"\"\n" +
//                        "}")}
//        };
//    }
}
package jsonParser;

import server.ServerLogic;
import org.json.*;

import java.net.Socket;

import static jsonParser.MessageType.*;

/**
 * @author Łukasz Sus
 */
public class JsonMessage {

    MessageType msgType = UNKNOWN;
    String p0 = null;
    String p1 = null;
    String p2 = null;
    String p3 = null;

    public MessageType getMsgType() {
        return msgType;
    }

    public String getP0() {
        return p0;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getP3() {
        return p3;
    }

    public JsonMessage(String message){
        JSONObject obj = new JSONObject(message);
        this.p0 = obj.has("P0") ? obj.getString("P0") : null;
        this.p1 = obj.has("P1") ? obj.getString("P1") : null;
        this.p2 = obj.has("P2") ? obj.getString("P2") : null;
        this.p3 = obj.has("P3") ? obj.getString("P3") : null;
        setMessageType();
    }

    public JsonMessage(String p0, String p1, String p2, String p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        setMessageType();
    }

    public JsonMessage(String p0, String p1, String p2) {
        this(p0, p1, p2, null);
    }

    public JsonMessage(String p0, String p1) {
        this(p0, p1, null, null);
        setMessageType();
    }

    public JsonMessage(MessageType msgType, String p1, String p2, String p3) {
        this.msgType = msgType;
        this.p0 = msgType.toString();
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public JsonMessage(MessageType msgType, String p1, String p2) {
        this(msgType, p1, p2, null);
    }

    public JsonMessage(MessageType msgType, String p1){
        this(msgType, p1, null, null);
    }

    public JsonMessage doAction(ServerLogic serverLogic, Socket socket) {
        switch(msgType) {
            case REG:
                return serverLogic.regResponse(p1, p2, p3);
            case LOGIN:
                return serverLogic.loginResponse(socket, p1, p2);
            case TEXT:
                return serverLogic.textResponse(socket, this);
            case LIST:
                return serverLogic.listResponse(socket, p1, p2.equals("true") || p2.equals("t") || p2.equals("1"));
            case FIND:
                return serverLogic.findResponse(socket, p1, p2, p3.equals("true") || p3.equals("t") || p3.equals("1"));
            case LOGOUT:
                return serverLogic.logoutResponse(socket, p1);
/*            case LOAD:
                return serverLogic.loadResponse(socket, p1);*/
            case PONG:
                return serverLogic.pongResponse(socket, p1);
            default:
                System.out.println("Message type is unknown.");
                return new JsonMessage(MessageType.UNKNOWN, null, null, null);
        }
    }

    public JSONObject toJsonObject(){
        JSONObject obj = new JSONObject();

        obj.put("P0", p0);
        obj.put("P1", p1);
        obj.put("P2", p2);
        obj.put("P3", p3);

        return obj;
    }

    public String toString(){
        JSONObject obj = new JSONObject();

        obj.put("P0", p0);
        obj.put("P1", p1);
        obj.put("P2", p2);
        obj.put("P3", p3);

        return obj.toString();
    }

    private void setMessageType(){
        String messageType = this.p0;
        switch(messageType){
            case "REG":
                msgType = REG;
                break;
            case "LOGIN":
                msgType = LOGIN;
                break;
            case "TEXT":
                msgType = TEXT;
                break;
            case "LIST":
                msgType = LIST;
                break;
            case "FIND":
                msgType = FIND;
                break;
            case "LOGOUT":
                msgType = LOGOUT;
                break;
/*            case "LOAD":
                msgType = LOAD;
                break;*/
            case "PONG":
                msgType = PONG;
                break;
            case "PING":
                msgType = PING;
                break;
            default:
                throw new IllegalArgumentException("Protocol doesn't define this type of message.");
        }
    }
}

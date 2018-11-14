package jsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Łukasz Sus
 */
public final class JsonMessageFactory {

    /**
     * Should not be initialized.
     */
    private JsonMessageFactory(){
    }
/*
    public static JsonMessage createLoadMsg(MessageType msgType, List<JsonMessage> list){
        if(msgType.equals(MessageType.LOAD)){
            return createLoadMsg(msgType, list);
        }
        else{
            return createListMsg(msgType, list);
        }
    }*/

/*    public static JsonMessage createLoadMsg(MessageType msgType, List<JsonMessage> list) {
        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        try
        {
            for (JsonMessage msg: list)
            {
                jArray.put(msg.toJsonObject());
            }
            return new JsonMessage(msgType, jArray.toString());
        } catch (JSONException jse) {
        }
        return new JsonMessage(msgType, jArray.toString());
    }*/

    public static JsonMessage createListMsg(MessageType msgType, List<String> list){
        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        try
        {
            for (String s: list)
            {
                jArray.put(s);
            }
            return new JsonMessage(msgType, jArray.toString());
        } catch (JSONException jse) {
        }
        return new JsonMessage(msgType, jArray.toString());
    }

    public static JsonMessage createPing(String login){
        return new JsonMessage(MessageType.PING, login);
    }
}

package server;

import jsonParser.JsonMessageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Łukasz Sus
 */
public class ConnectionRefresher extends Thread {

    private static final int FRESH_VALUE = 5;

    private Map<String, Integer> clientsActivity = new HashMap<>();
    private boolean work;
    private ServerLogic serverLogic;

    public ConnectionRefresher(ServerLogic serverLogic){
        this.serverLogic = serverLogic;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void run(){
        work = true;
        while (work){
            removeInactive();
            sendPings();
            decreaseActiveValues();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeInactive(){
        List<String> toRemove = new ArrayList<>();
        for(String login : clientsActivity.keySet()){
            int val = clientsActivity.get(login);
            if(val == 0) {
                toRemove.add(login);
            }
        }
        for(String s: toRemove){
            clientsActivity.remove(s);
            serverLogic.remove(s);
        }
    }

    private void sendPings(){
        for(String client : clientsActivity.keySet()){
            serverLogic.sendMessage(JsonMessageFactory.createPing(client));
        }
    }

    private void decreaseActiveValues(){
        for(String login : clientsActivity.keySet()){
            clientsActivity.replace(login, clientsActivity.get(login) - 1);
        }
    }

    public void refreshActiveValue(String login){
        if(clientsActivity.containsKey(login)){
            clientsActivity.replace(login, FRESH_VALUE);
        }else {
            clientsActivity.put(login, FRESH_VALUE);
        }
    }
}

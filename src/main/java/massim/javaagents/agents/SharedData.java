package massim.javaagents.agents;

import eis.iilang.Action;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SharedData {
    private static SharedData instance = null;

    private SharedData() {}

    public static SharedData getSharedData() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    private Map<String, Queue<Action>> actions = new HashMap<>();

    public Queue<Action> getMyActions(String key){
        return actions.get(key);
    }
}

package massim.javaagents.agents;

import eis.iilang.Action;

import java.util.*;

public class SharedData {
    private static SharedData instance = null;

    private SharedData() {
    }

    public static SharedData getSharedData() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    private Map<String, Queue<ArrayList<String>>> actions = new HashMap<>();

    public void initActions(ArrayList<String> agents) {
        for (String a : agents) {
            actions.put(a, new LinkedList<>());
        }
    }

    public void addNewAction(String agent, ArrayList<String> action) {
        actions.get(agent).add(action);
    }

    public Queue<ArrayList<String>> getMyActions(String key) {
        return actions.get(key);
    }
}

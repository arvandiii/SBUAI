package massim.javaagents.agents;

import eis.iilang.Action;
import massim.javaagents.percept.Pair;
import massim.javaagents.percept.job;
import massim.javaagents.percept.resourceNode;
import massim.javaagents.percept.role;

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
    private HashSet<resourceNode> resourceNodes = new HashSet<>();

    public void addNewResourceNode(resourceNode resource) {
        resourceNodes.add(resource);
    }

    public HashSet<resourceNode> getResourceNodes() {
        return resourceNodes;
    }

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

    private Map<String, job> jobs = new HashMap<>();

    public void takeJob(job j) {
        jobs.put(j.getJobID(), j);
    }

    public boolean isJobTaken(job j) {
        if (jobs.get(j.getJobID()) == null) {
            return false;
        }
        return true;
    }

    private LinkedList<Pair<String, role>> roles = new LinkedList<>();

    public void addMyRole(Pair<String, role> myRole) {
        roles.add(myRole);
    }

    public role getRole(String myName) {
        for (Pair<String, role> nr : roles) {
            if (nr.getLeft().equals(myName)) {
                return nr.getRight();
            }
        }
        return null;
    }

    public ArrayList<Object> getItemSources(String itemName){
        // array [string (shop || resourceNode), Double lat, Double lon, Integer Volume]
        // shop ha va resourceNode ha ro begard vase in item
        // todo
        return  null;
    }
}

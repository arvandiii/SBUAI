package massim.javaagents.agents;

import massim.javaagents.percept.*;

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
    private HashSet<shop> shops = new HashSet<>();

    public void addAllShops(List<shop> shops) {
        this.shops.addAll(shops);
    }

    public HashSet<shop> getShops() {
        return shops;
    }


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

    private HashMap<String, role> roles = new HashMap<>();

    public void addMyRole(String myName, role myRole) {
        roles.put(myName, myRole);
    }

    public role getRole(String myName) {
        return roles.get(myName);
    }

    public ArrayList<ArrayList<Object>> getItemSources(String itemName) {
        // array [string (shop || resourceNode), Double lat, Double lon, Integer Volume]
        // shop ha va resourceNode ha ro begard vase in item

        System.out.println("SOUUUUUT SIZE OF SHOPSSSSSSSS:" + shops.size());
        System.out.println("SOUUUUUT SIZE OF ResourceNodesssss:" + resourceNodes.size());

        ArrayList<ArrayList<Object>> sources = new ArrayList<>();


        for (resourceNode r : resourceNodes) {
            if (r.getResource().equals(itemName)) {
                ArrayList<Object> result = new ArrayList<>();
                result.add("resourceNode");
                result.add(r.getLat());
                result.add(r.getLon());
                result.add(-1);
                sources.add(result);
            }
        }

        for (shop s : shops) {
            shopItem item = s.getShopItemsMap().get(itemName);
            if (item != null) {
                ArrayList<Object> result = new ArrayList<>();
                result.add("shop");
                result.add(s.getShopLat());
                result.add(s.getShopLon());
                result.add(item.getAmount());
                sources.add(result);
            }
        }


        return sources;
    }
}

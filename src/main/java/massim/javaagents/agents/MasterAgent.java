package massim.javaagents.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.percept.*;

import java.util.*;


public class MasterAgent extends Agent {


    boolean oneDroneIsSearching = false;

    AgentPercepts AP = new AgentPercepts();
    ArrayList<entity> agents = new ArrayList<>();

    public MasterAgent(String name, MailService mailbox) {
        super(name, mailbox);
    }

    @Override
    public void handlePercept(Percept percept) {
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }

    @Override
    public Action step() {
        makePerceptObjects(AP);
        SharedData sharedData = SharedData.getSharedData();


        if (getStepNumber() == 1) {
            sharedData.addMyRole(AP.getSelfInfo().getName(), AP.getSelfRole());
            sharedData.addAllShops(AP.getShops());

            for (int i = 0; i < AP.getEntities().size(); i++) {
                entity e = AP.getEntities().get(i);
                if (e.getTeam().equals(AP.getSelfInfo().getTeam())) {
                    System.out.println(e.getName() + e.getRole());
                    agents.add(e);
                }
            }

            sharedData.initActions(agents);

            ArrayList<String> research = new ArrayList<>();
            research.add("research");

            for (entity a : agents) {
                if (a.getRole().equals("drone")) {
                    sharedData.addNewAction(a.getName(), research);
                    break;
                }
            }
        }

        if (getStepNumber() > 1) {

            // if resourceNode is seen save it in shared data
            if (AP.getResourceNodes().size() != 0) {
                for (resourceNode r : AP.getResourceNodes()) {
                    sharedData.addNewResourceNode(r);
                }
                System.out.println("!!!! RESOURCE NODE FOUND BY MASTER !!!!");
            }

            List<job> jobs = AP.getJobs();

            LinkedList<job> notTakenJobs = new LinkedList<>();
            for (job j : jobs) {
                if (!sharedData.isJobTaken(j)) {
                    notTakenJobs.add(j);
                }
            }

            System.out.println(notTakenJobs);

            // [job, evaluation, [<itemName, volume>]]

            ArrayList<ArrayList<Object>> evaluatedJobs = new ArrayList<>();

            for (job j : notTakenJobs) {
                int end = j.getJobEnd();
                int reward = j.getJobReward();
                int volume = 0;
                List<Pair<String, Integer>> requireds = j.getJobRequireds();
                ArrayList<Pair<String, Integer>> itemWithVolume = new ArrayList<>();
                for (int i = 0; i < requireds.size(); i++) {
                    for (int k = 0; k < AP.getItemsInEnv().size(); k++) {
                        if (AP.getItemsInEnv().get(k).getName().equals(requireds.get(i).getLeft())) {
                            int size = requireds.get(i).getRight() * AP.getItemsInEnv().get(k).getVolume();
                            volume += size;
                            itemWithVolume.add(new Pair<>(requireds.get(i).getLeft(), size));
                        }
                    }
                }
                int A = 1;
                int B = 10;
                int C = 15;
                int valuation = A * end + B * reward - C * volume;
                System.out.println(j.getJobID() + "\t" + end + "\t" + reward + "\t" + volume + "\t" + valuation);
                ArrayList<Object> ej = new ArrayList<>();
                ej.add(j);
                ej.add(valuation);
                ej.add(itemWithVolume);
                evaluatedJobs.add(ej);
            }


            for (ArrayList<Object> ej : evaluatedJobs) {
                ArrayList<Pair<String, ArrayList<ArrayList<Object>>>> items = new ArrayList<>();
                for (Pair<String, Integer> item : (ArrayList<Pair>) ej.get(2)) {
                    ArrayList<ArrayList<Object>> sources = sharedData.getItemSources(item.getLeft());
                    items.add(new Pair<>(item.getLeft(), sources));
                }

                if (items.size() == 1) {
                    for (entity a : agents) {
                        if (sharedData.getMyActions(a.getName()).size() == 0) {
                            role r = sharedData.getRole(a.getName());
                            int load = r.getLoad();
                            int speed = r.getSpeed();

                            double minDist = Double.MAX_VALUE;
                            ArrayList<Object> best = null;
                            String itemName = items.get(0).getLeft();
                            for (ArrayList<Object> i : items.get(0).getRight()) {
                                double dist = CustomUtils.distance(a.getLat(), a.getLon(),
                                        (Double) i.get(1), (Double) i.get(2), 'K');
                                if (dist < minDist && load >= (Integer) i.get(3)) {
                                    minDist = dist;
                                    best = i;
                                }
                            }
                            if (best != null) {
                                if (best.get(0).equals("resourceNode")) {
                                    ArrayList<String> gotoResourceNodeAction = new ArrayList<>();
                                    gotoResourceNodeAction.add("goto");
                                    gotoResourceNodeAction.add(best.get(1) + "");
                                    gotoResourceNodeAction.add(best.get(2) + "");
                                    sharedData.addNewAction(a.getName(), gotoResourceNodeAction);
                                    ArrayList<String> gatherAction = new ArrayList<>();
                                    gatherAction.add("gather");
                                    sharedData.addNewAction(a.getName(), gatherAction);
                                    ArrayList<String> gotoStorageAction = new ArrayList<>();
                                    gotoStorageAction.add("goto");
                                    sharedData.addNewAction(a.getName(), gotoStorageAction);
                                    ArrayList<String> deliverJob = new ArrayList<>();
                                    deliverJob.add("deliver_job");
                                    deliverJob.add(((job) ej.get(0)).getJobID());
                                    sharedData.addNewAction(a.getName(), deliverJob);
                                    sharedData.takeJob((job) ej.get(0));
                                    break;
                                } else if (best.get(0).equals("shop")) {
                                    ArrayList<String> gotoResourceNodeAction = new ArrayList<>();
                                    gotoResourceNodeAction.add("goto");
                                    gotoResourceNodeAction.add(best.get(1) + "");
                                    gotoResourceNodeAction.add(best.get(2) + "");
                                    sharedData.addNewAction(a.getName(), gotoResourceNodeAction);
                                    ArrayList<String> buyAction = new ArrayList<>();
                                    buyAction.add("buy");
                                    buyAction.add(itemName);
                                    buyAction.add(((job) ej.get(0)).getJobRequireds().get(0).getRight() + "");
                                    buyAction.add(((Pair) ((ArrayList) ej.get(2)).get(0)).getRight() + "");
                                    sharedData.addNewAction(a.getName(), buyAction);
                                    ArrayList<String> gotoStorageAction = new ArrayList<>();
                                    gotoStorageAction.add("goto");
                                    String storageName = ((job) ej.get(0)).getJobStorage();
                                    for (storage s :
                                            AP.getStorages()) {
                                        if (s.getName().equals(storageName)) {
                                            gotoStorageAction.add(s.getLat() + "");
                                            gotoStorageAction.add(s.getLon() + "");
                                            break;
                                        }
                                    }
                                    sharedData.addNewAction(a.getName(), gotoStorageAction);
                                    ArrayList<String> deliverJob = new ArrayList<>();
                                    deliverJob.add("deliver_job");
                                    deliverJob.add(((job) ej.get(0)).getJobID());
                                    sharedData.addNewAction(a.getName(), deliverJob);
                                    sharedData.takeJob((job) ej.get(0));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Action("skip");
    }
}
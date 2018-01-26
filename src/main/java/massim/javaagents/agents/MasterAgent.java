package massim.javaagents.agents;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.percept.*;

import java.util.*;


public class MasterAgent extends Agent {


    boolean oneDroneIsSearching = false;

    AgentPercepts AP = new AgentPercepts();

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


        if (getStepNumber() >= 1) {
            ArrayList<String> agents = new ArrayList<>();

            for (int i = 0; i < AP.getEntities().size(); i++) {
                entity e = AP.getEntities().get(i);
                if (e.getTeam().equals(AP.getSelfInfo().getTeam())) {
                    System.out.println(e.getName() + e.getRole());
                    agents.add(e.getName());
                }
            }

            sharedData.initActions(agents);

            shop shop0 = AP.getShops().get(0);
            shop shop1 = AP.getShops().get(1);

            ArrayList<String> research = new ArrayList<>();
            research.add("research");

            ArrayList<String> firstGoto = new ArrayList<>();
            firstGoto.add("goto");
            firstGoto.add(shop0.getShopLat() + "");
            firstGoto.add(shop0.getShopLon() + "");

            ArrayList<String> secondGoto = new ArrayList<>();
            secondGoto.add("goto");
            secondGoto.add(shop1.getShopLat() + "");
            secondGoto.add(shop1.getShopLon() + "");


            for (String a : agents) {
                switch (a) {
                    case "drone":
                        if (!oneDroneIsSearching) {
                            sharedData.addNewAction(a, research);
                            oneDroneIsSearching = true;
                            break;
                        }
                    default:
                        sharedData.addNewAction(a, firstGoto);
                        sharedData.addNewAction(a, secondGoto);
                }


            }
            System.out.println("im master");

        }

        if (getStepNumber() > 1) {

            ArrayList<entity> agents = new ArrayList<>();

            for (int i = 0; i < AP.getEntities().size(); i++) {
                entity e = AP.getEntities().get(i);
                if (e.getTeam().equals(AP.getSelfInfo().getTeam())) {
                    agents.add(e);
                }
            }


            List<job> jobs = AP.getJobs(); // find jobs

            LinkedList<job> notTakenJobs = new LinkedList<>();
            for (job j : jobs) {
                if (!sharedData.isJobTaken(j)) {
                    notTakenJobs.add(j);
                }
            }

            System.out.println(notTakenJobs);

            LinkedList<Pair<job, Pair<Integer, LinkedList<Pair<String, Integer>>>>> evaluatedJobs = new LinkedList<>();

            for (job j : notTakenJobs) {
                int end = j.getJobEnd();
                int reward = j.getJobReward();
                int volume = 0;
                List<Pair<String, Integer>> requireds = j.getJobRequireds();
                LinkedList<Pair<String, Integer>> itemWithVolume = new LinkedList<>();
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
                evaluatedJobs.add(new Pair<>(j, new Pair<>(valuation, itemWithVolume)));
            }

            for (Pair<job, Pair<Integer, LinkedList<Pair<String, Integer>>>> ej : evaluatedJobs) {
                ArrayList<Pair<String, ArrayList<Object>>> items = new ArrayList<>();
                for (Pair<String, Integer> item : ej.getRight().getRight()) {
                    ArrayList<Object> sources = sharedData.getItemSources(item.getLeft());
                    items.add(new Pair<>(item.getLeft(), sources));
                }


                for (entity a : agents) {
                    if (sharedData.getMyActions(a.getName()).size() == 0) {
                        role r = sharedData.getRole(a.getName());
                        int load = r.getLoad();
                        int speed = r.getSpeed();

                        double minDist = Double.MAX_VALUE;
                        Pair<String, ArrayList<Object>> best = null;
                        for (Pair<String, ArrayList<Object>> i : items) {
                            double dist = CustomUtils.distance(a.getLat(), a.getLon(),
                                    (Double) i.getRight().get(1), (Double) i.getRight().get(2), 'K');
                            if (dist < minDist && load >= (Integer) i.getRight().get(3)) {
                                minDist = dist;
                                best = i;
                            }
                        }
                        if (best != null) {
                            if (best.getRight().get(0).equals("resourceNode")) {
                                ArrayList<String> gotoResourceNodeAction = new ArrayList<>();
                                gotoResourceNodeAction.add("goto");
                                gotoResourceNodeAction.add(best.getRight().get(1) + "");
                                gotoResourceNodeAction.add(best.getRight().get(2) + "");
                                sharedData.addNewAction(a.getName(), gotoResourceNodeAction);
                                ArrayList<String> gatherAction = new ArrayList<>();
                                gatherAction.add("gather");
                                sharedData.addNewAction(a.getName(), gatherAction);
                                ArrayList<String> gotoStorageAction = new ArrayList<>();
                                gotoStorageAction.add("goto");
                                sharedData.addNewAction(a.getName(), gotoStorageAction);
                                ArrayList<String> deliverJob = new ArrayList<>();
                                deliverJob.add("deliver_job");
                                deliverJob.add(ej.getLeft().getJobID());
                                sharedData.addNewAction(a.getName(), deliverJob);
                                sharedData.takeJob(ej.getLeft());
                                break;
                            } else if (best.getRight().get(0).equals("shop")) {
                                ArrayList<String> gotoResourceNodeAction = new ArrayList<>();
                                gotoResourceNodeAction.add("goto");
                                gotoResourceNodeAction.add(best.getRight().get(1) + "");
                                gotoResourceNodeAction.add(best.getRight().get(2) + "");
                                sharedData.addNewAction(a.getName(), gotoResourceNodeAction);
                                ArrayList<String> buyAction = new ArrayList<>();
                                buyAction.add("buy");
                                buyAction.add(best.getLeft());
                                buyAction.add(best.getRight().get(3) + "");
                                sharedData.addNewAction(a.getName(), buyAction);
                                ArrayList<String> gotoStorageAction = new ArrayList<>();
                                gotoStorageAction.add("goto");
                                sharedData.addNewAction(a.getName(), gotoStorageAction);
                                ArrayList<String> deliverJob = new ArrayList<>();
                                deliverJob.add("deliver_job");
                                deliverJob.add(ej.getLeft().getJobID());
                                sharedData.addNewAction(a.getName(), deliverJob);
                                sharedData.takeJob(ej.getLeft());
                                break;
                            }
                        } else {
                            // todo goto nearest resource node gather and put them in a storage
                        }


                    }
                }
            }

        }


        // for jobs not taken => for agents not doing anything and not drone => find appropriate => assign this job

        // assign job => resource || shop || storage => do best

        // drones only searching and saving resource nodes

        // for agents not doing anything => go to resource nodes and store in storage and save data in shared data

        return new Action("skip");
    }
}
package massim.javaagents.agents;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.percept.*;

import java.util.*;


public class MasterAgent extends Agent {


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


        if (getStepNumber() == 1) {
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
                        sharedData.addNewAction(a, research);
                        break;
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

//            if (notTakenJobs.size() > 0)
//                sharedData.takeJob(notTakenJobs.get(0));

            LinkedList<Pair<job, Integer>> evaluatedJobs = new LinkedList<>();

            for (job j : notTakenJobs) {
                int end = j.getJobEnd();
                int reward = j.getJobReward();
                int volume = 0;
                List<Pair<String, Integer>> requireds = j.getJobRequireds();
                for (int i = 0; i < requireds.size(); i++) {
                    for (int k = 0; k < AP.getItemsInEnv().size(); k++) {
                        if (AP.getItemsInEnv().get(k).getName().equals(requireds.get(i).getLeft())) {
                            volume += requireds.get(i).getRight() * AP.getItemsInEnv().get(k).getVolume();
                        }
                    }
                }
                int A = 1;
                int B = 10;
                int C = 15;
                int valuation = A * end + B * reward - C * volume;
                System.out.println(j.getJobID() + "\t" + end + "\t" + reward + "\t" + volume + "\t" + valuation);
                evaluatedJobs.add(new Pair<>(j, valuation));
            }

            for (entity a : agents) {
                if (!a.getRole().equals("drone") &&
                        sharedData.getMyActions(a.getName()).size() == 0) {
                    // todo assign best job for him
                }
            }

        }


        // evaluate jobs
        // for jobs not taken => for agents not doing anything and not drone => find appropriate => assign this job

        // assign job => resource || shop || storage => do best

        // drones only searching and saving resource nodes

        // for agents not doing anything => go to resource nodes and store in storage and save data in shared data

        // if job assigned to me =>
        // if charge => do it
        // else => if chargeStation || solarCharge => do best
        // else => skip

//        System.out.println(AP.getSelfRole());
//        System.out.println(AP.getSelfInfo().getName() + "\t" + sharedData.DONE);
//        if (getStepNumber() == 1 && !sharedData.DONE) {
//            for (int i = 0; i < AP.getEntities().size(); i++) {
//                entity e = AP.getEntities().get(i);
//                System.out.println(e.getTeam());
//                if (e.getTeam().equals(AP.getSelfInfo().getTeam())) {
//
//                }
//            }
//            sharedData.DONE = true;
//        }

//        System.out.println(AP.getSelfInfo().getCharge());
//        System.out.println(AP.getSelfInfo().getRole());
//        System.out.println(AP.getSelfInfo().getTeam());
//        System.out.println(AP.getSelfInfo().getLat());
//        System.out.println(AP.getSelfInfo().getLon());

//        for (int i = 0; i < percepts.size(); i++) {
//            System.out.println(percepts);
//        }
//        percepts.stream()
//                .filter(p -> p.getName().equals("step"))
//                .findAny()
//                .ifPresent(p -> {
//                    Parameter param = p.getParameters().getFirst();
//                    if(param instanceof Identifier) say("Step " + ((Identifier) param).getValue());
//        });
        return new Action("skip");
    }
}
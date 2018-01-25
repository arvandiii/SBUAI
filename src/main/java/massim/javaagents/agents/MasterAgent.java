package massim.javaagents.agents;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;
import massim.javaagents.percept.entity;
import massim.javaagents.percept.shop;

import java.util.ArrayList;
import java.util.LinkedList;


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
                    agents.add(e.getName());
                }
            }

            sharedData.initActions(agents);

            shop shop0 = AP.getShops().get(0);
            shop shop1 = AP.getShops().get(1);

            ArrayList<String> firstGoto = new ArrayList<>();
            firstGoto.add("goto");
            firstGoto.add(shop0.getShopLat() + "");
            firstGoto.add(shop0.getShopLon() + "");

            ArrayList<String> secondGoto = new ArrayList<>();
            secondGoto.add("goto");
            secondGoto.add(shop1.getShopLat() + "");
            secondGoto.add(shop1.getShopLon() + "");


            for (String a : agents) {
                sharedData.addNewAction(a, firstGoto);
                sharedData.addNewAction(a, secondGoto);
            }
            System.out.println("im master");

        }
        // find jobs
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
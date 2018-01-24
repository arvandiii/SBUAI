package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class BasicAgent extends Agent {

    AgentPercepts AP = new AgentPercepts();

    public BasicAgent(String name, MailService mailbox) {
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


        // if myLastAction is done pop
        // sharedData.getMyAction(myName)
        // return action


        if (getStepNumber() > 0) {
            //        SharedData sharedData = SharedData.getSharedData();
//        Queue<ArrayList<String>> actions = sharedData.getMyActions(AP.getSelfInfo().getName());

//        ArrayList<String> lastAction = actions.peek();
//        if (lastAction.get(0).equals("goto")) {
            double dist = CustomUtils.distance(AP.getSelfInfo().getLat(),
                    AP.getSelfInfo().getLon(),
                    AP.getShops().get(0).getShopLat(),
                    AP.getShops().get(0).getShopLon(),
                    'K');

            if (dist < 0.003) {
                System.out.println("residam residam");
            }

            System.out.println("im not master " + dist + " " + AP.getSelfInfo().getLastAction());
//        }
            LinkedList<Parameter> p = new LinkedList<>();
            p.add(new Identifier(AP.getShops().get(0).getShopLat() + ""));
            p.add(new Identifier(AP.getShops().get(0).getShopLon() + ""));

            return new Action("goto", p);
        }

        return new Action("skip");
    }
}

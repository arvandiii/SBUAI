package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;


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
        SharedData sharedData = SharedData.getSharedData();

        // if myLastAction is done pop
        // sharedData.getMyAction(myName)
        // return action

        if (getStepNumber() > 1) {

            
            double dist = CustomUtils.distance(AP.getSelfInfo().getLat(),
                    AP.getSelfInfo().getLon(),
                    AP.getShops().get(0).getShopLat(),
                    AP.getShops().get(0).getShopLon(),
                    'K');

            System.out.println("im not master " + dist + " " + AP.getSelfInfo().getLastAction());

            return new Action("goto", new Identifier("shop1"));
        }

        return new Action("skip");
    }
}

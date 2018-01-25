package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;
import massim.javaagents.percept.resourceNode;

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

        SharedData sharedData = SharedData.getSharedData();

        // TODO if resourceNode is seen save it in shared data
        if (AP.getResourceNodes().size() != 0) {
            for (resourceNode r : AP.getResourceNodes()) {
                sharedData.addNewResourceNode(r);
            }
        }

        Queue<ArrayList<String>> actions = sharedData.getMyActions(AP.getSelfInfo().getName());

        System.out.println(AP.getSelfInfo().getName() + "\t" + actions);
        if (actions == null || actions.size() == 0)
            return new Action("skip");


        // check if last action should remove from queue
        ArrayList<String> lastAction = actions.peek();
        String lastActionName = lastAction.get(0);

        switch (lastActionName) {
            case "goto":
                double dist = CustomUtils.distance(AP.getSelfInfo().getLat(),
                        AP.getSelfInfo().getLon(),
                        Double.parseDouble(lastAction.get(1)),
                        Double.parseDouble(lastAction.get(2)),
                        'K');
                if (dist < 0.003) {
                    actions.poll();
                }
                break;
            case "research":
                break;
            default:
                break;
        }


        if (actions.size() == 0)
            return new Action("skip");


        // do action
        ArrayList<String> nextAction = actions.peek();
        String nextActionName = nextAction.get(0);

        switch (nextActionName) {
            case "goto":
                // TODO if charge not enough recharge
                LinkedList<Parameter> p = new LinkedList<>();
                p.add(new Identifier(nextAction.get(1)));
                p.add(new Identifier(nextAction.get(2)));
                return new Action("goto", p);
            case "research":
                // TODO go to a place not visited
            default:
                return new Action("skip");
        }


    }
}

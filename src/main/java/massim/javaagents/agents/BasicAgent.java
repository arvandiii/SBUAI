package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;
import massim.javaagents.percept.chargingStation;
import massim.javaagents.percept.resourceNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class BasicAgent extends Agent {

    AgentPercepts AP = new AgentPercepts();

    Double[] researchCoordinates = new Double[2];

    double minLon = 2.26;//0.15
    double maxLon = 2.41;
    double minLat = 48.82;//0.08
    double maxLat = 48.90;
    double walkLat = 0.006;
    double walkLon = 0.012;


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

        if (getStepNumber() == 0) {
            sharedData.addMyRole(AP.getSelfInfo().getName(), AP.getSelfRole());
            researchCoordinates[0] = AP.getSelfInfo().getLat();
            researchCoordinates[1] = AP.getSelfInfo().getLon();
        }


        // if resourceNode is seen save it in shared data
        if (AP.getResourceNodes().size() != 0) {
            for (resourceNode r : AP.getResourceNodes()) {
                sharedData.addNewResourceNode(r);
            }
            System.out.println("!!!! RESOURCE NODE FOUND BY " + AP.getSelfInfo().getName() + " !!!!");
        }

        Queue<ArrayList<String>> actions = sharedData.getMyActions(AP.getSelfInfo().getName());

        System.out.println(AP.getSelfInfo().getName() + "\t" + actions);
        if (actions == null || actions.size() == 0) {
            return new Action("skip");
        }


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
            case "buy":
                if (AP.getSelfInfo().getLastActionResult().equals("successful")) {
                    actions.poll();
                }
                break;
            case "deliver_job":
                if (AP.getSelfInfo().getLastActionResult().equals("successful")) {
                    actions.poll();
                }
                if (AP.getSelfInfo().getLastActionResult().equals("useless")) {
                    actions.poll();
                }
                if (AP.getSelfInfo().getLastActionResult().equals("failed_location")) {
                    actions.poll();
                }
                break;
            case "gather":
                if (AP.getSelfInfo().getLastActionResult().equals("successful")) {
                    actions.poll();
                }
                break;
            case "charge":
                actions.poll();
                break;
            default:
                break;
        }


        if (actions.size() == 0) {
            return new Action("skip");
        }

        // handle next action
        ArrayList<String> nextAction = actions.peek();
        String nextActionName = nextAction.get(0);

        switch (nextActionName) {
            case "goto":

                chargingStation nearestChargingStation = null;
                double nearestChargingStationDist = Double.MAX_VALUE;
                for (chargingStation c : AP.getChargingStations()) {
                    double dist = CustomUtils.distance(AP.getSelfInfo().getLat(), AP.getSelfInfo().getLon()
                            , c.getLat(), c.getLon(), 'K');
                    if (dist < nearestChargingStationDist) {
                        nearestChargingStationDist = dist;
                        nearestChargingStation = c;
                    }
                }

                if (nearestChargingStationDist < 0.0003 &&
                        sharedData.getRole(AP.getSelfInfo().getName()).getBattery() > 10 + AP.getSelfInfo().getCharge()) {
                    return new Action("charge");
                }

                double disance = CustomUtils.distance(AP.getSelfInfo().getLat(), AP.getSelfInfo().getLon()
                        , Double.parseDouble(nextAction.get(1)), Double.parseDouble(nextAction.get(2)), 'K');
                double step = Math.ceil(disance / (sharedData.getRole(AP.getSelfInfo().getName()).getSpeed() * 0.2));


                System.out.println(AP.getSelfInfo().getName() + " enghad stepe dg monde ta beresam " + Math.ceil(step));
                System.out.println(AP.getSelfInfo().getName() + " enghad charge daram " + AP.getSelfInfo().getCharge());

                if (AP.getSelfInfo().getCharge() > (step * 10)) {
                    LinkedList<Parameter> p = new LinkedList<>();
                    p.add(new Identifier(nextAction.get(1)));
                    p.add(new Identifier(nextAction.get(2)));
                    return new Action("goto", p);
                } else {


                    double stepsToChargingStation = nearestChargingStationDist / (sharedData.getRole(AP.getSelfInfo().getName()).getSpeed() * 0.2);
                    if (AP.getSelfInfo().getCharge() > 10 &&
                            stepsToChargingStation <
                                    (sharedData.getRole(AP.getSelfInfo().getName()).getBattery() - AP.getSelfInfo().getCharge()) / 5 &&
                            AP.getSelfInfo().getCharge() > (stepsToChargingStation * 10)) {
                        if (nearestChargingStationDist < 0.0003) {
                            return new Action("charge");
                        }
                        LinkedList<Parameter> p = new LinkedList<>();
                        p.add(new Identifier(nearestChargingStation.getLat() + ""));
                        p.add(new Identifier(nearestChargingStation.getLon() + ""));
                        return new Action("goto", p);
                    } else {
                        return new Action("recharge");
                    }
                }

            case "buy":
                LinkedList<Parameter> pbuy = new LinkedList<>();
                pbuy.add(new Identifier(nextAction.get(1)));
                pbuy.add(new Identifier(nextAction.get(2)));
                return new Action("buy", pbuy);
            case "deliver_job":
                LinkedList<Parameter> pdeliver = new LinkedList<>();
                pdeliver.add(new Identifier(nextAction.get(1)));
                return new Action("deliver_job", pdeliver);
            case "gather":
                return new Action("gather");
            case "research":
                // TODO go to a place not visited

                chargingStation nearestChargingStationD = null;
                double nearestChargingStationDistD = Double.MAX_VALUE;
                for (chargingStation c : AP.getChargingStations()) {
                    double dist = CustomUtils.distance(AP.getSelfInfo().getLat(), AP.getSelfInfo().getLon()
                            , c.getLat(), c.getLon(), 'K');
                    if (dist < nearestChargingStationDistD) {
                        nearestChargingStationDistD = dist;
                        nearestChargingStationD = c;
                    }
                }
                if (nearestChargingStationDistD < 0.0003 &&
                        sharedData.getRole(AP.getSelfInfo().getName()).getBattery() > 10 + AP.getSelfInfo().getCharge()) {
                    return new Action("charge");
                }
                if (Math.floor(nearestChargingStationDistD) < 1 && AP.getSelfInfo().getCharge() <= 50) {
                    return new Action("goto", new Identifier(nearestChargingStationD.getLat() + ""),
                            new Identifier(nearestChargingStationD.getLon() + ""));
                }
                if (AP.getSelfInfo().getCharge() < 10)
                    return new Action("recharge");
                LinkedList<Parameter> parameters = new LinkedList<>();
                parameters.add(new Identifier(researchCoordinates[0].toString()));
                parameters.add(new Identifier(researchCoordinates[1].toString()));
                calculateNextResearch();
                return new Action("goto", parameters);
            default:
                return new Action("skip");
        }


    }

    private void calculateNextResearch() {

        Random r = new Random();

        if (researchCoordinates[0] == 0 && researchCoordinates[1] == 0) {
            if (r.nextBoolean()) {
                researchCoordinates[0] = minLat + (minLat + 0.01 - minLat) * r.nextDouble();
            } else {
                researchCoordinates[0] = maxLat + (maxLat - 0.01 - maxLat) * r.nextDouble();
            }
            if (r.nextBoolean()) {
                researchCoordinates[1] = minLon + (minLon + 0.01 - minLon) * r.nextDouble();
            } else {
                researchCoordinates[1] = maxLon + (maxLon - 0.01 - maxLon) * r.nextDouble();
            }
        } else {
            if (Math.abs(researchCoordinates[1] - AP.getSelfInfo().getLon()) < 0.003
                    && Math.abs(researchCoordinates[0] - AP.getSelfInfo().getLat()) < 0.003) {
                if (r.nextBoolean()) {
                    researchCoordinates[0] = minLat + (minLat + 0.01 - minLat) * r.nextDouble();
                } else {
                    researchCoordinates[0] = maxLat + (maxLat - 0.01 - maxLat) * r.nextDouble();
                }
                if (r.nextBoolean()) {
                    researchCoordinates[1] = minLon + (minLon + 0.01 - minLon) * r.nextDouble();
                } else {
                    researchCoordinates[1] = maxLon + (maxLon - 0.01 - maxLon) * r.nextDouble();
                }
            }
        }

    }


}

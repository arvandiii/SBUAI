package massim.javaagents.agents;

import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.percept.AgentPercepts;

import java.util.List;
import java.util.Vector;

/**
 * An abstract Java agent.
 */
public abstract class Agent {

    private String name;
    private MailService mailbox;
    private List<Percept> percepts = new Vector<>();
    ///***
    private int stepNumber;
    public void setStepNumber(int sn)
    {
        stepNumber = sn;
    }
    public int getStepNumber()
    {
        return stepNumber;
    }
    ///***
    /**
     * Constructor
     * @param name the agent's name
     * @param mailbox the mail facility
     */
    Agent(String name, MailService mailbox){
        this.name = name;
        this.mailbox = mailbox;
    }

    /**
     * Handles a percept.
     * This method is used only if the EIS is configured to handle percepts as notifications.
     * @param percept the percept to process
     */
    public abstract void handlePercept(Percept percept);

    /**
     * Called for each step.
     */
    public abstract eis.iilang.Action step();

    /**
     * @return the name of the agent
     */
    public String getName() {
        return name;
    }

    /**
     * Sends a percept as a message to the given agent.
     * The receiver agent may fetch the message the next time it is stepped.
     * @param message the message to deliver
     * @param receiver the receiving agent
     * @param sender the agent sending the message
     */
    protected void sendMessage(Percept message, String receiver, String sender){
        mailbox.sendMessage(message, receiver, sender);
    }

    /**
     * Broadcasts a message to the entire team.
     * @param message the message to broadcast
     * @param sender the agent sending the message
     */
    void broadcast(Percept message, String sender){
        mailbox.broadcast(message, sender);
    }

    /**
     * Called if another agent sent a message to this agent; so technically this is part of another agent's step method.
     *
     * @param message the message that was sent
     * @param sender name of the agent who sent the message
     */
    public abstract void handleMessage(Percept message, String sender);

    /**
     * Sets the percepts for this agent. Should only be called from the outside.
     * @param percepts the new percepts for this agent.
     */
    public void setPercepts(List<Percept> percepts) {
        this.percepts = percepts;
    }

    /**
     * Prints a message to std out prefixed with the agent's name.
     * @param message the message to say
     */
    void say(String message){
        System.out.println("[ " + name + " ]  " + message);
    }

    void makePerceptObjects(AgentPercepts AP) {
        List<Percept> percepts = this.getPercepts();
        AP.setPercepts(percepts);
        if (getStepNumber() == 0) {
            AP.initialize();
        } else {
            AP.stepPercept();
        }
        AP.getSelfInfo().setName(this.getName());
    }

    /**
     * Returns a list of this agent's percepts. Percepts are set by the scheduler
     * each time before the step() method is called.
     * Percepts are cleared before each step, so relevant information needs to be stored somewhere else
     * by the agent.
     * @return a list of all new percepts for the current step
     */
    List<Percept> getPercepts(){
        return percepts;
    }
}
